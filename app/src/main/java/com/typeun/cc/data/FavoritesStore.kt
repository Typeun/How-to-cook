package com.typeun.cc.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.typeun.cc.widget.FavoritesWidgetProvider

/**
 * 「本日收藏夹」的本地存储。
 *
 * 用菜谱的 path 作为唯一 id（373 道菜里 path 唯一且稳定），
 * 存在 SharedPreferences 里，顺序 = 加入顺序，最新加入的排最前。
 *
 * ids 是 Compose 的 mutableStateOf，UI 读它即可自动重组。
 */
object FavoritesStore {

    private const val PREF_NAME = "howtocook_favorites"
    private const val KEY_IDS = "ids"

    private var prefs: SharedPreferences? = null

    /** 收藏的菜谱 path 列表（最新在前） */
    var ids by mutableStateOf<List<String>>(emptyList())
        private set

    fun init(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs = p
        ids = p.getString(KEY_IDS, null)
            ?.split('\n')
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }

    fun contains(id: String): Boolean = ids.contains(id)

    fun count(): Int = ids.size

    /**
     * 加入 / 移出收藏。
     * @return true 表示这次是「加入」，false 表示「移出」
     */
    fun toggle(context: Context, id: String): Boolean {
        if (id.isBlank()) return false
        val added = !ids.contains(id)
        ids = if (added) listOf(id) + ids.filter { it != id } else ids.filter { it != id }
        prefs?.edit()?.putString(KEY_IDS, ids.joinToString("\n"))?.apply()
        // 收藏变化立刻刷新桌面小组件
        FavoritesWidgetProvider.refreshAll(context)
        return added
    }

    fun clear(context: Context) {
        ids = emptyList()
        prefs?.edit()?.remove(KEY_IDS)?.apply()
        FavoritesWidgetProvider.refreshAll(context)
    }
}
