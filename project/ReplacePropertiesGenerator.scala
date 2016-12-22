import java.io.{File, FileInputStream}
import java.util.Properties

import android.Keys._
import sbt.Keys._
import sbt._

import scala.annotation.tailrec
import scala.collection.JavaConverters._

object ReplacePropertiesGenerator {

  lazy val propertyNames: Map[String, String] = Map(
    "backend.v1.url" -> "",
    "backend.v1.appid" -> "",
    "backend.v1.appkey" -> "",
    "backend.v2.url" -> "",
    "crashlytics.enabled" -> "false",
    "crashlytics.apikey" -> "",
    "crashlytics.apisecret" -> "",
    "strictmode.enabled" -> "false",
    "analytics.enabled" -> "false",
    "analytics.trackid" -> "",
    "firebase.enabled" -> "false",
    "firebase.url" -> "",
    "firebase.google.appid" -> "",
    "firebase.google.apikey" -> "",
    "firebase.gcm.senderid" -> "",
    "firebase.clientid" -> "",
    "flowup.enabled" -> "false",
    "flowup.apikey" -> "",
    "apptentive.enabled" -> "false",
    "apptentive.apikey" -> "")

  lazy val propertiesFileName = sys.env.getOrElse("9CARDS_PROPERTIES", "ninecards.properties")

  lazy val propertiesMap = loadPropertiesMap

  private[this] def loadPropertiesMap: Map[String, String] = {

    def populateDefaultProperties(propertiesMap: Map[String, String]): Map[String, String] =
      propertiesMap ++ (propertyNames filterKeys (key => !propertiesMap.contains(key)))

    def loadPropertiesFile: Option[File] = {
      val file = new File(propertiesFileName)
      if (file.exists()) Some(file) else None
    }

    populateDefaultProperties {
      (loadPropertiesFile map { file =>
        val properties = new Properties()
        properties.load(new FileInputStream(file))
        properties.asScala.toMap
      }) getOrElse Map.empty
    }
  }

  def replaceValuesTask = Def.task[Seq[File]] {

    val log = streams.value.log

    log.debug("Replacing values")
    try {
      val dir: (File, File) = (collectResources in Android).value
      val valuesFile: File =  new File(dir._2, "/values/values.xml")
      replaceContent(valuesFile, valuesFile)(log)
      Seq(valuesFile)
    } catch {
      case e: Throwable =>
        log.error(s"An error occurred replacing values")
        throw e
    }
  }

  def replaceContent(origin: File, target: File)(log: Logger) = {

    def replaceLine(properties: Map[String, String], line: String) = {
      @tailrec
      def replace(properties: Map[String, String], line: String): String = {
        properties.headOption match {
          case Some(property) =>
            val (key, value) = property
            val name = s"$${$key}"
            replace(properties.tail, if (line.contains(name)) line.replace(name, value) else line)
          case None => line
        }
      }
      replace(properties, line)
    }

    log.debug(s"Loading properties file $propertiesFileName")
    val content = IO.readLines(origin) map (replaceLine(propertiesMap, _))
    IO.write(target, content.mkString("\n"))
  }

}