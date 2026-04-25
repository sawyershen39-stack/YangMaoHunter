# Add project specific ProGuard rules here.
-keep class com.yangmaolie.hunter.** { *; }
-dontwarn com.yangmaolie.hunter.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule
-keep enum com.bumptech.glide.*

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
