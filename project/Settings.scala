import Libraries.android._
import Libraries.graphics._
import Libraries.playServices._
import android.Keys._
import sbt.Keys._
import sbt._

object Settings {

  // Commons

  lazy val commonSettings = Seq(
    scalaVersion := Versions.scalaV,
    resolvers ++= commonResolvers)

  lazy val commonDependencies = Seq(
    aar(androidSupportv4),
    aar(androidRecyclerview),
    aar(androidCardView),
    aar(playServicesBase),
    glide,
    compilerPlugin(Libraries.wartRemover))

  lazy val commonResolvers = Seq(
    Resolver.mavenLocal,
    DefaultMavenRepository,
    "jcenter" at "http://jcenter.bintray.com",
    Resolver.typesafeRepo("releases"),
    Resolver.typesafeRepo("snapshots"),
    Resolver.typesafeIvyRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.defaultLocal,
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
  )

  lazy val proguardCommons = Seq(
    "-ignorewarnings",
    "-keepattributes Signature",
    "-keepattributes InnerClasses",
    "-dontwarn scala.collection.**",
    "-keep class android.support.v7.widget.SearchView { <init>(...); }",
    "-keep class android.support.v7.internal.widget.* { <init>(...); }",
    "-keep class scala.Dynamic",
    "-keep class macroid.** { *; }",
    "-keep class com.fortysevendeg.** { *; }",
    "-keep class android.** { *; }",
    "-keep class com.google.** { *; }"
  )

  // App Module

  lazy val appSettings = commonSettings ++
      Seq(
        run <<= run in Android,
        javacOptions in Compile ++= Seq("-target", "1.7", "-source", "1.7"),
        transitiveAndroidLibs in Android := false,
        libraryDependencies ++= commonDependencies,
        proguardScala in Android := true,
        useProguard in Android := true,
        proguardOptions in Android ++= proguardCommons
      )

  // Api Module

  lazy val apiSettings = commonSettings ++
      Seq(
        exportJars := true,
        scalacOptions in Compile ++= Seq("-deprecation", "-Xexperimental"),
        javacOptions in Compile ++= Seq("-target", "1.7", "-source", "1.7"),
        javacOptions in Compile += "-deprecation",
        proguardScala in Android := false
      )

  // Repository Module

  lazy val repositorySettings = commonSettings ++
      Seq(
        exportJars := true,
        proguardScala in Android := false
      )
}