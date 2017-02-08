import java.io.File

import ReplacePropertiesGenerator._
import android.Keys._
import sbt.Keys._
import sbt._

object Crashlytics {

  lazy val crashlyticsSettings =
      Seq(
        collectResources in Android <<= (collectResources in Android)
          dependsOn fixNameSpace
          dependsOn crashlyticsPreBuild
          dependsOn createFiles,
        zipalign in Android <<= (zipalign in Android) map { result =>
          crashlyticsPostPackage
          result
        }
      )

  val requiredProperties = Seq("crashlytics.apikey", "crashlytics.apisecret")

  val crashlyticsEnabled: SettingKey[Boolean] = settingKey[Boolean]("Crashlytics enabled")

  def createFiles = Def.task[Seq[File]] {
    val log = streams.value.log
    configTask[Seq[File]](log, Seq.empty, crashlyticsEnabled.value) {
      log.info("Creating crashlytics files")
      try {
        val templates = loadTemplates(baseDirectory.value / "crashlytics" / "templates")
        templates map { file =>
          val target = baseDirectory.value / "crashlytics" / file.getName
          replaceContent(file, target)(log)
          target
        }
      } catch {
        case e: Throwable =>
          log.error("An error occurred loading creating files")
          throw e
      }
    }
  }

  /*
   * Removes the namespace from the crashlytics auto-generated file.
   * This is a common problem of the com.android.tools.build:builder and crashlytics
   * that can be solved by simply removing the namespace declaration and the attributes
   * with namespace
   */
  def fixNameSpace = Def.task[Unit] {
    val log = streams.value.log
    configTask[Unit](log, (), crashlyticsEnabled.value) {
      crashlyticsCodeGen.value
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
  }

  def crashlyticsPreBuild = Def.task[Unit] {
    val log = streams.value.log
    configTask[Unit](log, (), crashlyticsEnabled.value) {
      log.info("Crashlytics pre build")

      // Cleanup
      crashlyticsCleanupResources.value

      // Upload deobs - Disabled
      // crashlyticsUploadDeobs.value
    }
  }

  def crashlyticsCodeGen = Def.task[Unit] {
    val log = streams.value.log
    configTask[Unit](log, (), crashlyticsEnabled.value) {
      log.info("Crashlytics code gen")

      // Generate resources
      crashlyticsGenerateResources.value
    }
  }

  def crashlyticsPostPackage = Def.task[Unit] {
    val log = streams.value.log
    configTask[Unit](log, (), crashlyticsEnabled.value) {
      log.info("Crashlytics post package")

      // Store deobs - Disabled
      // crashlyticsStoreDeobs.value

      // Upload deobs - Disabled
      // crashlyticsUploadDeobs.value

      // Cleanup
      crashlyticsCleanupResources.value
    }
  }

  def crashlyticsCleanupResources = Def.task[Unit] {
    val log = streams.value.log
    configTask[Unit](log, (), crashlyticsEnabled.value) {
      crashlyticsTask(
        log = streams.value.log,
        task = Crashlytics.CleanupResources,
        projectPath = baseDirectory.value.getAbsolutePath)
    }
  }

  def crashlyticsGenerateResources = Def.task[Unit] {
    val log = streams.value.log
    configTask[Unit](log, (), crashlyticsEnabled.value) {
      crashlyticsTask(
        log = streams.value.log,
        task = Crashlytics.GenerateResources,
        projectPath = baseDirectory.value.getAbsolutePath,
        extraArgs = Seq(
          "-buildEvent",
          "-tool", "com.crashlytics.tools.ant",
          "-version", "1.20.0"))
    }
  }

  def crashlyticsStoreDeobs = Def.task[Unit] {
    val log = streams.value.log
    configTask[Unit](log, (), crashlyticsEnabled.value) {
      crashlyticsTask(
        log = streams.value.log,
        task = Crashlytics.StoreDeobs,
        projectPath = baseDirectory.value.getAbsolutePath,
        extraArgs = Seq(
          s"${(baseDirectory.value / "proguard-mapping.txt").getAbsolutePath}",
          "-obfuscating",
          "-obfuscator",
          "proguard",
          "-obVer",
          "4.7",
          "-verbose"))
    }
  }

  def crashlyticsUploadDeobs = Def.task[Unit] {
    val log = streams.value.log
    configTask[Unit](log, (), crashlyticsEnabled.value) {
      crashlyticsTask(
        log = streams.value.log,
        task = Crashlytics.UploadDeobs,
        projectPath = baseDirectory.value.getAbsolutePath,
        extraArgs = Seq("-verbose"))
    }
  }

  object Crashlytics {

    sealed trait Task {
      def param: String
    }

    case object CleanupResources extends Task {
      val param = "-cleanupResourceFile"
    }

    case object GenerateResources extends Task {
      val param = "-generateResourceFile"
    }

    case object StoreDeobs extends Task {
      val param = "-storeDeobs"
    }

    case object UploadDeobs extends Task {
      val param = "-uploadDeobs"
    }

  }

  private[this] def configTask[T](log: Logger, defaultValue: T, isEnabled: Boolean)(f: => T) = {

    val enabled = if (isEnabled) {
      requiredProperties.foldLeft(true) {
        case (false, _) => false
        case (true, prop) => propertiesMap.get(prop).exists(_.nonEmpty)
      }
    } else false

    if (enabled) f else {
      log.info("Skipping crashlytics: There are some missing required properties")
      defaultValue
    }

  }

  private[this] def crashlyticsTask(
    log: Logger,
    task: Crashlytics.Task,
    projectPath: String,
    extraArgs: Seq[String] = Seq.empty) = {

    log.info(s"Crashlytics task: ${task.toString}")

    val args = Seq(
      "-projectPath", projectPath,
      "-androidManifest", s"$projectPath/crashlytics/CrashlyticsManifest.xml",
      "-androidRes", s"$projectPath/src/main/res",
      "-androidAssets", s"$projectPath/src/main/assets",
      task.param,
      "-properties", s"$projectPath/crashlytics/fabric.properties") ++ extraArgs

    log.debug(s"Arguments: $args")
    try {
      com.crashlytics.tools.android.DeveloperTools.main(args.toArray)
    } catch {
      case e: Throwable =>
        log.error(s"Error executing crashlytics task: ${e.getMessage}")
        throw e
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