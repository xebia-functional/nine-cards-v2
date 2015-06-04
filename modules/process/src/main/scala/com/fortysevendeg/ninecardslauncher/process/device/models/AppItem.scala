package com.fortysevendeg.ninecardslauncher.process.device.models

import scala.util.{Failure, Success, Try}

case class AppItem(
  name: String,
  packageName: String,
  imagePath: String,
  intent: String,
  category: Option[String] = None,
  starRating: Double = .0,
  numDownloads: Option[String] = None,
  ratingsCount: Int = 0,
  commentCount: Int = 0,
  micros: Int = 0) {

  val AvgDownloadsDefault = 1.0

  def getMFIndex: Double = {
    val avgDownloads = Try {
      val nDownloads = numDownloads map (nd => nd.replace(",", "").replace("+", "").toDouble) getOrElse AvgDownloadsDefault
      nDownloads match {
        case nd if nd >= 10000000 => 5
        case nd if nd >= 5000000 => 4.5
        case nd if nd >= 1000000 => 4
        case nd if nd >= 500000 => 3.5
        case nd if nd >= 100000 => 3
        case nd if nd >= 50000 => 2
        case _ => 1
      }
    } match {
      case Success(nd) => nd
      case Failure(ex) => AvgDownloadsDefault
    }

    val avgRatingsCount = ratingsCount match {
      case rc if rc >= 500000 => 5
      case rc if rc >= 100000 => 4.5
      case rc if rc >= 50000 => 4
      case rc if rc >= 25000 => 3.5
      case rc if rc >= 10000 => 3
      case rc if rc >= 1000 => 2.5
      case rc if rc >= 500 => 2
      case _ => 1
    }

    val avgCommentCount = commentCount match {
      case cc if cc >= 200000 => 5
      case cc if cc >= 100000 => 4.5
      case cc if cc >= 50000 => 4
      case cc if cc >= 10000 => 3.5
      case cc if cc >= 5000 => 3
      case cc if cc >= 1000 => 2.5
      case cc if cc >= 500 => 2
      case _ => 1
    }

    (starRating + avgDownloads + avgRatingsCount + avgCommentCount) / 4

  }
}
