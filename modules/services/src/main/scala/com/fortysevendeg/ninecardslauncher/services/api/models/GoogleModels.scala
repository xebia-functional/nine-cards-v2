package com.fortysevendeg.ninecardslauncher.services.api.models

case class GooglePlayPackage(app: GooglePlayApp)

case class GoogleDevice(
    name: String,
    deviceId: String,
    secretToken: String,
    permissions: Seq[String])

case class GooglePlayApp(
    docid: String,
    title: String,
    creator: String,
    descriptionHtml: Option[String],
    image: Seq[GooglePlayImage],
    details: GooglePlayDetails,
    offer: Seq[GooglePlayOffer],
    aggregateRating: GooglePlayAggregateRating) {

    val IconImageType = 4

    val IconBackgroundType = 2

    val IconScreenShootType = 1

    val IconVideoType = 3

    def getIcon: Option[String] = image.find(_.imageType == IconImageType) map (_.imageUrl)

    def getBackground: Option[String] = image.find(_.imageType == IconBackgroundType) map (_.imageUrl)

    def getScreenShoots: Seq[String] = image.filter(_.imageType == IconScreenShootType) map (_.imageUrl)

    def getVideo: Option[String] = image.find(_.imageType == IconVideoType) map (_.imageUrl)

}

case class GooglePlayImage(
    imageType: Int,
    imageUrl: String,
    creator: Option[String])

case class GooglePlayDetails(
    appDetails: GooglePlayAppDetails)

case class GooglePlayAppDetails(
    appCategory: Seq[String],
    numDownloads: String,
    developerEmail: Option[String],
    developerName: Option[String],
    developerWebsite: Option[String],
    versionCode: Int,
    versionString: Option[String],
    appType: Option[String],
    permission: Seq[String])

case class GooglePlayOffer(
    formattedAmount: String,
    micros: Long)

case class GooglePlayAggregateRating(
    ratingsCount: Int,
    commentCount: Option[Int],
    oneStarRatings: Int,
    twoStarRatings: Int,
    threeStarRatings: Int,
    fourStarRatings: Int,
    fiveStarRatings: Int,
    starRating: Double)

case class GooglePlaySimplePackages(
    errors: Seq[String],
    items: Seq[GooglePlaySimplePackage])

case class GooglePlaySimplePackage(
    packageName: String,
    appType: String,
    appCategory: String,
    numDownloads: String,
    starRating: Double,
    ratingCount: Int,
    commentCount: Int)

