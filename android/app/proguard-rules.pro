# HUD-Link ProGuard Rules

# Keep Room entities
-keep class com.hudlink.core.storage.entity.** { *; }

# Keep data models for serialization
-keep class com.hudlink.core.data.model.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
