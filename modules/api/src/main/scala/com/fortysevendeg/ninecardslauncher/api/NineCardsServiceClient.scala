package com.fortysevendeg.ninecardslauncher.api

import com.fortysevendeg.ninecardslauncher.api.model.{SharedCollection, SharedCollectionList, SharedCollectionSubscription}
import com.fortysevendeg.ninecardslauncher.api.reads.SharedCollectionImplicits
import com.fortysevendeg.rest.client.ServiceClient

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
    emptyPost[SharedCollection](
      path = s"$prefixPath/$sharedCollectionId/rate/$rate")

  def subscribeSharedCollection(sharedCollectionId: Long) =
    emptyPut[SharedCollectionSubscription](
      path = s"$prefixPath/$sharedCollectionId/subscribe")

  def unsubscribeSharedCollection(sharedCollectionId: Long) =
    delete[String](
      path = s"$prefixPath/$sharedCollectionId/subscribe")

  def notifyViewCollection(sharedCollectionId: Long) =
    emptyPut[SharedCollection](
      path = s"$prefixPath/$sharedCollectionId/notifyViews")

  def notifyInstallCollection(sharedCollectionId: Long) =
    emptyPut[SharedCollection](
      path = s"$prefixPath/$sharedCollectionId/notifyInstall")

}
