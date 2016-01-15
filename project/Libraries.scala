import sbt._
import Versions._

object Libraries {

  def onCompile(dep: ModuleID): ModuleID = dep % "compile"
  def onTest(dep: ModuleID): ModuleID = dep % "test"

  object scala {

    lazy val scalaReflect = "org.scala-lang" % "scala-reflect" % scalaV
    lazy val scalap = "org.scala-lang" % "scalap" % scalaV
    lazy val scalaz = "org.scalaz" %% "scalaz-core" % "7.1.3"
    lazy val scalazConcurrent = "org.scalaz" %% "scalaz-concurrent" % "7.1.3"
//    lazy val rapture = "com.propensive" %% "rapture-core" % "2.0.+" changing()
//    lazy val raptureScalaz = "com.propensive" %% "rapture-core-scalaz" % "2.0.+" changing()
  }

  object android {

    def androidDep(module: String) = "com.android.support" % module % androidV

    lazy val multiDexLib = "com.google.android" % "multidex" % multiDexV

    lazy val androidProvidedLib = "com.google.android" % "android" % androidProvidedV % "provided"

    lazy val androidSupportv4 = androidDep("support-v4")
    lazy val androidAppCompat = androidDep("appcompat-v7")
    lazy val androidRecyclerview = androidDep("recyclerview-v7")
    lazy val androidCardView = androidDep("cardview-v7")
    lazy val androidDesign = androidDep("design")
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
    lazy val okHttp = "com.squareup.okhttp" % "okhttp" % okHttpV
  }

  object test {
    lazy val specs2 = "org.specs2" %% "specs2-core" % specs2V % "test"
    lazy val mockito = "org.specs2" % "specs2-mock_2.11" % mockitoV % "test"
    lazy val androidTest = "com.google.android" % "android" % "4.1.1.4" % "test"
    lazy val mockServer = "org.mock-server" % "mockserver-netty" % mockServerV % "test"
  }

  object graphics {
    lazy val glide = "com.github.bumptech.glide" % "glide" % glideV
  }

  object playServices {

    def playServicesDep(module: String) = "com.google.android.gms" % module % playServicesV

    // Google Actions, Google Analytics and Google Cloud Messaging
    lazy val playServicesBase = playServicesDep("play-services-base")
    lazy val playServicesDrive = playServicesDep("play-services-drive")
  }

  object debug {
    lazy val stetho = "com.facebook.stetho" % "stetho" % stethoV
    lazy val stethoOkhttp = "com.facebook.stetho" % "stetho-okhttp" % stethoV
    lazy val stethoUrlconnection = "com.facebook.stetho" % "stetho-urlconnection" % stethoV
    lazy val crashlytics = "com.crashlytics.sdk.android" % "crashlytics" % crashlyticsV
  }

}
