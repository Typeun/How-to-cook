package com.typeun.cc.ui

import androidx.compose.ui.graphics.Color

// 家常优先排序：最常做的往前放，酱料/半成品/饮料/甜品靠后
val CATEGORY_ORDER = listOf(
    "主食", "荤菜", "素菜", "汤与粥", "早餐", "水产",
    "甜品", "饮料", "半成品加工", "酱料和其它材料"
)

private val CATEGORY_COLOR_MAP = mapOf(
    "素菜" to 0xFF43A047,
    "荤菜" to 0xFFE53935,
    "水产" to 0xFF1E88E5,
    "早餐" to 0xFFFB8C00,
    "主食" to 0xFF8E24AA,
    "半成品加工" to 0xFF00ACC1,
    "汤与粥" to 0xFF6D4C41,
    "饮料" to 0xFF00897B,
    "酱料和其它材料" to 0xFFF4511E,
    "甜品" to 0xFFD81B60,
    "全部" to 0xFFF57C00
)

fun categoryColor(name: String): Color =
    Color(CATEGORY_COLOR_MAP[name] ?: 0xFF9E9E9E)

fun orderCategories(cats: List<String>): List<String> {
    return cats.sortedBy { name ->
        val i = CATEGORY_ORDER.indexOf(name)
        if (i < 0) Int.MAX_VALUE else i
    }
}
