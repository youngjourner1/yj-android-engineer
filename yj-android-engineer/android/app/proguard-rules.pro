# Proguard rules for YJ Webcam

-keep class org.webrtc.** { *; }
-dontwarn org.webrtc.**

-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

-keep class com.yj.webcam.domain.model.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
