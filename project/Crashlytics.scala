import android.Keys._
import de.johoop.ant4sbt.Ant4Sbt._
import sbt.Keys._
import sbt._

object Crashlytics {

  lazy val crashlyticsSettings =
    antSettings ++
      addAntTasks(
        "test1",
        "crashlytics-pre-build",
        "crashlytics-code-gen",
        "crashlytics-post-package") ++
      Seq(
        antBuildFile := baseDirectory.value / "crashlytics_build_base.xml",
        apkbuild <<= (apkbuild in Android) dependsOn antTaskKey("crashlytics-pre-build"),
        compile <<= (compile in Compile) dependsOn antTaskKey("crashlytics-code-gen"),
        zipalign <<= (zipalign in Android) dependsOn antTaskKey("crashlytics-post-package")
      )

}