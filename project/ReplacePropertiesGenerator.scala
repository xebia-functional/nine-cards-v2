import java.io.{File, FileInputStream}
import java.util.Properties

import android.Keys._
import sbt._

import scala.annotation.tailrec
import scala.collection.JavaConverters._

object ReplacePropertiesGenerator {

  val debugPropertiesFile = "debug.properties"

  val releasePropertiesFile = "release.properties"

  var debug = true

  def propertiesMap(): Map[String, String] = {
    (loadPropertiesFile map { file =>
      val properties = new Properties()
      properties.load(new FileInputStream(file))
      properties.asScala.toMap
    }) getOrElse Map.empty
  }

  private def namePropertyInConfig(name: String) = s"$${$name}"

  private def loadPropertiesFile: Option[File] = {
    val file = new File(if (debug) debugPropertiesFile else releasePropertiesFile)
    if (file.exists()) Some(file) else None
  }

  def replaceContent(valuesFile: File) = {
    val properties = propertiesMap()
    val content = IO.readLines(valuesFile) map (replaceLine(properties, _))
    IO.write(valuesFile, content.mkString("\n"))
  }

  private def replaceLine(properties: Map[String, String], line: String) = {
    @tailrec
    def replace(properties: Map[String, String], line: String): String = {
      if (properties.isEmpty) {
        line
      } else {
        val (key, value) = properties.head
        val name = namePropertyInConfig(key)
        replace(properties.tail, if (line.contains(name)) line.replace(name, value) else line)
      }
    }
    replace(properties, line)
  }

  def replaceValuesTask = Def.task[Seq[File]] {
    try {
      val dir: (File, File) = (collectResources in Android).value
      val valuesFile: File =  new File(dir._2, "/values/values.xml")
      replaceContent(valuesFile)
      Seq(valuesFile)
    } catch {
      case e: Throwable =>
        println("An error occurred loading values.xml")
        throw e
    }
  }

  def setDebugTask(debug: Boolean) = Def.task[Unit] {
    this.debug = debug
  }

}
