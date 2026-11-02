package com.typeun.cc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeData(
    val generated: String = "",
    val categories: List<String> = emptyList(),
    val recipes: List<Recipe> = emptyList()
)

@Serializable
data class Recipe(
    val title: String = "",
    val category: String = "",
    val path: String = "",
    val desc: String = "",
    val difficulty: String? = null,
    val calories: String? = null,
    val ingredients: List<String> = emptyList(),
    val calc: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val extra: String = ""
)
