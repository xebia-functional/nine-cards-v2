package com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl

import com.fortysevendeg.ninecardslauncher.process.sharedcollections.TopSharedCollection
import com.fortysevendeg.ninecardslauncher.services.api.{RequestConfig, SharedCollectionResponseList, SharedCollectionResponse}
import scala.util.Random

trait SharedCollectionsProcessImplData {

  val requestConfig = RequestConfig("fake-device-id", "fake-token")

  val category = "COMMUNICATION"

  val typeShareCollection = TopSharedCollection

  val offset = 0

  val limit = 50

  val statusCodeOk = 200

  def generateSharedCollectionResponse() = 1 to 10 map { i =>
    SharedCollectionResponse(
      id = Random.nextString(10),
      sharedCollectionId = Random.nextString(10),
      publishedOn = Random.nextLong(),
      description = Random.nextString(10),
      icon = Random.nextString(10),
      screenshots = Seq.empty,
      author = Random.nextString(10),
      tags = Seq.empty,
      name = Random.nextString(10),
      shareLink = Random.nextString(10),
      packages = Seq.empty,
      resolvedPackages = Seq.empty,
      views = Random.nextInt(),
      category = Random.nextString(10),
      community = Random.nextBoolean())
  }

  val shareCollectionList = SharedCollectionResponseList(
    statusCode = statusCodeOk,
    items = generateSharedCollectionResponse())

}
