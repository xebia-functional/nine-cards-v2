import android.Keys._
import sbt.Keys._
import sbt._

object Settings {

  val scalaV = "2.11.2"

  lazy val appSettings = commonSettings ++
      Seq(
        run <<= run in Android,
        javacOptions in Compile ++= Seq("-target", "1.7", "-source", "1.7"),
        transitiveAndroidLibs in Android := false,
        proguardScala in Android := true,
        useProguard in Android := true,
        proguardOptions in Android ++= Seq(
          "-ignorewarnings",
          "-keepattributes Signature",
          "-keepattributes InnerClasses",
          "-dontwarn scala.collection.**",
          "-keep class android.support.v7.widget.SearchView { <init>(...); }",
          "-keep class android.support.v7.internal.widget.* { <init>(...); }",
          "-keep class scala.Dynamic",
          "-keep class macroid.** { *; }",
          "-keep class android.** { *; }",
          "-keep class com.google.** { *; }"
        )
      )

  lazy val apiSettings = commonSettings ++
      Seq(
        exportJars := true,
        scalacOptions in Compile ++= Seq("-deprecation", "-Xexperimental"),
        javacOptions in Compile ++= Seq("-target", "1.7", "-source", "1.7"),
        javacOptions in Compile += "-deprecation",
        proguardScala in Android := false
      )

  lazy val commonSettings = Seq(
    scalaVersion := scalaV,
    resolvers ++= commonResolvers,
    libraryDependencies ++= commonDependencies)

  lazy val commonDependencies = Seq(
    "com.android.support" % "support-v4" % "21.0.0",
    "com.android.support" % "appcompat-v7" % "21.0.0",
    "com.android.support" % "recyclerview-v7" % "21.0.0",
    "com.android.support" % "cardview-v7" % "21.0.0",
    "org.macroid" %% "macroid" % "2.0.0-M3",
    compilerPlugin("org.brianmckenna" %% "wartremover" % "0.11"))

  lazy val commonResolvers = Seq(
    Resolver.sonatypeRepo("releases"),
    "jcenter" at "http://jcenter.bintray.com"
  )
}