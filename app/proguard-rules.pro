# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Room
#-keepclassmembers class * extends androidx.room.RoomDatabase {
#   *;
#}
#-keepclassmembers interface androidx.room.** {
#   *;
#}
-keepattributes Signature
-keepattributes *Annotation*

# ViewModel
#-keep class androidx.lifecycle.ViewModel { *; }

# View Binding and Data Binding
#-keep class * extends androidx.databinding.ViewDataBinding { *; }
#-keep class **BR { *; }
#-keep class * extends androidx.databinding.ViewDataBinding { *; }
#-keepclassmembers class * extends androidx.databinding.ViewDataBinding {
#    public static **[] getBindingAdapters(...);
#}
#-keep class * extends androidx.databinding.BaseObservable { *; }
#-keep class androidx.databinding.* { *; }

# Your own model classes
-keep class com.minimal.notes.model.** { *; }

# Keep Hilt annotations
#-keep class dagger.hilt.** { *; }
#-keep class javax.inject.** { *; }

# Keep Hilt-generated classes
#-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }

# Keep Hilt modules
#-keep @dagger.Module class * { *; }

# Keep injected classes
#-keepclassmembers,allowobfuscation class * {
#    @javax.inject.Inject <fields>;
#    @javax.inject.Inject <methods>;
#}
