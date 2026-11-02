package com.typeun.cc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.typeun.cc.model.Recipe

@Composable
fun DetailScreen(
    recipe: Recipe,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit
) {
    val color = categoryColor(recipe.category)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
        TopAppBar(
            title = { Text(recipe.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
            },
            actions = {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "从本日收藏夹移出" else "加入本日收藏夹"
                    )
                }
            },
            backgroundColor = color,
            contentColor = Color.White
        )
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 头部渐变（按分类色，仅此处用渐变）
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(color, shade(color, -0.22f))))
                    .padding(20.dp)
            ) {
                Column {
                    Text(recipe.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TagChip(recipe.category)
                        if (recipe.difficulty != null) TagChip("难度 ${recipe.difficulty}")
                        if (recipe.calories != null) TagChip(recipe.calories)
                    }
                }
            }

            Section(title = "必备原料和工具") {
                if (recipe.ingredients.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recipe.ingredients.forEach { ing ->
                            Surface(
                                color = Color(0xFFFAFAFA),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFECECEC))
                            ) {
                                Text(ing, fontSize = 13.5.sp, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp))
                            }
                        }
                    }
                } else {
                    Text("暂无原料信息", color = Color(0xFF9E9E9E), fontSize = 13.sp)
                }
            }

            if (recipe.calc.isNotEmpty()) {
                Section(title = "计算（按份数配方）") {
                    Surface(color = Color(0xFF263238), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.padding(14.dp)) {
                            recipe.calc.forEach { line ->
                                Text(
                                    line,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    color = Color(0xFFECEFF1),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Section(title = "操作步骤") {
                if (recipe.steps.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        recipe.steps.forEachIndexed { i, step ->
                            Row(verticalAlignment = Alignment.Top) {
                                Surface(shape = CircleShape, color = color, modifier = Modifier.size(26.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("${i + 1}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(step, fontSize = 14.5.sp, lineHeight = 22.sp, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    Text("暂无步骤信息", color = Color(0xFF9E9E9E), fontSize = 13.sp)
                }
            }

            if (recipe.extra.isNotEmpty()) {
                Section(title = "附加内容") {
                    Text(recipe.extra, fontSize = 14.sp, lineHeight = 22.sp, color = Color(0xFF757575))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TagChip(text: String) {
    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
        Text(text, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(14.dp),
        elevation = 1.dp,
        modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primaryVariant)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

// 颜色加深（percent 取负数表示变暗）
fun shade(color: Color, percent: Float): Color {
    val f = 1f + percent
    return Color(
        red = (color.red * f).coerceIn(0f, 1f),
        green = (color.green * f).coerceIn(0f, 1f),
        blue = (color.blue * f).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}
