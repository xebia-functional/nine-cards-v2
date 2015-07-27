package com.fortysevendeg.ninecardslauncher.process.collection.utils

import com.fortysevendeg.ninecardslauncher.process.collection.models.UnformedItem

import scala.util.{Failure, Success, Try}

object NineCardAppUtils {

  val avgDownloadsDefault = 1.0

  def mfIndex(appItem: UnformedItem): Double = {
    val avgDownloads = Try {
      val nDownloads = appItem.numDownloads.replace(",", "").replace("+", "").toDouble
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
      case Failure(ex) => avgDownloadsDefault
    }

    val avgRatingsCount = appItem.ratingsCount match {
      case rc if rc >= 500000 => 5
      case rc if rc >= 100000 => 4.5
      case rc if rc >= 50000 => 4
      case rc if rc >= 25000 => 3.5
      case rc if rc >= 10000 => 3
      case rc if rc >= 1000 => 2.5
      case rc if rc >= 500 => 2
      case _ => 1
    }

    val avgCommentCount = appItem.commentCount match {
      case cc if cc >= 200000 => 5
      case cc if cc >= 100000 => 4.5
      case cc if cc >= 50000 => 4
      case cc if cc >= 10000 => 3.5
      case cc if cc >= 5000 => 3
      case cc if cc >= 1000 => 2.5
      case cc if cc >= 500 => 2
      case _ => 1
    }

    (appItem.starRating + avgDownloads + avgRatingsCount + avgCommentCount) / 4

  }

}

