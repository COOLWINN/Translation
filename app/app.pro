-injars 'D:\youdao\fanyi\raw\YoudaoBase-raw.jar'
-outjars 'D:\youdao\fanyi\ok\YoudaoBase.jar'

-libraryjars 'D:\software\Androidsdk\platforms\android-23\android.jar'
-libraryjars 'D:\software\Androidsdk\platforms\android-23\optional\org.apache.http.legacy.jar'
-libraryjars 'D:\software\Androidsdk\extras\android\support\v7\appcompat'

-dontoptimize
-allowaccessmodification
-printmapping 'D:\youdao\fanyi\mapping\appmapping.txt'
-repackageclasses 'com.youdao.sdk.app.other'
-keepattributes SourceFile,LineNumberTable,*Annotation*,InnerClasses
-renamesourcefileattribute SourceFile
-dontpreverify
-dontnote com.android.vending.licensing.ILicensingService
-dontwarn android.support.**
-keepattributes *JavascriptInterface* 

-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-keep class android.net.http.** { *; }
-dontwarn android.net.http.**
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

# Preserve all fundamental application classes.
-keep public class * extends android.app.Activity

-keep public class * extends android.app.Application

-keep public class * extends android.app.Service

-keep public class * extends android.content.BroadcastReceiver

-keep public class * extends android.content.ContentProvider

# Preserve all View implementations, their special context constructors, and
# their setters.
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context,android.util.AttributeSet);
    public <init>(android.content.Context,android.util.AttributeSet,int);
    public void set*(...);
}

# Preserve all classes that have special context constructors, and the
# constructors themselves.
-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}

# Preserve all classes that have special context constructors, and the
# constructors themselves.
-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}

# Preserve the special fields of all Parcelable implementations.
-keepclassmembers class * extends android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Preserve the required interface from the License Verification Library
# (but don't nag the developer if the library is not used at all).
-keep public interface  com.android.vending.licensing.ILicensingService

# Preserve the special static methods that are required in all enumeration
# classes.
-keepclassmembers class * extends java.lang.Enum {
    *;
}
-keepclassmembers enum *  {
    *;
}

# Also keep - Serialization code. Keep all fields and methods that are used for
# serialization.
-keepclassmembers class * extends java.io.Serializable {
   *;
}

-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

-keep class com.youdao.sdk.ydtranslate.TranslateSdk{
*;
} 

-keep class com.youdao.sdk.ydtranslate.TranslateErrorCode{
*;
} 

-keep public class com.youdao.sdk.app.YouDaoApplication {
*;
}

-keep public class com.youdao.sdk.app.LanguageOcrTranslate {
*;
}

-keep public class com.youdao.sdk.ydtranslate.Md5Util {
*;
}

-keep public class com.youdao.sdk.common.Constants{
*;
}

-keep public class com.youdao.sdk.common.AndroidUtils{
*;
}

-keep public class com.youdao.sdk.app.Language {
*;
}
-keep public class com.youdao.sdk.app.LanguageUtils {
*;
}
-keep public class com.youdao.sdk.app.Utils {
*;
}
-keep public class com.youdao.sdk.ydtranslate.Translate {
*;
}

-keep public class com.youdao.sdk.ydtranslate.TranslateListener {
*;
}

-keep public class com.youdao.sdk.ydtranslate.TranslateParameters {
*;
}

-keep public class com.youdao.sdk.ydtranslate.TranslateParameters$Builder {
*;
}

-keep public class com.youdao.sdk.ydtranslate.WebExplain {
*;
}

-keep class com.youdao.sdk.app.Stats {
*;
}

-keep class com.youdao.sdk.app.Auth{
*;
}

-keep class com.youdao.sdk.app.WordHelper {
*;
}

-keep class com.youdao.sdk.common.DownloadTask {
*;
}

-keep class com.youdao.sdk.common.DownloadTask$DownloadTaskListener {
*;
}

-keep class com.youdao.sdk.app.HttpHelper {
*;
}

-keep class com.youdao.sdk.app.HttpHelper$HttpJsonListener {
*;
}


-keep class com.youdao.sdk.app.HttpErrorCode{
*;
}

-keep class com.youdao.sdk.common.util.AsyncTasks {
*;
}

-keep class com.youdao.sdk.common.YDUrlGenerator {
*;
}

-keep class com.youdao.sdk.common.HttpResponses {
*;
}

-keep class com.youdao.sdk.common.DownloadResponse {
*;
}

-keep class com.youdao.sdk.common.YouDaoLog {
*;
}

-keep class com.youdao.sdk.app.JsonHelper {
*;
}

-keep class com.youdao.sdk.app.EncryptHelper {
*;
}

-keep class com.youdao.sdk.common.HttpClient{
*;
}

-keep class com.youdao.sdk.common.util.Streams{
*;
}

-keep class com.youdao.sdk.common.YoudaoParams{
*;
}
-keepattributes Signature