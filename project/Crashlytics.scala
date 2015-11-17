import android.Keys._
import de.johoop.ant4sbt.Ant4Sbt._
import sbt.Keys._
import sbt._

object Crashlytics {

  lazy val crashlyticsSettings =
    antSettings ++
      addAntTasks(
        "crashlytics-pre-build",
        "crashlytics-code-gen",
        "crashlytics-post-package") ++
      Seq(
        antBuildFile := baseDirectory.value / "crashlytics_build_base.xml",
        packageDebug in Android <<= (packageDebug in Android) dependsOn antTaskKey("crashlytics-pre-build"),
        packageRelease in Android <<= (packageRelease in Android) dependsOn antTaskKey("crashlytics-pre-build"),
        packageResources in Android <<= (packageResources in Android) dependsOn antTaskKey("crashlytics-code-gen"),
        apkbuild in Android <<= (apkbuild in Android) map { result =>
          antTaskKey("crashlytics-post-package")
          result
        }
      )

}