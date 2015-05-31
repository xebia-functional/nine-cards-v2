import Libraries.android._
import Libraries.graphics._
import Libraries.macroid._
import Libraries.playServices._
import Versions._
import Libraries.net._
import android.Keys._
import sbt.Keys._
import sbt._

object Settings {

  // App Module
  // For multidex add `multiDex ++` to this settings
  lazy val appSettings = commonSettings ++
      Seq(
        run <<= run in Android,
        javacOptions in Compile ++= Seq("-target", "1.7", "-source", "1.7"),
        transitiveAndroidLibs in Android := false,
        libraryDependencies ++= commonDependencies,
        dexMaxHeap in Android := "2048m",
        proguardScala in Android := true,
        useProguard in Android := true,
        proguardOptions in Android ++= proguardCommons,
        proguardCache in Android := Seq.empty)

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
    scalaVersion := Versions.scalaV,
    resolvers ++= commonResolvers)

  lazy val commonDependencies = Seq(
//    aar(multiDexLib),
    aar(androidSupportv4),
    aar(androidAppCompat),
    aar(macroidRoot),
    aar(macroidExtras),
    aar(androidRecyclerview),
    aar(androidCardView),
    aar(playServicesBase),
    glide,
    okHttp)

  lazy val commonResolvers = Seq(
    Resolver.mavenLocal,
    DefaultMavenRepository,
    "jcenter" at "http://jcenter.bintray.com",
    "47 Degrees Bintray Repo" at "http://dl.bintray.com/47deg/maven",
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
  
  lazy val multiDex = Seq(
    dexMulti in Android := true,
    dexMinimizeMainFile in Android := true,
    dexMainFileClasses in Android := multiDexClasses
  )

  lazy val multiDexClasses = Seq(
    "com/fortysevendeg/ninecardslauncher/NineCardsApplication.class",
    "android/support/multidex/BuildConfig.class",
    "android/support/multidex/MultiDex$V14.class",
    "android/support/multidex/MultiDex$V19.class",
    "android/support/multidex/MultiDex$V4.class",
    "android/support/multidex/MultiDex.class",
    "android/support/multidex/MultiDexApplication.class",
    "android/support/multidex/MultiDexExtractor$1.class",
    "android/support/multidex/MultiDexExtractor.class",
    "android/support/multidex/ZipUtil$CentralDirectory.class",
    "android/support/multidex/ZipUtil.class")
}
