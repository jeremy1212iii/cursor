# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep ML Kit classes
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep CameraX classes
-keep class androidx.camera.** { *; }

# Keep pose detection classes
-keep class com.google.mlkit.vision.pose.** { *; }
