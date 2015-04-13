import sbt._

object Libraries {

  def onCompile(dep: ModuleID): ModuleID = dep % "compile"
  def onTest(dep: ModuleID): ModuleID = dep % "test"

  //Plugins:
  lazy val wartRemover = "org.brianmckenna" %% "wartremover" % Versions.wartremoverV

  object scala {

    lazy val scalaReflect = "org.scala-lang" % "scala-reflect" % Versions.scalaV
    lazy val scalap = "org.scala-lang" % "scalap" % Versions.scalaV
  }

  object android {

    def androidDep(module: String) = "com.android.support" % module % Versions.androidV

    lazy val multiDexLib = "com.google.android" % "multidex" % Versions.multiDexV

    lazy val androidSupportv4 = androidDep("support-v4")
    lazy val androidAppCompat = androidDep("appcompat-v7")
    lazy val androidRecyclerview = androidDep("recyclerview-v7")
    lazy val androidCardView = androidDep("cardview-v7")
  }

  object macroid {

    def macroid(module: String = "") =
      "org.macroid" %% s"macroid${if(!module.isEmpty) s"-$module" else ""}" % Versions.macroidV

    lazy val macroidRoot = macroid()
    lazy val macroidExtras = "com.fortysevendeg" %% "macroid-extras" % Versions.macroidExtrasV
  }

  object json {
    lazy val playJson = "com.typesafe.play" %% "play-json" % Versions.playJsonV
  }

  object net {
    lazy val sprayClient = "io.spray" %% "spray-client" % Versions.sprayClientV
    lazy val okHttp = "com.squareup.okhttp" % "okhttp" % Versions.okHttpV
  }

  object test {
    lazy val specs2 = "org.specs2" %% "specs2-core" % Versions.specs2V % "test"
    lazy val mockito = "org.specs2" % "specs2-mock_2.11" % Versions.mockitoV % "test"
    lazy val androidTest = "com.google.android" % "android" % "4.1.1.4" % "test"
    lazy val mockServer = "org.mock-server" % "mockserver-netty" % Versions.mockServerV % "test"
  }

  object graphics {
    lazy val glide = "com.github.bumptech.glide" % "glide" % Versions.glideV
//    lazy val picasso = "com.squareup.picasso" % "picasso" % Versions.picassoV
  }

  object akka {
    lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % Versions.akkaActorV
  }

  object playServices {

    def playServicesDep(module: String) = "com.google.android.gms" % module % Versions.playServicesV

    // Google Actions, Google Analytics and Google Cloud Messaging
    lazy val playServicesBase = playServicesDep("play-services-base")
  }

}
