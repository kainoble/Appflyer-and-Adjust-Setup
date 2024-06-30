#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#region Common
-keepattributes *Annotation*, Javadoc
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-keep public class com.android.installreferrer.** { *; }
#endregion

#region AppsFlyer
-keep class com.appsflyer.** { *; }
-keep class kotlin.jvm.internal.** { *; }
#endregion

#region Adjust
-keep class com.adjust.sdk.** { *; }
-keep class com.google.android.gms.common.ConnectionResult {
   int SUCCESS;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
   com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
   java.lang.String getId();
   boolean isLimitAdTrackingEnabled();
}
#endregion
