import ReplacePropertiesGenerator._
import Settings._
import Versions._
import android.Keys._
import sbt.Keys._
import sbt._
import microsites.MicrositesPlugin

object AppBuild extends Build {

  def excludeArtifact(module: ModuleID, artifactOrganizations: String*): ModuleID =
    module.excludeAll(artifactOrganizations map (org => ExclusionRule(organization = org)): _*)

  lazy val root = Project(id = "root", base = file("."))
      .settings(
        scalaVersion := scalaV,
        name := "9 Cards 2.0",
        scalacOptions ++= Seq("-feature", "-deprecation"),
        platformTarget in Android := androidPlatformV,
        packageRelease <<= packageRelease in Android in app,
        packageDebug <<= packageDebug in Android in app,
        install <<= install in Android in app,
        run <<= (run in Android in app),
        applicationId in Android := "com.fortysevendeg.ninecardslauncher"
      )
      .aggregate(app)

  lazy val app = Project(id = "app", base = file("modules/app"))
    .androidBuildWith(process)
    .settings(projectDependencies ~= (_.map(excludeArtifact(_, "com.android"))))
    .settings(
      outputLayout in Android <<= (outputLayout in Android),
      packageResources in Android <<= (packageResources in Android).dependsOn(replaceValuesTask)
    )
    .settings(appSettings: _*)
    .dependsOn(commonsTests % "test->test")

  lazy val process = Project(id = "process", base = file("modules/process"))
    .settings(processSettings: _*)
    .androidBuildWith(services)
    .dependsOn(commonsTests % "test->test")

  lazy val services = Project(id = "services", base = file("modules/services"))
    .settings(servicesSettings: _*)
    .dependsOn(api, repository, models, commonsTests % "test->test")

  lazy val api = Project(id = "api", base = file("modules/api"))
    .settings(apiSettings: _*)
    .dependsOn(commons, models, commonsTests % "test->test")

  lazy val repository = Project(id = "repository", base = file("modules/repository"))
    .settings(repositorySettings: _*)
    .dependsOn(commons, models, commonsTests % "test->test")

  lazy val models = Project(id = "models", base = file("modules/models"))
    .settings(modelsSettings: _*)
    .dependsOn(commons, mockAndroid % "test->test")

  lazy val commons = Project(id = "commons", base = file("modules/commons"))
    .settings(commonsSettings: _*)
    .dependsOn(mockAndroid % "test->test")

  lazy val commonsTests = Project(id = "commons-tests", base = file("modules/commons-tests"))
    .settings(commonsTestsSettings: _*)
    .dependsOn(commons, mockAndroid, models)

  lazy val mockAndroid = Project(id = "mockAndroid", base = file("modules/mock-android"))
    .settings(mockAndroidSettings: _*)

  lazy val tests = Project(id = "tests", base = file("modules/tests"))
    .settings(commonsSettings: _*)
    .aggregate(process, services, api, repository, commons, commonsTests)

  lazy val docs = (project in file("modules/docs"))
    .settings(commonsSettings: _*)
    .settings(micrositeSettings: _*)
    .settings(moduleName := "docs")
    .enablePlugins(MicrositesPlugin)
    .settings(
      name := "docs",
      description := "9Cards Documentation")

}
