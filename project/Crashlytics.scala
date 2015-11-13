import Libraries.android._
import Libraries.graphics._
import Libraries.json._
import Libraries.macroid._
import Libraries.net._
import Libraries.playServices._
import Libraries.scala._
import Libraries.test._
import Libraries.debug._
import android.Keys._
import S3._
import de.johoop.ant4sbt.Ant4Sbt._
import sbt.Keys._
import sbt._

object Crashlytics {

  lazy val crashlyticsSettings = antSettings ++ Seq(
    antBuildFile := baseDirectory.value / "crashlytics_build_base.xml"
  )

  addAntTasks("test1")

  compile <<= (compile in Compile) dependsOn antTaskKey("test1")


}