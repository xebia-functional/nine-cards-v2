import sbt._
import sbt.Keys._
import android.Keys._
import Settings._
import Libraries.json._
import Libraries.net._
import Libraries.test._
import Libraries.akka._

//object AppBuild extends android.AutoBuild {
object AppBuild extends Build {

  def excludeArtifact(module: ModuleID, artifactOrganizations: String*): ModuleID =
    module.excludeAll(artifactOrganizations map (org => ExclusionRule(organization = org)): _*)

  lazy val root = Project(id = "root", base = file("."))
      .settings(
        scalaVersion := scalaV,
        name := "9 Cards 2.0",
        scalacOptions ++= Seq("-feature", "-deprecation"),
        platformTarget in Android := "android-21",
        packageT in Compile <<= packageT in Android in app,
        packageRelease <<= packageRelease in Android in app,
        packageDebug <<= packageDebug in Android in app,
        install <<= install in Android in app,
        run <<= run in Android in app,
        packageName in Android := "com.fortysevendeg.ninecardslauncher"
      )
      .aggregate(app, api, repository)

  lazy val app = Project(id = "app", base = file("modules/app"))
      .androidBuildWith(api, repository)
      .settings(projectDependencies ~= (_.map(excludeArtifact(_, "com.android"))))
      .settings(apkbuildExcludes in Android ++= Seq(
          "META-INF/LICENSE",
          "META-INF/LICENSE.txt",
          "META-INF/NOTICE",
          "META-INF/NOTICE.txt",
          "reference.conf"))
      .settings(appSettings: _*)

  val api = Project(id = "api", base = file("modules/api"))
      .settings(libraryDependencies ++= Seq(
    playJson,
    sprayClient,
    akkaActor,
    specs2,
    mockito,
    androidTest,
    mockServer))
      .settings(apiSettings: _*)

  val repository = Project(id = "repository", base = file("modules/repository"))
      .settings(repositorySettings: _*)
}