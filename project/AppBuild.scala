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
      platformTarget in Android := "android-21",
      packageT in Compile <<= packageT in Android in app,
      packageRelease <<= packageRelease in Android in app,
      packageDebug <<= packageDebug in Android in app,
      install <<= install in Android in app,
      run <<= (run in Android in app).dependsOn(setDebugTask(true)),
      packageName in Android := "com.fortysevendeg.ninecardslauncher"
    )
    .aggregate(app, process)

  lazy val app = Project(id = "app", base = file("modules/app"))
    .androidBuildWith(process)
    .settings(projectDependencies ~= (_.map(excludeArtifact(_, "com.android"))))
    .settings(packageResources in Android <<= (packageResources in Android).dependsOn(replaceValuesTask))
    .settings(appSettings: _*)

  val api = Project(id = "api", base = file("modules/api"))
    .settings(apiSettings: _*)

  val repository = Project(id = "repository", base = file("modules/repository"))
    .settings(repositorySettings: _*)

  val services = Project(id = "services", base = file("modules/services"))
    .settings(servicesSettings: _*)
    .dependsOn(api, repository)

  val process = Project(id = "process", base = file("modules/process"))
    .settings(processSettings: _*)
    .dependsOn(services)
}