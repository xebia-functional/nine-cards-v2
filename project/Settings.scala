import java.text.SimpleDateFormat
import java.util.Date

import Libraries.cats._
import Libraries.android._
import Libraries.graphics._
import Libraries.json._
import Libraries.macroid._
import Libraries.net._
import Libraries.google._
import Libraries.date._
import Libraries.test._
import Libraries.debug._
import Libraries.performance._
import android.Keys._
import S3._
import Crashlytics._
import Libraries.monix._
import Proguard._
import sbt.Keys._
import sbt._
import microsites.MicrositeKeys._
import com.typesafe.sbt.site.SiteKeys

object Settings extends SiteKeys {

  lazy val commit = sys.env.getOrElse("GIT_COMMIT", "unknown-commit")

  lazy val user = sys.env.getOrElse("USER", "unknown-user")

  def getDateFormatted = new SimpleDateFormat("yyyyMMdd").format(new Date())

  def versionNameSuffix = sys.env.get("GIT_PR") match {
    case Some("false") => s"-master-$getDateFormatted-$commit"
    case Some(prNumber) => s"-PR$prNumber-$commit"
    case None => ""
  }

  lazy val androidVersionName = "2.0.3-alpha"
  lazy val androidVersionCode = 60

  // App Module
  lazy val appSettings = basicSettings ++ multiDex ++ customS3Settings ++ crashlyticsSettings ++
    Seq(
      name := "nine-cards-v2",
      versionName in Android := Some(s"$androidVersionName$versionNameSuffix"),
      versionCode in Android := Some(androidVersionCode),
      run <<= run in Android,
      javacOptions in Compile ++= Seq("-target", "1.7", "-source", "1.7"),
      scalacOptions ++= Seq("-feature", "-deprecation", "-target:jvm-1.7", "-Yresolve-term-conflict:package"),
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
      proguardOptions in Android ++= proguardCommons,
      proguardCache in Android := Seq.empty,
      parallelExecution in Test := false)

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

  // Docs Module

  lazy val micrositeSettings = Seq(
    micrositeName := "9Cards",
    micrositeDescription := "A launcher crafted for and by Android Power Users",
    micrositeBaseUrl := "nine-cards-v2",
    micrositeDocumentationUrl := "/nine-cards-v2/docs/",
    micrositeGithubOwner := "47deg",
    micrositeGithubRepo := "nine-cards-v2",
    includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.md",
    micrositePalette := Map(
      "brand-primary"     -> "#E91E63",
      "brand-secondary"   -> "#283593",
      "brand-tertiary"    -> "#243087",
      "gray-dark"         -> "#4A4A4A",
      "gray"              -> "#797979",
      "gray-light"        -> "#EAEAEA",
      "gray-lighter"      -> "#F8F8F8",
      "white-color"       -> "#FFFFFF"))

  // Commons Tests Module
  lazy val commonsTestsSettings = basicSettings ++ librarySettings ++
    Seq(libraryDependencies ++= commonsTestsDependencies)

  // Android classes for Mock Module
  lazy val mockAndroidSettings = basicSettings ++ librarySettings ++
    Seq(libraryDependencies ++= mockAndroidDependencies)

  // Basic Setting for all modules
  lazy val basicSettings = Seq(
    organization := "cards.nine",
    organizationName := "47deg",
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
    aar(flowUp),
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
