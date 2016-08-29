import Proguard.Packages._

object Proguard {

  lazy val proguardCommons = Seq(
    "-ignorewarnings",
    "-keepattributes Signature",
    "-keepattributes InnerClasses",
    "-dontwarn scala.collection.**",
    "-dontobfuscate",
    "-keep class org.ocpsoft.prettytime.i18n.**",
    "-keep class android.support.v7.widget.SearchView { <init>(...); }",
    "-keep class android.support.v7.internal.widget.* { <init>(...); }",
    "-keep class scala.Dynamic",
    "-keep class macroid.** { *; }",
    "-keep class com.fortysevendeg.** { *; }",
    "-keep class com.facebook.stetho.** { *; }",
    "-keep class com.crashlytics.** { *; }",
    "-dontwarn com.crashlytics.**")

  lazy val proguardCacheList =
    pgApplication ::
      pgMacroid ::
      pgMacroidExtras ::
      pgAndroidSupport ::
      pgPlayServices ::
//      pgFabric ::
//      pgCrashlytcs ::
//      pgStetho ::
      pgGlide ::
      pgOkHttp ::
      pgOkio ::
      pgJackson ::
      pgTypesafeConfig ::
      pgJavax ::
      pgApacheCommons ::
      pgJodaTime ::
      pgPlayApi ::
      pgWartRemover ::
      pgScalaz ::
      pgScala ::
      Nil

  object Packages {

    val pgApplication = "com.fortysevendeg.ninecardslauncher"
    val pgMacroid = "macroid"
    val pgMacroidExtras = "com.fortysevendeg.macroid.extras"

    val pgAndroidSupport = "android.support"
    val pgPlayServices = "com.google.android.gms"

    val pgFabric = "io.fabric.sdk"
    val pgCrashlytcs = "com.crashlytics.android"
    val pgStetho = "com.facebook.stetho"

    val pgGlide = "com.bumptech.glide"
    val pgOkHttp = "com.skuareup.okhttp"
    val pgOkio = "okio"

    val pgJackson = "com.fasterxml.jackson"
    val pgTypesafeConfig = "com.typesafe.config"
    val pgJavax = "javax.annotation"
    val pgApacheCommons = "org.apache.commons"
    val pgJodaTime = "org.joda"
    val pgPlayApi = "play.api"
    val pgWartRemover ="org.brianmckenna.wartremover"

    val pgScalaz = "scalaz"

    val pgScala = "scala"

  }

}