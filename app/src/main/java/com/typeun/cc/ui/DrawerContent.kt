package com.typeun.cc.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.typeun.cc.widget.WidgetPin

private const val GITHUB_URL = "https://github.com/Anduin2017/HowToCook"

@Composable
fun DrawerContent(
    categories: List<String>,
    counts: Map<String, Int>,
    total: Int,
    selected: String,
    favoritesCount: Int,
    onSelect: (String) -> Unit,
    onSelectFavorites: () -> Unit,
    onPinWidget: () -> Unit
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().background(MaterialTheme.colors.surface)) {
        // 纯橙头部（无渐变）
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)
                .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp)
        ) {
            Column {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.22f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🍳", fontSize = 22.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text("程序员做饭指南", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "共 $total 道菜 · 10 大分类",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 12.5.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    GITHUB_URL.replace("https://", ""),
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .clickable { openGitHub(context) }
                )
            }
        }
        // 分类列表
        LazyColumn(Modifier.fillMaxSize().padding(vertical = 8.dp)) {
            item {
                DrawerItem("全部", total, selected == "全部", categoryColor("全部")) { onSelect("全部") }
            }
            item {
                // 本日收藏夹（置顶，方便随手打开）
                DrawerItem(
                    name = FAVORITES_SECTION,
                    count = favoritesCount,
                    selected = selected == FAVORITES_SECTION,
                    color = MaterialTheme.colors.primary,
                    icon = Icons.Default.Favorite,
                    onClick = onSelectFavorites
                )
            }
            item {
                Divider(
                    color = Color(0xFFEEEEEE),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
            items(categories) { cat ->
                DrawerItem(cat, counts[cat] ?: 0, selected == cat, categoryColor(cat)) { onSelect(cat) }
            }
            item {
                Divider(
                    color = Color(0xFFEEEEEE),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
            item {
                DrawerItem(
                    name = "把收藏夹放到桌面",
                    count = 0,
                    selected = false,
                    color = Color(0xFF9E9E9E),
                    icon = Icons.Outlined.Widgets,
                    showCount = false
                ) { onPinWidget() }
            }
            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

private fun openGitHub(context: Context) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL)))
    }
}

@Composable
private fun DrawerItem(
    name: String,
    count: Int,
    selected: Boolean,
    color: Color,
    icon: ImageVector? = null,
    showCount: Boolean = true,
    onClick: () -> Unit
) {
    val bg = if (selected) MaterialTheme.colors.primary.copy(alpha = 0.08f) else Color.Transparent
    Row(
        Modifier
            .fillMaxWidth()
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        } else {
            Surface(shape = CircleShape, color = color, modifier = Modifier.size(11.dp)) {}
        }
        Spacer(Modifier.width(14.dp))
        Text(
            name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (showCount) {
            Text("$count", fontSize = 12.5.sp, color = Color(0xFF9E9E9E), fontWeight = FontWeight.SemiBold)
        }
    }
}

/** 侧拉栏里点「把收藏夹放到桌面」的入口逻辑（抽出来便于 Toast 提示） */
fun requestPinWidget(context: Context) {
    val ok = WidgetPin.request(context)
    if (!ok) {
        Toast.makeText(context, "当前系统不支持一键添加，请长按桌面 → 小组件 → 本日收藏夹", Toast.LENGTH_LONG).show()
    }
}
