import ReplacePropertiesGenerator._
import Settings._
import Versions._
import android.Keys._
import sbt.Keys._
import sbt._

object AppBuild extends Build {

  def excludeArtifact(module: ModuleID, artifactOrganizations: String*): ModuleID =
    module.excludeAll(artifactOrganizations map (org => ExclusionRule(organization = org)): _*)

  lazy val root = Project(id = "root", base = file("."))
      .settings(
        scalaVersion := scalaV,
        name := "9 Cards 2.0",
        scalacOptions ++= Seq("-feature", "-deprecation"),
        platformTarget in Android := "android-22",
        packageRelease <<= packageRelease in Android in app,
        packageDebug <<= packageDebug in Android in app,
        install <<= install in Android in app,
        run <<= (run in Android in app).dependsOn(setDebugTask(true)),
        packageName in Android := "com.fortysevendeg.ninecardslauncher"
      )
      .aggregate(app)

  lazy val app = Project(id = "app", base = file("modules/app"))
    .androidBuildWith(process)
    .settings(projectDependencies ~= (_.map(excludeArtifact(_, "com.android"))))
    .settings(packageResources in Android <<= (packageResources in Android).dependsOn(replaceValuesTask))
    .settings(appSettings: _*)

  lazy val process = Project(id = "process", base = file("modules/process"))
    .settings(processSettings: _*)
    .androidBuildWith(services)

  lazy val services = Project(id = "services", base = file("modules/services"))
    .settings(servicesSettings: _*)
    .dependsOn(commons, api, repository)

  lazy val api = Project(id = "api", base = file("modules/api"))
    .settings(apiSettings: _*)

  lazy val repository = Project(id = "repository", base = file("modules/repository"))
    .settings(repositorySettings: _*)

  lazy val commons = Project(id = "commons", base = file("modules/commons"))
    .settings(commonsSettings: _*)
}