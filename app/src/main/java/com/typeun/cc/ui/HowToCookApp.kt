@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.typeun.cc.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.typeun.cc.data.FavoritesStore
import com.typeun.cc.data.RecipeRepository
import com.typeun.cc.model.Recipe
import com.typeun.cc.model.RecipeData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** 推荐区与普通列表之间的额外空隙（想更大就调这一个数） */
private val RECOMMEND_EXTRA_GAP = 6.dp

@Composable
fun HowToCookApp(
    initialRecipePath: String? = null,
    onInitialPathConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    var data by remember { mutableStateOf<RecipeData?>(null) }
    LaunchedEffect(Unit) { data = RecipeRepository.load(context) }

    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("全部") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var aboutOpen by remember { mutableStateOf(false) }
    var detailRecipe by remember { mutableStateOf<Recipe?>(null) }
    var revealTarget by remember { mutableStateOf(0f) }
    val reveal by animateFloatAsState(
        targetValue = revealTarget,
        animationSpec = tween(420, easing = FastOutSlowInEasing)
    )
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 收藏状态（读 Compose state，收藏变化会自动重组）
    val favoriteIds = FavoritesStore.ids

    val categories = remember(data) { orderCategories(data?.categories ?: emptyList()) }
    val allRecipes = remember(data) { data?.recipes ?: emptyList() }
    val counts = remember(allRecipes) { allRecipes.groupingBy { it.category }.eachCount() }
    val byPath = remember(allRecipes) { allRecipes.associateBy { it.path } }
    val favoriteRecipes = remember(byPath, favoriteIds) { favoriteIds.mapNotNull { byPath[it] } }

    val filtered = remember(allRecipes, favoriteRecipes, selectedCategory, query) {
        val q = query.trim()
        val isFavSection = selectedCategory == FAVORITES_SECTION
        val base = if (isFavSection) favoriteRecipes else allRecipes
        base.filter { r ->
            val catOk = isFavSection || selectedCategory == "全部" || r.category == selectedCategory
            catOk && (
                q.isEmpty()
                    || r.title.contains(q, ignoreCase = true)
                    || r.category.contains(q, ignoreCase = true)
                    || r.ingredients.any { it.contains(q, ignoreCase = true) }
                    || r.desc.contains(q, ignoreCase = true)
                )
        }
    }

    // 今日推荐：仅在「全部」且未搜索时出现，每天 3 道、分类互不相同
    val recommendations = remember(allRecipes, selectedCategory, query) {
        if (selectedCategory == "全部" && query.isBlank()) {
            dailyRecommendations(allRecipes, 3)
        } else {
            emptyList()
        }
    }
    val recPaths = remember(recommendations) { recommendations.map { it.path }.toSet() }
    val restRecipes = remember(filtered, recPaths) {
        if (recPaths.isEmpty()) filtered else filtered.filterNot { it.path in recPaths }
    }

    fun openDetail(r: Recipe) {
        detailRecipe = r
        revealTarget = 1f
    }

    fun closeDetail() {
        revealTarget = 0f
        scope.launch { delay(420); detailRecipe = null }
    }

    fun toggleFavorite(r: Recipe) {
        val added = FavoritesStore.toggle(context, r.path)
        scope.launch {
            snackbarHostState.showSnackbar(
                if (added) "已加入「本日收藏夹」· 共 ${FavoritesStore.count()} 道"
                else "已从「本日收藏夹」移出"
            )
        }
    }

    // 从桌面小组件点进来：直接打开对应菜谱
    LaunchedEffect(data, initialRecipePath) {
        val path = initialRecipePath ?: return@LaunchedEffect
        val loaded = data ?: return@LaunchedEffect
        loaded.recipes.firstOrNull { it.path == path }?.let { openDetail(it) }
        onInitialPathConsumed()
    }

    MaterialTheme { // 主题由 MainActivity 的 HowToCookTheme 提供，此处只是兜底
        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    categories = categories,
                    counts = counts,
                    total = allRecipes.size,
                    selected = selectedCategory,
                    favoritesCount = favoriteRecipes.size,
                    onSelect = { cat ->
                        selectedCategory = cat
                        scope.launch { drawerState.close() }
                    },
                    onSelectFavorites = {
                        selectedCategory = FAVORITES_SECTION
                        scope.launch { drawerState.close() }
                    },
                    onPinWidget = {
                        scope.launch { drawerState.close() }
                        requestPinWidget(context)
                    }
                )
            },
            content = {
                Box(Modifier.fillMaxSize()) {
                    Scaffold(
                        topBar = {
                            SearchTopBar(
                                query = query,
                                onQueryChange = { query = it },
                                onMenu = { scope.launch { drawerState.open() } },
                                onInfo = { aboutOpen = true }
                            )
                        },
                        floatingActionButton = {
                            RandomFab(onClick = {
                                if (allRecipes.isNotEmpty()) openDetail(allRecipes.random())
                            })
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { padding ->
                        Column(Modifier.fillMaxSize().padding(padding)) {
                            ChipRow(
                                categories = categories,
                                selected = selectedCategory,
                                onSelect = { selectedCategory = it }
                            )
                            if (filtered.isEmpty()) {
                                if (selectedCategory == FAVORITES_SECTION) {
                                    FavoritesEmptyState(
                                        onPinWidget = { requestPinWidget(context) }
                                    )
                                } else {
                                    EmptyState()
                                }
                            } else {
                                RecipeList(
                                    recipes = restRecipes,
                                    recommended = recommendations,
                                    favoriteIds = favoriteIds,
                                    onItemClick = { openDetail(it) },
                                    onItemLongClick = { toggleFavorite(it) }
                                )
                            }
                        }
                    }

                    // 详情：圆形揭露覆盖层（覆盖整个屏幕，含顶栏）
                    detailRecipe?.let { r ->
                        // 系统返回键：先关详情，而不是直接退出应用
                        BackHandler(enabled = true) { closeDetail() }
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colors.background)
                                .circularReveal(reveal)
                        ) {
                            DetailScreen(
                                recipe = r,
                                isFavorite = favoriteIds.contains(r.path),
                                onToggleFavorite = { toggleFavorite(r) },
                                onBack = { closeDetail() }
                            )
                        }
                    }

                    if (aboutOpen) {
                        AboutDialog(
                            onDismiss = { aboutOpen = false },
                            onCopied = {
                                scope.launch { snackbarHostState.showSnackbar("已复制仓库地址") }
                            }
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onMenu: () -> Unit,
    onInfo: () -> Unit
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        navigationIcon = {
            IconButton(onClick = onMenu) {
                Icon(Icons.Default.Menu, contentDescription = "分类菜单")
            }
        },
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("搜索菜谱，如「西红柿炒鸡蛋」", color = Color.White.copy(alpha = 0.75f)) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = Color.Transparent,
                    cursorColor = Color.White,
                    leadingIconColor = Color.White,
                    trailingIconColor = Color.White,
                    placeholderColor = Color.White.copy(alpha = 0.75f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        actions = {
            IconButton(onClick = onInfo) {
                Icon(Icons.Default.Info, contentDescription = "来源与版权说明")
            }
        }
    )
}

@Composable
private fun ChipRow(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    val items = listOf("全部") + categories
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = items, key = { it }) { cat ->
            val isSel = cat == selected
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (isSel) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                border = BorderStroke(1.5.dp, if (isSel) MaterialTheme.colors.primary else Color(0xFFECECEC)),
                modifier = Modifier.combinedClickable(onClick = { onSelect(cat) })
            ) {
                Text(
                    text = cat,
                    color = if (isSel) Color.White else Color(0xFF757575),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
private fun RecipeList(
    recipes: List<Recipe>,
    recommended: List<Recipe>,
    favoriteIds: List<String>,
    onItemClick: (Recipe) -> Unit,
    onItemLongClick: (Recipe) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (recommended.isNotEmpty()) {
            item(key = "__rec_header__") { RecommendHeader() }
            items(items = recommended, key = { "rec_" + it.path }) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    isFavorite = favoriteIds.contains(recipe.path),
                    recommended = true,
                    onClick = { onItemClick(recipe) },
                    onLongClick = { onItemLongClick(recipe) }
                )
            }
            // 第 3 个（推荐）与第 4 个（普通）之间：一条横线 + 约两倍间距的大空隙
            item(key = "__rec_separator__") { RecommendSeparator() }
        }
        items(items = recipes, key = { it.path }) { recipe ->
            RecipeCard(
                recipe = recipe,
                isFavorite = favoriteIds.contains(recipe.path),
                recommended = false,
                onClick = { onItemClick(recipe) },
                onLongClick = { onItemLongClick(recipe) }
            )
        }
    }
}

@Composable
private fun RecommendHeader() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Star,
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            "今日推荐",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )
        Spacer(Modifier.width(8.dp))
        Text("每天 3 道 · 分类各不相同", fontSize = 11.5.sp, color = Color(0xFF9E9E9E))
    }
}

/**
 * 推荐区与普通列表的分隔：
 * 横线本身 + 上下留白，加上 LazyColumn 的 12dp 间距，
 * 3 号与 4 号卡片之间实际空隙约 37dp（普通卡片间是 12dp）。
 */
@Composable
private fun RecommendSeparator() {
    Column(Modifier.fillMaxWidth()) {
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = RECOMMEND_EXTRA_GAP)
        )
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    isFavorite: Boolean,
    recommended: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(280)) +
            slideInVertically(animationSpec = tween(280)) { it / 10 }
    ) {
        Card(
            elevation = if (recommended) 3.dp else 2.dp,
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        // 长按：轻微震动 + 加入/移出「本日收藏夹」
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    }
                )
        ) {
            Column(Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        recipe.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (isFavorite) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "已在收藏夹",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    val color = categoryColor(recipe.category)
                    Surface(color = color, shape = RoundedCornerShape(11.dp)) {
                        Text(
                            recipe.category,
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }
                if (recipe.desc.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        recipe.desc,
                        fontSize = 13.sp,
                        color = Color(0xFF757575),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (recommended) {
                        Surface(
                            color = MaterialTheme.colors.primary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "推荐",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (recipe.difficulty != null)
                        Text("难度 ${recipe.difficulty}", fontSize = 12.sp, color = Color(0xFF9E9E9E))
                    if (recipe.calories != null)
                        Text(recipe.calories, fontSize = 12.sp, color = Color(0xFF9E9E9E))
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Search, null, modifier = Modifier.size(64.dp), tint = Color(0xFFBDBDBD))
            Spacer(Modifier.height(8.dp))
            Text("没有找到匹配的菜谱", fontSize = 16.sp, color = Color(0xFF757575), fontWeight = FontWeight.Medium)
            Text("换个关键词或分类试试", fontSize = 13.sp, color = Color(0xFF9E9E9E))
        }
    }
}

@Composable
private fun FavoritesEmptyState(onPinWidget: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.FavoriteBorder,
                null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFBDBDBD)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "本日收藏夹还是空的",
                fontSize = 16.sp,
                color = Color(0xFF757575),
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(2.dp))
            Text("长按任意菜谱卡片，就能把它加进来", fontSize = 13.sp, color = Color(0xFF9E9E9E))
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = onPinWidget) {
                Text("把收藏夹放到桌面", color = MaterialTheme.colors.primary, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun RandomFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Icon(Icons.Default.Shuffle, null, tint = Color.White)
    }
}
