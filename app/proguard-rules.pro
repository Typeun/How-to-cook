# 本工程未开启 R8 代码压缩（minifyEnabled false），
# 以下规则留作将来开启混淆时的参考，当前不参与构建。

# kotlinx.serialization：保留 @Serializable 类及其生成的序列化器
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.typeun.cc.**$$serializer { *; }
-keepclassmembers class com.typeun.cc.** {
    *** Companion;
}
-keepclasseswithmembers class com.typeun.cc.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# 小组件 Provider 由系统按类名实例化，不能混淆/移除
-keep class com.typeun.cc.widget.FavoritesWidgetProvider { *; }
