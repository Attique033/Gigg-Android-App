-keep class com.tapjoy.** { *; }
-keep class com.moat.** { *; }
-keepattributes JavascriptInterface
-keepattributes *Annotation*
-keep class * extends java.util.ListResourceBundle {
protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
@com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
public static final ** CREATOR;
}
-keep class com.google.android.gms.ads.identifier.** { *; }
-dontwarn com.tapjoy.**
-keep class com.chartboost.** { *; }
-keep class com.ayetstudios.publishersdk.messages.** {*;}
-keep public class com.ayetstudios.publishersdk.AyetSdk
-keepclassmembers class com.ayetstudios.publishersdk.AyetSdk {
   public *;
}
-keep public interface com.ayetstudios.publishersdk.interfaces.UserBalanceCallback { *; }
-keep public interface com.ayetstudios.publishersdk.interfaces.DeductUserBalanceCallback { *; }

-keep class com.ayetstudios.publishersdk.models.VastTagReqData { *; }

-dontwarn com.google.**
-dontwarn com.android.**
-dontwarn android.webkit.**
-dontwarn com.unity3d.player.UnityPlayer
-keepattributes *Annotation*, Exceptions, Signature, Deprecated, SourceFile, SourceDir, LineNumberTable, LocalVariableTable, LocalVariableTypeTable, Synthetic, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations, AnnotationDefault, InnerClasses

-keep class com.google.** { *; }
-keep class com.android.** { *; }

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class * extends android.webkit.WebChromeClient { *; }
-dontwarn im.delight.android.webview.**