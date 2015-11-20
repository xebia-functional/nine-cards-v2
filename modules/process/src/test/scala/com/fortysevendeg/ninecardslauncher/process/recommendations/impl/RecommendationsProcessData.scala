package com.fortysevendeg.ninecardslauncher.process.recommendations.impl

import com.fortysevendeg.ninecardslauncher.process.commons.types.Social
import com.fortysevendeg.ninecardslauncher.services.api.RequestConfig
import com.fortysevendeg.ninecardslauncher.services.api.models._

import scala.util.Random

trait RecommendationsProcessData {

  val requestConfig = RequestConfig("fake-device-id", "fake-token")

  val statusCodeOk = 200

  val category = Social

  val categories = Seq(Social.name)

  val likePackages = Seq("com.fortysevendeg.package1", "com.fortysevendeg.package2", "com.fortysevendeg.package3")

  val limit = 20

  def generateGooglePlayApps() = 1 to 10 map { i =>
    GooglePlayApp(
      docid = Random.nextString(10),
      title = Random.nextString(10),
      creator = Random.nextString(10),
      descriptionHtml = None,
      icon = None,
      background = None,
      screenshots = Seq.empty,
      video = None,
      details = GooglePlayDetails(generateGooglePlayDetails()),
      offer = Seq.empty,
      aggregateRating = generateGooglePlayAggregateRating())
  }

  def generateGooglePlayDetails(
    appCategory: Seq[String] = categories,
    numDownloads: String = Random.nextInt(1000).toString,
    developerEmail: Option[String] = None,
    developerName: Option[String] = None,
    developerWebsite: Option[String] = None,
    versionCode: Option[Int] = None,
    versionString: Option[String] = None,
    appType: Option[String] = None,
    permission: Seq[String] = Seq.empty) =
    GooglePlayAppDetails(
      appCategory,
      numDownloads,
      developerEmail,
      developerName,
      developerWebsite,
      versionCode,
      versionString,
      appType,
      permission)

  def generateGooglePlayAggregateRating(
    ratingsCount: Int = Random.nextInt(10),
    commentCount: Option[Int] = None,
    oneStarRatings: Int = Random.nextInt(10),
    twoStarRatings: Int = Random.nextInt(10),
    threeStarRatings: Int = Random.nextInt(10),
    fourStarRatings: Int = Random.nextInt(10),
    fiveStarRatings: Int = Random.nextInt(10),
    starRating: Double = Random.nextDouble() * 5) =
    GooglePlayAggregateRating(
      ratingsCount,
      commentCount,
      oneStarRatings,
      twoStarRatings,
      threeStarRatings,
      fourStarRatings,
      fiveStarRatings,
      starRating)

}
