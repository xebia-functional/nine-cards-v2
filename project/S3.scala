import java.util.{Calendar, TimeZone, GregorianCalendar, Date}

import com.typesafe.sbt.S3Plugin.S3._
import com.typesafe.sbt.S3Plugin._
import sbt.Keys._
import sbt._

object S3 {

  lazy val bucketName = sys.env.getOrElse("AWS_BUCKET", "")
  lazy val pullRequest = sys.env.getOrElse("GIT_PR", "false")
  lazy val hostName = s"$bucketName.s3.amazonaws.com"
  lazy val apkName = pullRequest match {
    case "false" => s"nine-cards-v2-latest.apk"
    case number => s"nine-cards-v2-$number.apk"
  }

  def getExpirationDate: Date = {
    val c = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
    c.add(Calendar.DAY_OF_YEAR, 30)
    c.getTime
  }

  lazy val customS3Settings = s3Settings ++ Seq(
    host in upload := hostName,
    host in generateLink := hostName,
    progress in upload := true,
    credentials += Credentials(
      "Amazon S3",
      hostName,
      sys.env.getOrElse("AWS_ACCESS_KEY_ID", ""),
      sys.env.getOrElse("AWS_SECRET_KEY", "")),
    expirationDate in generateLink := getExpirationDate,
    mappings in upload := Seq((target.value / "android" / "output" / "nine-cards-v2-debug.apk", apkName)),
    keys in generateLink := Seq(apkName))


}