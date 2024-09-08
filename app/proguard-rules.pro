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

# ------------------------------基本指令区---------------------------------
-optimizationpasses 5 #指定压缩级别
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/* #混淆时采用的算法
-verbose #打印混淆的详细信息
-dontoptimize #关闭优化
-keepattributes *Annotation* #保留注解中的参数
-keepattributes *Annotation*,InnerClasses # 保持注解
-keepattributes Signature  # 避免混淆泛型, 这在JSON实体映射时非常重要
-ignorewarnings # 屏蔽警告
-keepattributes SourceFile,LineNumberTable # 抛出异常时保留代码行号
#混淆时不使用大小写混合，混淆后的类名为小写(大小写混淆容易导致class文件相互覆盖）
-dontusemixedcaseclassnames

#未混淆的类和成员
-printseeds proguardbuild/print_seeds.txt
#列出从 apk 中删除的代码
-printusage proguardbuild/print_unused.txt
#混淆前后的映射，生成映射文件
-printmapping proguardbuild/print_mapping.txt

## 指定一个文本文件，其中所有有效字词都用作混淆字段和方法名称。
## 默认情况下，诸如“a”，“b”等短名称用作混淆名称。
## 使用模糊字典，您可以指定保留关键字的列表，或具有外来字符的标识符，
## 例如： 忽略空格，标点符号，重复字和＃符号后的注释。
## 注意，模糊字典几乎不改善混淆。 有些编译器可以自动替换它们，并且通过使用更简单的名称再次混淆，可以很简单地撤消该效果。
## 最有用的是指定类文件中通常已经存在的字符串（例如'Code'），从而减少类文件的大小。 仅适用于混淆处理。
#-obfuscationdictionary proguardbuild/pro_package.txt
#
## 指定一个文本文件，其中所有有效词都用作混淆类名。 与-obfuscationdictionary类似。 仅适用于混淆处理。
#-classobfuscationdictionary proguardbuild/pro_class.txt
#
## 指定一个文本文件，其中所有有效词都用作混淆包名称。与-obfuscationdictionary类似。 仅适用于混淆处理。
#-packageobfuscationdictionary proguardbuild/pro_func.txt

# -------------------------------基本指令区--------------------------------


#---------------------------------默认保留区---------------------------------
#继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
-keep public class * extends android.app.Activity
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class android.support.** {*;}
-keep public class * extends android.database.sqlite.SQLiteOpenHelper{*;}
-keep class me.everything.**
-keep class org.apmem.tools.**
-keep class org.jsoup.**
-keep class com.balysv.materialripple.**
-keep class me.rosuh.filepicker.**
-keep class com.jakewharton.threetenabp.**
-keep class com.tonyodev.fetch2.**
-keep class com.atilika.kuromoji.**
-keep class com.danikula.videocache.**
-keep class org.codehaus.plexus.**
-keep class com.trello.rxlifecycle.**
-keep class cz.adaptech.**
-keep class com.izettle.html2bitmap.**

# interface
#-keep interface * { *; }
-keepclassmembers class * implements com.mmjang.ankihelper.data.dict.IDictionary{*;}

# sqlite
-dontwarn org.litepal.*
-keep class org.litepal.** { *; }
-keep enum org.litepal.**
-keep interface org.litepal.** { *; }
-keep public class * extends org.litepal.**
-keepattributes *Annotation*
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * extends org.litepal.crud.DataSupport{*;}

# androidx 混淆
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-keep class * implements androidx.** {
    *;
}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-printconfiguration
-keep,allowobfuscation interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

#不混淆View中的set***() 和 get***()方法 以保证属性动画正常工作  某个类中的某个方法不混淆
#自定义View的set get方法 和 构造方法不混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#实现Serializable接口的类重写父类方法保留
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 保留R文件中所有静态字段，以保证正确找到每个资源的ID
-keepclassmembers class **.R$* {
    public static <fields>;
}


-keepclassmembers class * {
    void *(*Event);
}

#保留枚举类中的values和valueOf方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#保留Parcelable实现类中的Creator字段，以保证Parcelable机制正常工作
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#不混淆包含native方法的类的类名以及native方法名
-keepclasseswithmembernames class * {
  native<methods>;
}

#避免log打印输出
-assumenosideeffects class android.util.Log {
 public static *** v(...);
 public static *** d(...);
 public static *** i(...);
 public static *** w(...);
}
#对含有反射类的处理
#--------------------------------默认保留区--------------------------------------------



#----------------------------- WebView(项目中没有可以忽略) -----------------------------
#webView需要进行特殊处理
# WebView
-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient
-keep public class android.webkit.WebView
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient
#在app中与HTML5的JavaScript的交互进行特殊处理
#我们需要确保这些js要调用的原生方法不能够被混淆，于是我们需要做如下处理：
-keepclassmembers class com.deepocean.tplh5.helper.JsInterfaceHelper {
    <methods>;
}
#----------------------------- WebView(项目中没有可以忽略) -----------------------------


#----------------------------- 实体类不可混淆 ------------------------------------------
#添加实体类混淆规则
# Application classes that will be serialized/deserialized over Gson
-keep class **.entity.** { *; }
-keep class **.bean.** { *; }
#----------------------------- 实体类不可混淆 ------------------------------------------


#----------------------------- 第三方类库 ------------------------------------------
#添加第三方类库的混淆规则
#Adjust sdk
-keep class com.adjust.sdk.**{ *; }
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
-keep public class com.android.installreferrer.**{ *; }
# OkHttp3 去掉缺失类警告
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.javax.net.ssl.**
-dontwarn org.openjsse.net.ssl.**

#R8
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.fasterxml.jackson.core.JsonParser$Feature
-dontwarn com.fasterxml.jackson.core.JsonProcessingException
-dontwarn com.fasterxml.jackson.core.type.TypeReference
-dontwarn com.fasterxml.jackson.databind.JsonNode
-dontwarn com.fasterxml.jackson.databind.ObjectMapper
-dontwarn com.madrobot.beans.BeanInfo
-dontwarn com.madrobot.beans.IntrospectionException
-dontwarn com.madrobot.beans.Introspector
-dontwarn com.madrobot.beans.PropertyDescriptor
-dontwarn com.squareup.picasso.Picasso
-dontwarn com.squareup.picasso.RequestCreator
-dontwarn java.beans.BeanInfo
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor
-dontwarn net.minidev.json.JSONArray
-dontwarn net.minidev.json.JSONValue
-dontwarn net.minidev.json.parser.ContainerFactory
-dontwarn net.minidev.json.parser.JSONParser
-dontwarn org.cheffo.jeplite.JEP
-dontwarn org.cheffo.jeplite.ParseException
-dontwarn sun.misc.BASE64Encoder
#----------------------------- 第三方类库 ------------------------------------------
