# -----------------------------------------------------------------------
# Kotlin Serialization
# -----------------------------------------------------------------------
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class **$$serializer { *; }
-keep,includedescriptorclasses class com.chknkv.**$$serializer { *; }
-keepclassmembers @kotlinx.serialization.Serializable class com.chknkv.** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# -----------------------------------------------------------------------
# CoreNetwork internal DTOs (RefreshRequest / RefreshResponse)
# -----------------------------------------------------------------------
-keep @kotlinx.serialization.Serializable class com.chknkv.corenetwork.** { *; }
-keepclassmembers @kotlinx.serialization.Serializable class com.chknkv.corenetwork.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}

# -----------------------------------------------------------------------
# Koin
# -----------------------------------------------------------------------
-keep class org.koin.** { *; }
-keep class com.chknkv.**.di.** { *; }

# -----------------------------------------------------------------------
# Coroutines
# -----------------------------------------------------------------------
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-keepclassmembernames class kotlinx.** { volatile <fields>; }

# -----------------------------------------------------------------------
# Napier
# -----------------------------------------------------------------------
-keep class io.github.aakira.napier.** { *; }

# -----------------------------------------------------------------------
# Ktor
# -----------------------------------------------------------------------
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { volatile <fields>; }

# -----------------------------------------------------------------------
# Compose
# -----------------------------------------------------------------------
-keep class androidx.compose.** { *; }

# -----------------------------------------------------------------------
# Anchor domain models & navigation
# -----------------------------------------------------------------------
-keep class com.chknkv.**.models.** { *; }
-keep class com.chknkv.**.navigation.** { *; }
