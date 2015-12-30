import java.io.{File, FileInputStream}
import java.util.Properties

import android.Keys._
import sbt.Keys._
import sbt._

import scala.annotation.tailrec
import scala.collection.JavaConverters._

object ReplacePropertiesGenerator {

  val debugPropertiesFile = "debug.properties"

  val releasePropertiesFile = "release.properties"

  var debug = true

  lazy val propertiesMap: Map[String, String] = {
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

  def replaceContent(origin: File, target: File) = {
    val content = IO.readLines(origin) map (replaceLine(propertiesMap, _))
    IO.write(target, content.mkString("\n"))
  }

  private def replaceLine(properties: Map[String, String], line: String) = {
    @tailrec
    def replace(properties: Map[String, String], line: String): String = {
      properties.headOption match {
        case Some(property) =>
          val (key, value) = property
          val name = namePropertyInConfig(key)
          replace(properties.tail, if (line.contains(name)) line.replace(name, value) else line)
        case None => line
      }
    }
    replace(properties, line)
  }

  def replaceValuesTask = Def.task[Seq[File]] {
    val log = streams.value.log
    println("Replacing values")
    try {
      val dir: (File, File) = (collectResources in Android).value
      val valuesFile: File =  new File(dir._2, "/values/values.xml")
      replaceContent(valuesFile, valuesFile)
      Seq(valuesFile)
    } catch {
      case e: Throwable =>
        log.error("An error occurred replacing values")
        throw e
    }
  }

  def setDebugTask(debug: Boolean) = Def.task[Unit] {
    this.debug = debug
  }

}
