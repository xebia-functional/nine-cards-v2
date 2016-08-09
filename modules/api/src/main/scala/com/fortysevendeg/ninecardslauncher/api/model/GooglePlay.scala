package com.fortysevendeg.ninecardslauncher.api.model

case class GooglePlayPackage(docV2: GooglePlayApp)

case class GooglePlayApp(
    docid: String,
    title: String,
    creator: String,
    descriptionHtml: Option[String],
    image: Seq[GooglePlayImage],
    details: GooglePlayDetails,
    offer: Seq[GooglePlayOffer],
    aggregateRating: GooglePlayAggregateRating)

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
    versionCode: Option[Int],
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

case class PackagesRequest(items: Seq[String])

case class GooglePlayPackages(
    errors: Seq[String],
    items: Seq[GooglePlayPackage])