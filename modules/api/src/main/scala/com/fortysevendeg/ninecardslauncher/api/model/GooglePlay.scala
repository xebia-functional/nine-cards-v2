package com.fortysevendeg.ninecardslauncher.api.model

case class GooglePlayPackage(docV2: GooglePlayApp)

case class GooglePlayApp(
    docid: String,
    title: String,
    creator: String,
    descriptionHtml: String,
    image: Seq[GooglePlayImage],
    details: GooglePlayDetails,
    offer: Seq[GooglePlayOffer],
    aggregateRating: GooglePlayAggregateRating)

case class GooglePlayImage(
    imageType: Int,
    imageUrl: String,
    creator: String)

case class GooglePlayDetails(
    appCategory: Seq[String],
    numDownloads: String,
    developerEmail: String,
    developerName: String,
    developerWebsite: String,
    versionCode: String,
    versionString: String,
    appType: String,
    permission: Seq[String])

case class GooglePlayOffer(
    formattedAmount: String,
    micros: Long)

case class GooglePlayAggregateRating(
    ratingsCount: Int,
    commentCount: Int,
    oneStarRatings: Int,
    twoStarRatings: Int,
    threeStarRatings: Int,
    fourStarRatings: Int,
    fiveStarRatings: Int,
    rStarRatings: Int,
    starRating: Double)