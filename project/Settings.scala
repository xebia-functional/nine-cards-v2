import java.text.SimpleDateFormat
import java.util.Date

import Libraries.cats._
import Libraries.android._
import Libraries.graphics._
import Libraries.json._
import Libraries.macroid._
import Libraries.net._
import Libraries.google._
import Libraries.scala._
import Libraries.date._
import Libraries.test._
import Libraries.debug._
import android.Keys._
import S3._
import Crashlytics._
import Libraries.monix._
import Proguard._
import sbt.Keys._
import sbt._

object Settings {

  lazy val commit = sys.env.getOrElse("GIT_COMMIT", "unknown-commit")

  lazy val user = sys.env.getOrElse("USER", "unknown-user")

  def getDateFormatted = new SimpleDateFormat("yyyyMMdd").format(new Date())

  def versionNameSuffix = sys.env.get("GIT_PR") match {
    case Some("false") => s"-master-$getDateFormatted-$commit"
    case Some(prNumber) => s"-PR$prNumber-$commit"
    case None => ""
  }

  // App Module
  lazy val appSettings = basicSettings ++ multiDex ++ customS3Settings ++ crashlyticsSettings ++
    Seq(
      name := "nine-cards-v2",
      run <<= run in Android,
      javacOptions in Compile ++= Seq("-target", "1.7", "-source", "1.7"),
      scalacOptions ++= Seq("-feature", "-deprecation", "-target:jvm-1.7"),
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
      useProguardInDebug in Android := true,
      versionName in Android := Some(s"${versionName.value.getOrElse("")}$versionNameSuffix"),
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

  // Models Module
  lazy val modelsSettings = basicSettings ++ librarySettings ++
    Seq(libraryDependencies ++= modelsDependencies)

  // Commons Tests Module
  lazy val commonsTestsSettings = basicSettings ++ librarySettings ++
    Seq(libraryDependencies ++= commonsTestsDependencies)

  // Android classes for Mock Module
  lazy val mockAndroidSettings = basicSettings ++ librarySettings ++
    Seq(libraryDependencies ++= mockAndroidDependencies)

  // Basic Setting for all modules
  lazy val basicSettings = Seq(
    scalaVersion := Versions.scalaV,
    resolvers ++= commonResolvers,
    libraryDependencies ++= Seq(cats, monixTypes, monixEval)
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
    aar(androidFlexbox),
    aar(playServicesBase),
    aar(playServicesAnalytics),
    aar(playServicesDrive),
    aar(playServicesAuth),
    aar(playServicesPlus),
    aar(playServicesAwareness),
    aar(multiDexLib),
    aar(crashlytics),
    aar(firebaseCore),
    aar(firebaseMessaging),
    prettyTime,
    glide,
    okHttp,
    stetho,
    stethoOkhttp,
    stethoUrlconnection,
    specs2,
    mockito,
    androidTest)

  lazy val processDependencies = Seq(
    androidProvidedLib,
    specs2,
    mockito)

  lazy val servicesDependencies = Seq(
    gfcTimeUUID,
    androidProvidedLib,
    specs2,
    mockito)

  lazy val apiDependencies = Seq(
    androidProvidedLib,
    playJson,
    okHttp,
    specs2,
    mockito)

  lazy val repositoryDependencies = Seq(
    jodaTime,
    androidProvidedLib,
    specs2,
    mockito)

  lazy val commonsDependencies = Seq(
    androidProvidedLib,
    specs2,
    mockito)

  lazy val modelsDependencies = Seq(
    androidProvidedLib,
    playJson,
    specs2,
    mockito)

  lazy val commonsTestsDependencies = Seq(
    androidProvidedLib,
    specs2Lib,
    mockitoLib)

  lazy val mockAndroidDependencies = Seq(
    androidProvidedLib,
    specs2Lib,
    mockitoLib)

  lazy val commonResolvers = Seq(
    Resolver.mavenLocal,
    DefaultMavenRepository,
    Resolver.typesafeRepo("releases"),
    Resolver.typesafeRepo("snapshots"),
    Resolver.typesafeIvyRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.defaultLocal,
    Resolver.jcenterRepo,
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    "crashlytics" at "https://maven.fabric.io/public"
  )

  lazy val multiDex = Seq(
    dexMulti in Android := true,
    dexMainClasses in Android := multiDexClasses
  )

  lazy val multiDexClasses = Seq(
    "cards/nine/app/NineCardsApplication.class",
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
