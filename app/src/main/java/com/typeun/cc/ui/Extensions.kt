package com.typeun.cc.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.hypot

/**
 * 圆形揭露用的裁剪形状：以内容中心为圆心，半径随 progress 从 0 增长到「覆盖整个内容」。
 *
 * 关键点：progress 到 1 时半径已超过半对角线，整块内容都在圆内，等价于没有裁剪。
 */
private class CircleRevealShape(private val progress: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val p = progress.coerceIn(0f, 1f)
        // 半对角线 * 1.05，保证 p=1 时四个角也在圆内（不留白角）
        val maxRadius = (hypot(size.width, size.height) / 2f) * 1.05f
        val radius = maxRadius * p
        val path = Path().apply {
            addOval(Rect(center = Offset(size.width / 2f, size.height / 2f), radius = radius))
        }
        return Outline.Generic(path)
    }
}

/**
 * 圆形揭露过渡：progress(0→1) 控制一个从屏幕中心扩散的圆形裁剪区域。
 *
 * 与旧实现的区别：旧版用 graphicsLayer 的 shape 裁剪 + 整体缩放，
 * 但 scale 回到 1.0 后 shape 依然生效，导致详情页被永久裁成椭圆。
 * 现在 progress >= 1 时直接不施加裁剪，终态就是完整页面。
 */
fun Modifier.circularReveal(progress: Float): Modifier {
    val p = progress.coerceIn(0f, 1f)
    return if (p >= 1f) this else this.clip(CircleRevealShape(p))
}
