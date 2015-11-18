import java.io.File

import android.Keys._
import de.johoop.ant4sbt.Ant4Sbt._
import sbt.Keys._
import sbt._
import ReplacePropertiesGenerator._

object Crashlytics {

  lazy val crashlyticsSettings =
    antSettings ++
      addAntTasks(
        "crashlytics-pre-build",
        "crashlytics-code-gen",
        "crashlytics-post-package") ++
      Seq(
        antBuildFile := baseDirectory.value / "crashlytics" / "crashlytics_build.xml",
        typedResourcesGenerator in Android <<= (typedResourcesGenerator in Android)
          dependsOn antTaskKey("crashlytics-code-gen")
          dependsOn createFiles
          dependsOn antTaskKey("crashlytics-pre-build"),
        zipalign in Android <<= (zipalign in Android) map { result =>
          antTaskKey("crashlytics-post-package")
          result
        }
      )

  def createFiles = Def.task[Seq[File]] {
    val log = streams.value.log
    log.info("Creating crashlytics files")
    try {
      val templates = loadTemplates(baseDirectory.value / "crashlytics" / "templates")
      templates map { file =>
        val target = baseDirectory.value / "crashlytics" / file.getName
        replaceContent(file, target)
        target
      }
    } catch {
      case e: Throwable =>
        log.error("An error occurred loading creating files")
        throw e
    }
  }

  private[this] def loadTemplates(folder: File): Seq[File] = {
    folder.listFiles().toSeq
  }

}