import Libraries.android._
import Libraries.graphics._
import Libraries.json._
import Libraries.macroid._
import Libraries.playServices._
import Libraries.test._
import android.Keys._
import sbt.Keys._
import sbt._

object Settings {

  val scalaV = "2.11.2"

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
      scalacOptions in Compile ++= Seq("-deprecation", "-Xexperimental"),
      javacOptions in Compile ++= Seq("-target", "1.7", "-source", "1.7"),
      javacOptions in Compile += "-deprecation",
      proguardScala in Android := false
    )

  // Commons

  lazy val commonSettings = Seq(
    scalaVersion := scalaV,
    resolvers ++= commonResolvers)

  lazy val commonDependencies = Seq(
    aar(androidSupportv4),
    aar(androidAppCompat),
    aar(androidRecyclerview),
    aar(androidCardView),
    aar(macroidRoot),
    aar(macroidExtras),
    aar(playServicesBase),
    glide,
    compilerPlugin( "org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full ),
    "io.taig.android" %% "parcelable" % "1.2.5",
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
}