# Add project specific ProGuard rules here.
-keep class com.bumblebeemax.data.model.** { *; }
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }
