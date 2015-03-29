package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model.{SharedCollectionSubscription, SharedCollectionList, SharedCollection}
import com.fortysevendeg.ninecardslauncher.api.reads.SharedCollectionImplicits
import com.fortysevendeg.rest.client.ServiceClient

trait SharedCollectionsServiceClient
    extends ServiceClient
    with SharedCollectionImplicits {

  val prefixPathCollections = "/ninecards/collections"

  def shareCollection(
      sharedCollection: SharedCollection,
      headers: Seq[(String, String)]) =
    post[SharedCollection, SharedCollection](
      path = prefixPathCollections,
      headers = headers,
      body = sharedCollection)

  def getSharedCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)]) =
    get[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId",
      headers = headers)

  def getSharedCollectionList(
      collectionType: String,
      offset: Int,
      limit: Int,
      headers: Seq[(String, String)]) =
    get[SharedCollectionList](
      path = s"$prefixPathCollections/$collectionType/$offset/$limit",
      headers = headers)

  def getSharedCollectionListByCategory(
      collectionType: String,
      category: String,
      offset: Int,
      limit: Int,
      headers: Seq[(String, String)]) =
    get[SharedCollectionList](
      path = s"$prefixPathCollections/$collectionType/$category/$offset/$limit",
      headers = headers)

  def searchSharedCollection(
      keywords: String,
      offset: Int,
      limit: Int,
      headers: Seq[(String, String)]) =
    get[SharedCollectionList](
      path = s"$prefixPathCollections/search/$keywords/$offset/$limit",
      headers = headers)

  def rateSharedCollection(
      sharedCollectionId: String,
      rate: Double,
      headers: Seq[(String, String)]) =
    emptyPost[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/rate/$rate",
      headers = headers)

  def subscribeSharedCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)]) =
    emptyPut[SharedCollectionSubscription](
      path = s"$prefixPathCollections/$sharedCollectionId/subscribe",
      headers = headers)

  def unsubscribeSharedCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)]) =
    delete[Unit](
      path = s"$prefixPathCollections/$sharedCollectionId/subscribe",
      headers = headers)

  def notifyViewCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)]) =
    emptyPut[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/notifyViews",
      headers = headers)

  def notifyInstallCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)]) =
    emptyPut[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/notifyInstall",
      headers = headers)

}
