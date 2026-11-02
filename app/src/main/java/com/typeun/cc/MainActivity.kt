package com.typeun.cc

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.typeun.cc.data.FavoritesStore
import com.typeun.cc.ui.HowToCookApp
import com.typeun.cc.ui.theme.HowToCookTheme
import com.typeun.cc.widget.FavoritesWidgetProvider

class MainActivity : ComponentActivity() {

    /** 小组件点进来要直接打开的菜谱（path），消费后清空 */
    private var pendingRecipePath by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FavoritesStore.init(this)
        // 应用启动时同步一次小组件（避免系统缓存了旧数据）
        FavoritesWidgetProvider.refreshAll(this)
        pendingRecipePath = intent?.getStringExtra(EXTRA_RECIPE_PATH)

        setContent {
            HowToCookTheme {
                Surface(color = MaterialTheme.colors.background) {
                    HowToCookApp(
                        initialRecipePath = pendingRecipePath,
                        onInitialPathConsumed = { pendingRecipePath = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingRecipePath = intent.getStringExtra(EXTRA_RECIPE_PATH)
    }

    companion object {
        const val EXTRA_RECIPE_PATH = "extra_recipe_path"
    }
}
