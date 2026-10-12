# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Gson model classes for reflection during OpenLibrary parsing
-keep class com.tf.clasificacioncutter.data.model.** { *; }
-keep class com.tf.clasificacioncutter.data.model.openlibrary.** { *; }

# Keep Room entities and database classes
-keep class com.tf.clasificacioncutter.data.** { *; }
-keepattributes *Annotation*,Signature,EnclosingMethod

# Keep ML Kit and CameraX
-keep class com.google.mlkit.** { *; }
-keep class androidx.camera.** { *; }
