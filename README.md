# 程序员做饭指南 · Android

> 把 [HowToCook](https://github.com/Anduin2017/HowToCook) 的 **373 道家常菜谱**装进手机。
> 原生 Android（Kotlin + Jetpack Compose），Material Design 2，橙色主题，离线可用、无广告、不登录。

| | |
|---|---|
| 应用名 | 程序员做饭指南 |
| 包名 | `com.typeun.cc` |
| 版本 | 1.01（versionCode 2） |
| 系统要求 | Android 5.0（API 21）及以上 |
| 安装包 | [`dist/cc-1.01-release.apk`](dist/cc-1.01-release.apk)（9.9 MB） |
| 菜谱数据 | 373 道 / 10 大分类，随包离线内置 |

---

## 截图

<!-- 待补充：把 3~4 张手机截图放进 docs/screenshots/，然后把下面两行改成图片引用 -->

| 首页 | 详情页 | 侧拉栏 |
|---|---|---|
| _（待添加）_ | _（待添加）_ | _（待添加）_ |

---

## 它能做什么

**找菜**
- 顶部搜索栏，按 **菜名 / 分类 / 原料 / 描述** 实时过滤，输入即出结果
- 10 大分类：主食、荤菜、素菜、汤与粥、早餐、水产、甜品、饮料、半成品加工、酱料和其它材料
- 分类按**家常频率**重排过，最常做的排前面；抽屉与顶部 chips 双导航

**看菜**
- 详情页包含：难度、卡路里、原料清单、按份数自动换算的「计算公式」、分步操作、附加说明
- 圆形揭露转场（从点按位置扩散）、列表错峰淡入、涟漪反馈——手感和系统应用一致

**收藏**
- 长按任意卡片，或点详情页右上角 ♥，即可加入「本日收藏夹」，带震动反馈与提示条
- 侧拉栏有独立入口，随时翻今天想做的菜

**桌面小组件**
- 4×3 尺寸，展示前 4 道收藏，点条目**深链直达**该菜谱
- 侧拉栏「把收藏夹放到桌面」，一键添加（Android 8.0+；低版本请长按桌面手动添加）

**今日推荐**
- 首页顶部每天随机推荐 3 道**分类互不相同**的菜，同日固定、隔天更换
- 与下方普通列表之间用一条横线 + 约两倍间距分隔，视觉上明确分区

---

## 下载安装

**直接装**：到 [Releases](https://github.com/Typeun/How-to-cook/releases/latest) 下载 `cc-1.01-release.apk`（或本仓库内的 [`dist/cc-1.01-release.apk`](dist/cc-1.01-release.apk)）。

1. 手机下载 APK；
2. 点击安装，若提示「禁止安装未知来源应用」，在系统设置里对本浏览器/文件管理器放行；
3. 已校验：v1 + v2 签名、zipalign 通过、非 debuggable。

SHA-256：`3DCCAEBA95A4A062D9EDF4363D537C0A4A03AC35E5EC76DEBDF47C351F6F8BBE`

```bash
# 校验方式
sha256sum cc-1.01-release.apk
```

---

## 数据来源与声明（重要）

- **菜谱内容并非原创**。全部菜谱、原料、步骤、文案整理自开源项目 **[Anduin2017/HowToCook](https://github.com/Anduin2017/HowToCook)**（程序员做饭指南），该项目以 **The Unlicense** 协议发布，属于公有领域，可自由使用、修改与分发。
- 本仓库是它的 **Android 客户端**：重新设计了界面与交互，重写了数据解析与缓存，补齐了收藏、桌面小组件、每日推荐等功能；**未对原文进行实质性改写**。
- 应用内「关于」弹窗同样标注了来源与 GitHub 地址，文本可复制，欢迎传播原项目。
- 菜谱如有错误或想新增菜品，请优先向上游仓库提 PR —— 本仓库的 `assets/recipes.json` 由上游内容生成。

---

## 技术栈

| 层 | 选型 |
|---|---|
| 语言 | Kotlin 1.9.25 |
| UI | Jetpack Compose + Material 2（Compose BOM 2024.09.00） |
| 构建 | AGP 8.7.0 / Gradle 8.9 / JDK 17 |
| 数据 | 单文件 JSON（`assets/recipes.json`）+ kotlinx.serialization 1.6.3 |
| 持久化 | SharedPreferences（收藏） |
| 组件 | AppWidgetProvider + RemoteViews（桌面小组件） |
| 适配 | minSdk 21 / targetSdk 34 |

**没有任何网络请求**，不申请联网权限，全部数据在包里。

---

## 目录结构

```
.
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/recipes.json          # 373 道菜谱（约 667 KB）
│       ├── java/com/typeun/cc/
│       │   ├── MainActivity.kt          # 入口 + 小组件深链
│       │   ├── data/                    # 仓库层：JSON 解析缓存、收藏持久化
│       │   ├── model/Recipe.kt          # 数据模型
│       │   ├── ui/                      # 页面与组件（列表/详情/抽屉/弹窗/推荐）
│       │   └── widget/                  # 桌面小组件与「一键添加」
│       └── res/                         # 图标、主题、小组件布局
├── dist/cc-1.01-release.apk             # 已签名安装包
├── docs/                                # 截图等素材（待补充）
├── RELEASE_NOTES.md                     # 更新日志
├── build.gradle / settings.gradle
├── gradle.properties                    # 签名配置留空，需自备密钥
└── gradlew / gradlew.bat                # Gradle Wrapper（已内置，无需联网下载）
```

---

## 本地构建

**环境**：JDK 17 + Android SDK（compileSdk 34 / build-tools 34.0.0）。

```bash
# 1) 克隆
git clone https://github.com/Typeun/How-to-cook.git
cd How-to-cook

# 2) 配置 SDK 路径（或直接用 Android Studio 打开，自动生成 local.properties）
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# 3) 编译
./gradlew assembleDebug          # 调试包，输出 app/build/outputs/apk/debug/
./gradlew assembleRelease        # 正式包（需自备签名，见下）
```

**关于签名**：仓库**不包含**签名密钥（`.jks` 已在 `.gitignore` 中排除）。要出可安装的 release 包，自行生成并填写：

```bash
keytool -genkeypair -v -keystore my-release.jks -alias mykey \
        -keyalg RSA -keysize 2048 -validity 10950

./gradlew assembleRelease \
  -PCCING_STORE_FILE=/绝对路径/my-release.jks \
  -PCCING_STORE_PASSWORD=你的口令 \
  -PCCING_KEY_ALIAS=mykey \
  -PCCING_KEY_PASSWORD=你的口令
```

> ⚠️ Android 5.0 / 6.0 只认 **v1（JAR）签名**，构建脚本已强制开启 `enableV1Signing`，不要关掉，否则老机型会报「解析软件包出现问题」。

---

## 包名说明

`com.typeun.cc` 遵循 Android 的反写域名（reverse-DNS）约定：

| 段 | 含义 |
|---|---|
| `com` | 顶级域名段，包名必备起点 |
| `typeun` | 作者 GitHub 账号 |
| `cc` | **C**oder**C**ooking 的缩写 |

全部小写书写，符合 Java/Kotlin 包名惯例，也避免在大小写敏感的文件系统（Linux CI、Git）上出现目录冲突。包名一经对外发布不可更改——那等于变成另一个 App，老用户无法覆盖升级。

---

## 已知限制

- 桌面小组件「一键添加到桌面」依赖 Android 8.0+ 的 `requestPinAppWidget`，低版本会提示改为长按桌面添加。
- Android 8.0 起**图标外轮廓由桌面启动器决定**（原生桌面为圆形，MIUI 为圆角方形），应用无法强制指定；包内已同时提供圆形 `roundIcon` 与自适应图标。
- 未开启 R8 代码压缩（`minifyEnabled false`），优先保证运行稳定；`proguard-rules.pro` 里已备好 serialization 与小组件 Provider 的 keep 规则，将来开启可直接用。
- 菜谱数据是**快照**，不会自动跟随上游更新；需要新菜谱时重新生成 `recipes.json`。

---

## 更新日志

见 [RELEASE_NOTES.md](RELEASE_NOTES.md)。

---

## 路线图

<!-- 待补充：这里留给你自己写想法，示例先放着，不需要的直接删 -->

- [ ] 深色模式
- [ ] 按食材反查菜谱（冰箱里有什么做什么）
- [ ] 菜谱数据跟随上游自动更新
- [ ] 平板 / 折叠屏适配

---

## 致谢

- 菜谱内容：[Anduin2017/HowToCook](https://github.com/Anduin2017/HowToCook) 及全部贡献者
- 图标：自行绘制（橙色正圆 + 锅具几何造型）

## 许可

本仓库的**代码**部分遵循 [MIT License](LICENSE)：可自由使用、修改、分发、商用，保留版权与许可声明即可。

`app/src/main/assets/recipes.json` 中的**菜谱内容**来自 [HowToCook](https://github.com/Anduin2017/HowToCook)，遵循 **The Unlicense**（公有领域），不受上面的 MIT 条款约束。

简单说：代码随便拿去改，菜谱随便拿去用，两个都很宽松。

##此内容由AI生成##

