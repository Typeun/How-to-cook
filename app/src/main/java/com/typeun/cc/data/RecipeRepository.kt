package com.typeun.cc.data

import android.content.Context
import com.typeun.cc.model.RecipeData
import kotlinx.serialization.json.Json
import java.nio.charset.Charset

object RecipeRepository {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    /** 菜谱来自 assets，内容不会变，解析一次缓存起来（小组件刷新时不用反复解析 667KB JSON） */
    @Volatile
    private var cached: RecipeData? = null

    fun load(context: Context): RecipeData {
        cached?.let { return it }
        val text = context.assets.open("recipes.json")
            .bufferedReader(Charset.forName("UTF-8"))
            .use { it.readText() }
        val parsed = json.decodeFromString(RecipeData.serializer(), text)
        cached = parsed
        return parsed
    }
}
