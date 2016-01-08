import java.io.File

import ReplacePropertiesGenerator._
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
        antBuildFile := baseDirectory.value / "crashlytics" / "crashlytics_build.xml",
        collectResources in Android <<= (collectResources in Android)
          dependsOn fixNameSpace
          dependsOn antTaskKey("crashlytics-pre-build")
          dependsOn createFiles,
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

  /*
   * Removes the namespace from the crashlytics auto-generated file.
   * This is a common problem of the com.android.tools.build:builder and crashlytics
   * that can be solved by simply removing the namespace declaration and the attributes
   * with namespace
   */
  def fixNameSpace = Def.task[Unit] {
    antTaskKey("crashlytics-code-gen").value
    val file = baseDirectory.value / "src/main/res/values/com_crashlytics_export_strings.xml"
    if (file.exists()) {
      val xml = scala.xml.XML.loadFile(file)

      val rewriteRule = new scala.xml.transform.RewriteRule {
        override def transform(node: scala.xml.Node) = node match {
          case elem: scala.xml.Elem if elem.label == "resources" =>
            elem.copy(child = fixChilds(elem.child))
          case x => x
        }
      }

      scala.xml.XML.save(
        filename = file.getAbsolutePath,
        node = rewriteRule(xml),
        enc = "UTF-8",
        xmlDecl = true)
    }
  }

  private[this] def loadTemplates(folder: File): Seq[File] = {
    folder.listFiles().toSeq
  }

  private[this] def fixChilds(child: Seq[scala.xml.Node]): Seq[scala.xml.Node] = {
    child map {
      case elem: scala.xml.Elem =>
        elem.copy(
          scope = scala.xml.TopScope,
          attributes = elem.attributes.filter { a =>
            Option(a.getNamespace(elem)).isEmpty
          })
      case x => x
    }
  }

}