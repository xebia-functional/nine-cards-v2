import Libraries.android._
import Libraries.graphics._
import Libraries.json._
import Libraries.macroid._
import Libraries.net._
import Libraries.playServices._
import Libraries.scala._
import Libraries.test._
import Libraries.debug._
import android.Keys._
import S3._
import Crashlytics._
import sbt.Keys._
import sbt._

object Settings {

  // App Module
  lazy val appSettings = basicSettings ++ multiDex ++ customS3Settings ++ crashlyticsSettings ++
    Seq(
      name := "nine-cards-v2",
      run <<= run in Android,
      javacOptions in Compile ++= Seq("-target", "1.7", "-source", "1.7"),
      transitiveAndroidLibs in Android := true,
      libraryDependencies ++= appDependencies,
      packagingOptions in Android := PackagingOptions(excludes = Seq(
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/NOTICE",
        "META-INF/NOTICE.txt",
        "scalac-plugin.xml",
        "reference.conf")),
      dexMaxHeap in Android := "2048m",
      proguardScala in Android := true,
      useProguard in Android := true,
      proguardOptions in Android ++= proguardCommons,
      proguardCache in Android := Seq.empty)

  // Api Module
  lazy val apiSettings = basicSettings ++ librarySettings ++
    Seq(libraryDependencies ++= apiDependencies)

  // Repository Module
  lazy val repositorySettings = basicSettings ++ librarySettings ++
    Seq(libraryDependencies ++= repositoryDependencies)

  // Services Module
  lazy val servicesSettings = basicSettings ++ librarySettings ++
    Seq(libraryDependencies ++= servicesDependencies)

  // Process Module
  lazy val processSettings = basicSettings ++ librarySettings ++
    Seq(libraryDependencies ++= processDependencies)

  // Commons Module
  lazy val commonsSettings = basicSettings ++ librarySettings ++
    Seq(libraryDependencies ++= commonsDependencies)

  // Basic Setting for all modules
  lazy val basicSettings = Seq(
    scalaVersion := Versions.scalaV,
    resolvers ++= commonResolvers,
    libraryDependencies ++= Seq(scalaz, scalazConcurrent)
  )

  lazy val duplicatedFiles = Set(
    "AndroidManifest.xml",
    "theme_dark.json",
    "theme_light.json")

  // Settings associated to library modules
  lazy val librarySettings = Seq(
    mappings in(Compile, packageBin) ~= {
      _.filter { tuple =>
        !duplicatedFiles.contains(tuple._1.getName)
      }
    },
    exportJars := true,
    scalacOptions in Compile ++= Seq("-deprecation", "-Xexperimental"),
    javacOptions in Compile ++= Seq("-target", "1.7", "-source", "1.7"),
    javacOptions in Compile += "-deprecation",
    proguardScala in Android := false)

  lazy val appDependencies = Seq(
    aar(androidAppCompat),
    aar(macroidExtras),
    aar(macroidRoot),
    aar(androidSupportv4),
    aar(androidRecyclerview),
    aar(androidCardView),
    aar(androidDesign),
    aar(playServicesBase),
    aar(multiDexLib),
    glide,
    okHttp,
    stetho,
    stethoOkhttp,
    stethoUrlconnection)

  lazy val processDependencies = Seq(
    androidProvidedLib,
    specs2,
    mockito)

  lazy val servicesDependencies = Seq(
    androidProvidedLib,
    specs2,
    mockito)

  lazy val apiDependencies = Seq(
    androidProvidedLib,
    playJson,
    okHttp % "provided",
    specs2,
    mockito,
    mockServer)

  lazy val repositoryDependencies = Seq(
    androidProvidedLib,
    specs2,
    mockito)

  lazy val commonsDependencies = Seq(
    androidProvidedLib,
    specs2,
    mockito)

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
    "-keep class com.google.** { *; }",
    "-keep class com.facebook.stetho.** { *; }",
    "-keep class com.crashlytics.** { *; }",
    "-dontwarn com.crashlytics.**")

  lazy val multiDex = Seq(
    dexMulti in Android := true,
    dexMinimizeMain in Android := true,
    dexMainClasses in Android := multiDexClasses
  )

  lazy val multiDexClasses = Seq(
    "com/fortysevendeg/ninecardslauncher/app/NineCardsApplication.class",
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
