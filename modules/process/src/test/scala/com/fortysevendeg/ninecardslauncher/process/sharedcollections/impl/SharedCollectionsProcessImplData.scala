package com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl

import com.fortysevendeg.ninecardslauncher.process.commons.types.Communication
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.TopSharedCollection
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{CreateSharedCollection, SharedCollection}
import com.fortysevendeg.ninecardslauncher.services.api.{CreateSharedCollectionResponse, RequestConfig, SharedCollectionResponse, SharedCollectionResponseList, CreateSharedCollection => ApiCreateSharedCollection}

import scala.util.Random

trait SharedCollectionsProcessImplData {

  val requestConfig = RequestConfig("fake-device-id", "fake-token", Some("fake-android-token"))

  val category = Communication

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
      category = Communication.name,
      community = Random.nextBoolean())
  }

  val shareCollectionList = SharedCollectionResponseList(
    statusCode = statusCodeOk,
    items = generateSharedCollectionResponse())

  def generateSharedCollection =
    SharedCollection(
      id = Random.nextString(10),
      sharedCollectionId = Random.nextString(10),
      publishedOn = Random.nextLong(),
      description = Random.nextString(10),
      screenshots = Seq.empty,
      author = Random.nextString(10),
      tags = Seq.empty,
      name = Random.nextString(10),
      shareLink = Random.nextString(10),
      packages = Seq.empty,
      resolvedPackages = Seq.empty,
      views = Random.nextInt(),
      category = Communication,
      icon = Random.nextString(10),
      community = Random.nextBoolean())

  val sharedCollection = generateSharedCollection

  def generateCreateSharedCollection =
    CreateSharedCollection(
      description = Random.nextString(10),
      author = Random.nextString(10),
      name = Random.nextString(10),
      packages = Seq.empty,
      category = Communication,
      icon = Random.nextString(10),
      community = Random.nextBoolean())

  val createSharedCollection = generateCreateSharedCollection

  val createSharedCollectionResponse ={
    CreateSharedCollectionResponse(
      statusCode = statusCodeOk,
      newSharedCollection = ApiCreateSharedCollection(
        name = sharedCollection.name,
        description = sharedCollection.description,
        author = sharedCollection.author,
        packages = sharedCollection.packages,
        category = sharedCollection.category.name,
        shareLink = sharedCollection.shareLink,
        icon = sharedCollection.icon,
        community = sharedCollection.community
      )
    )
  }
}
