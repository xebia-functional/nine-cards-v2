import java.util.{Calendar, TimeZone, GregorianCalendar, Date}

import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.{HttpMethod, Protocol, ClientConfiguration}
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import sbt.Keys._
import sbt._

import scala.util.{Failure, Success, Try}

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

  val upload = TaskKey[Unit]("s3-upload", "Uploads files to an S3 bucket.")

  val generateLink = TaskKey[Option[String]]("s3-generateLink","Uploads files to an S3 bucket.")

  lazy val credentials = new BasicAWSCredentials(
    sys.env.getOrElse("AWS_ACCESS_KEY_ID", ""),
    sys.env.getOrElse("AWS_SECRET_KEY", ""))

  lazy val client = new AmazonS3Client(credentials, new ClientConfiguration().withProtocol(Protocol.HTTPS))

  lazy val customS3Settings = Seq(
    upload := {
      val log = streams.value.log
      log.info("Uploading APK")
      val file = target.value / "android" / "output" / "nine-cards-v2-debug.apk"
      Try(client.putObject(bucketName, apkName, file)) match {
        case Success(r) => log.info("APK successfully uploaded")
        case Failure(e) =>
          log.error("Error uploading APK")
          log.trace(e)
      }
    },
    generateLink := {
      val log = streams.value.log
      val request = new GeneratePresignedUrlRequest(bucketName, apkName)
      request.setMethod(HttpMethod.GET)
      request.setExpiration(getExpirationDate)
      val url = Try(client.generatePresignedUrl(request)) match {
        case Success(result) =>
          log.info(s"URL successfully created: $result")
          Option(result.toString)
        case Failure(e) =>
          log.error("Error creating URL")
          log.trace(e)
          None
      }
      url
    }

  )


}