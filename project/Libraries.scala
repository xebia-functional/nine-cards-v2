import sbt._
import Versions._

object Libraries {

  def onCompile(dep: ModuleID): ModuleID = dep % "compile"
  def onTest(dep: ModuleID): ModuleID = dep % "test"

  object scala {

    lazy val scalaReflect = "org.scala-lang" % "scala-reflect" % scalaV
    lazy val scalap = "org.scala-lang" % "scalap" % scalaV
  }

  object cats {
    lazy val cats = "org.typelevel" %% "cats" % Versions.catsV
  }

  object monix {
    lazy val monixTypes = "io.monix" %% "monix-types" % Versions.monixV
    lazy val monixEval = "io.monix" %% "monix-eval" % Versions.monixV
  }

  object android {

    def androidDep(module: String) = "com.android.support" % module % androidV

    lazy val multiDexLib = "com.android.support" % "multidex" % multiDexV

    lazy val androidProvidedLib = "com.google.android" % "android" % androidProvidedV % "provided"

    lazy val androidSupportv4 = androidDep("support-v4")
    lazy val androidAppCompat = androidDep("appcompat-v7")
    lazy val androidRecyclerview = androidDep("recyclerview-v7")
    lazy val androidCardView = androidDep("cardview-v7")
    lazy val androidDesign = androidDep("design")
    lazy val androidFlexbox = "com.google.android" % "flexbox" % flexboxV
  }

  object macroid {

    def macroid(module: String = "") =
      "org.macroid" %% s"macroid${if (!module.isEmpty) s"-$module" else ""}" % macroidV

    lazy val macroidRoot = macroid()
    lazy val macroidExtras = "com.fortysevendeg" %% "macroid-extras" % macroidExtrasV changing()
  }

  object json {
    lazy val playJson = "com.typesafe.play" %% "play-json" % playJsonV
  }

  object net {
    lazy val okHttp = "com.squareup.okhttp3" % "okhttp" % okHttpV
  }

  object date {
    lazy val prettyTime = "org.ocpsoft.prettytime" % "prettytime" % prettyTimeV
    lazy val jodaTime = "joda-time" % "joda-time" % jodaTimeV
    lazy val gfcTimeUUID = "com.gilt" %% "gfc-timeuuid" % gfcTimeUUIDV
  }

  object test {

    lazy val specs2Lib = "org.specs2" %% "specs2-core" % specs2V
    lazy val specs2 = specs2Lib % "test"

    lazy val mockitoLib = "org.specs2" % "specs2-mock_2.11" % mockitoV
    lazy val mockito = mockitoLib % "test"

    lazy val androidTest = "com.google.android" % "android" % "4.1.1.4" % "test"
  }

  object graphics {
    lazy val glide = "com.github.bumptech.glide" % "glide" % glideV
  }

  object google {

    def playServicesDep(module: String) = "com.google.android.gms" % module % playServicesV

    // Google Actions, Google Analytics and Google Cloud Messaging
    lazy val playServicesBase = playServicesDep("play-services-base")
    lazy val playServicesDrive = playServicesDep("play-services-drive")
    lazy val playServicesAnalytics = playServicesDep("play-services-analytics")
    lazy val playServicesAuth = playServicesDep("play-services-auth")
    lazy val playServicesPlus = playServicesDep("play-services-plus")
    lazy val playServicesAwareness = playServicesDep("play-services-contextmanager")

    lazy val firebaseCore = "com.google.firebase" % "firebase-core" % playServicesV
    lazy val firebaseMessaging = "com.google.firebase" % "firebase-messaging" % playServicesV
  }

  object debug {
    lazy val stetho = "com.facebook.stetho" % "stetho" % stethoV
    lazy val stethoOkhttp = "com.facebook.stetho" % "stetho-okhttp3" % stethoV
    lazy val stethoUrlconnection = "com.facebook.stetho" % "stetho-urlconnection" % stethoV
    lazy val crashlytics = "com.crashlytics.sdk.android" % "crashlytics" % crashlyticsV
  }

}
