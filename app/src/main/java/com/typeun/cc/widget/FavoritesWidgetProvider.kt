package com.typeun.cc.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import com.typeun.cc.MainActivity
import com.typeun.cc.R
import com.typeun.cc.data.FavoritesStore
import com.typeun.cc.data.RecipeRepository
import com.typeun.cc.model.Recipe
import com.typeun.cc.ui.categoryColor

/**
 * 「本日收藏夹」桌面小组件。
 *
 * 展示收藏的前 4 道菜；点击条目直接深链打开对应菜谱详情。
 * 收藏变化时由 FavoritesStore 主动调 refreshAll() 刷新，所以 updatePeriodMillis = 0。
 */
class FavoritesWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        FavoritesStore.init(context)
        // 一次 update 里只解析一次数据，多个小组件共用
        val favorites = favoriteRecipes(context)
        appWidgetIds.forEach { render(context, appWidgetManager, it, favorites) }
    }

    companion object {
        private const val MAX_ITEMS = 4

        private val ROW_IDS = intArrayOf(
            R.id.item_row_1, R.id.item_row_2, R.id.item_row_3, R.id.item_row_4
        )
        private val TITLE_IDS = intArrayOf(
            R.id.item_title_1, R.id.item_title_2, R.id.item_title_3, R.id.item_title_4
        )
        private val CAT_IDS = intArrayOf(
            R.id.item_cat_1, R.id.item_cat_2, R.id.item_cat_3, R.id.item_cat_4
        )

        /** 收藏变化 / 应用启动时调用，刷新所有已放到桌面的小组件 */
        fun refreshAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(
                ComponentName(context, FavoritesWidgetProvider::class.java)
            )
            if (ids.isEmpty()) return
            FavoritesStore.init(context)
            val favorites = favoriteRecipes(context)
            ids.forEach { render(context, mgr, it, favorites) }
        }

        private fun render(
            context: Context,
            mgr: AppWidgetManager,
            widgetId: Int,
            favorites: List<Recipe>
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_favorites)

            views.setTextViewText(R.id.widget_count, "${favorites.size} 道")
            views.setViewVisibility(
                R.id.widget_empty,
                if (favorites.isEmpty()) View.VISIBLE else View.GONE
            )
            views.setOnClickPendingIntent(R.id.widget_header, openAppIntent(context, widgetId))

            val shown = favorites.take(MAX_ITEMS)
            ROW_IDS.forEachIndexed { i, rowId ->
                val recipe = shown.getOrNull(i)
                if (recipe == null) {
                    views.setViewVisibility(rowId, View.GONE)
                } else {
                    views.setViewVisibility(rowId, View.VISIBLE)
                    views.setTextViewText(TITLE_IDS[i], recipe.title)
                    views.setTextViewText(CAT_IDS[i], recipe.category)
                    views.setTextColor(CAT_IDS[i], categoryColor(recipe.category).toArgb())
                    views.setOnClickPendingIntent(
                        rowId, openRecipeIntent(context, widgetId, i, recipe.path)
                    )
                }
            }

            mgr.updateAppWidget(widgetId, views)
        }

        /** 收藏 path → 菜谱实体（保持收藏顺序） */
        private fun favoriteRecipes(context: Context): List<Recipe> {
            val ids = FavoritesStore.ids
            if (ids.isEmpty()) return emptyList()
            val data = runCatching { RecipeRepository.load(context) }.getOrNull() ?: return emptyList()
            val byPath = data.recipes.associateBy { it.path }
            return ids.mapNotNull { byPath[it] }
        }

        private fun openAppIntent(context: Context, widgetId: Int): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            return PendingIntent.getActivity(
                context, widgetId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun openRecipeIntent(
            context: Context,
            widgetId: Int,
            index: Int,
            path: String
        ): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(MainActivity.EXTRA_RECIPE_PATH, path)
            }
            return PendingIntent.getActivity(
                context, widgetId * 10 + index, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
