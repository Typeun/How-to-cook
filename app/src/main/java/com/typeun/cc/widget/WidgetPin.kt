package com.typeun.cc.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * 一键把「本日收藏夹」小组件钉到桌面（Android 8.0+ 支持）。
 * 系统弹确认框，用户同意后自动放置，不需要用户自己进小组件列表翻。
 */
object WidgetPin {

    fun isSupported(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        return supportedApi26(context)
    }

    /** @return true 表示已发起系统确认框 */
    fun request(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        return requestApi26(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun supportedApi26(context: Context): Boolean =
        AppWidgetManager.getInstance(context).isRequestPinAppWidgetSupported

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestApi26(context: Context): Boolean {
        val mgr = AppWidgetManager.getInstance(context)
        if (!mgr.isRequestPinAppWidgetSupported) return false
        val provider = ComponentName(context, FavoritesWidgetProvider::class.java)
        return mgr.requestPinAppWidget(provider, null, null)
    }
}
