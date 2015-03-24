package com.fortysevendeg.ninecardslauncher.api

import com.fortysevendeg.ninecardslauncher.api.model.{SharedCollectionSubscription, SharedCollectionList, SharedCollection}
import com.fortysevendeg.ninecardslauncher.api.reads.SharedCollectionImplicits
import com.fortysevendeg.rest.client.{BodyContent, ServiceClient}

trait NineCardsServiceClient
  extends ServiceClient
  with SharedCollectionImplicits {
  
  val prefixPath = "/ninecards/collections"

  def shareCollection(sharedCollection: SharedCollection) =
    post[SharedCollection, SharedCollection](
      path = prefixPath,
      body = sharedCollection)

  def getSharedCollection(sharedCollectionId: String) =
    get[SharedCollection](
      path = s"$prefixPath/$sharedCollectionId")

  def getSharedCollectionList(collectionType: String, offset: Int, limit: Int) =
    get[SharedCollectionList](
      path = s"$prefixPath/$collectionType/$offset/$limit")

  def getSharedCollectionListByCategory(collectionType: String, category: String, offset: Int, limit: Int) =
    get[SharedCollectionList](
      path = s"$prefixPath/$collectionType/$category/$offset/$limit")

  def searchSharedCollection(keywords: String, offset: Int, limit: Int) =
    get[SharedCollectionList](
      path = s"$prefixPath/search/$keywords/$offset/$limit")

  def rateSharedCollection(sharedCollectionId: Long, rate: Double) =
    rawBodyPost[SharedCollection](
      path = s"$prefixPath/$sharedCollectionId/rate/$rate",
      rawBody = BodyContent.emptyTextBody)

  def subscribeSharedCollection(sharedCollectionId: Long) =
    rawBodyPut[SharedCollectionSubscription](
      path = s"$prefixPath/$sharedCollectionId/subscribe",
      rawBody = BodyContent.emptyTextBody)

  def unsubscribeSharedCollection(sharedCollectionId: Long) =
    delete[String](
      path = s"$prefixPath/$sharedCollectionId/subscribe")

  def notifyViewCollection(sharedCollectionId: Long) =
    rawBodyPut[SharedCollection](
      path = s"$prefixPath/$sharedCollectionId/notifyViews",
      rawBody = BodyContent.emptyTextBody)

  def notifyInstallCollection(sharedCollectionId: Long) =
    rawBodyPut[SharedCollection](
      path = s"$prefixPath/$sharedCollectionId/notifyInstall",
      rawBody = BodyContent.emptyTextBody)

}
