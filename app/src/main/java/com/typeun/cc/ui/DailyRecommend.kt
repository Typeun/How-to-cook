package com.typeun.cc.ui

import com.typeun.cc.model.Recipe
import kotlin.random.Random

/** 侧拉栏里「本日收藏夹」这个伪分类的名字 */
const val FAVORITES_SECTION = "本日收藏夹"

/**
 * 当天种子：用「天」为粒度生成随机种子。
 * 同一天多次进入应用 → 推荐同一批；换一天 → 自动换一批。
 * （不用 java.time，避免 minSdk 24 的 API 26 限制）
 */
private fun todaySeed(): Int = (System.currentTimeMillis() / 86_400_000L).toInt()

/**
 * 今日推荐：随机挑 [count] 道，且**分类互不相同**。
 *
 * 做法：先把分类洗牌取前 count 个，再从每个分类里随机取一道，
 * 这样天然保证「不同种类」。
 */
fun dailyRecommendations(recipes: List<Recipe>, count: Int = 3): List<Recipe> {
    if (recipes.isEmpty()) return emptyList()
    val byCategory = recipes.filter { it.category.isNotBlank() }.groupBy { it.category }
    if (byCategory.isEmpty()) return recipes.take(count)

    val rnd = Random(todaySeed())
    val pickedCategories = byCategory.keys.shuffled(rnd).take(count)
    return pickedCategories.mapNotNull { cat ->
        val list = byCategory[cat].orEmpty()
        if (list.isEmpty()) null else list[rnd.nextInt(list.size)]
    }
}
