package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model.{SharedCollectionSubscription, SharedCollectionList, SharedCollection}
import com.fortysevendeg.rest.client.ServiceClient
import play.api.libs.json.{Writes, Reads}

import scala.concurrent.ExecutionContext

class ApiSharedCollectionsService(serviceClient: ServiceClient) {

  val prefixPathCollections = "/ninecards/collections"

  def shareCollection(
      sharedCollection: SharedCollection,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollection], writes: Writes[SharedCollection]) =
    serviceClient.post[SharedCollection, SharedCollection](
      path = prefixPathCollections,
      headers = headers,
      body = sharedCollection,
      Some(reads))

  def getSharedCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollection]) =
    serviceClient.get[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId",
      headers = headers,
      Some(reads))

  def getSharedCollectionList(
      collectionType: String,
      offset: Int,
      limit: Int,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollectionList]) =
    serviceClient.get[SharedCollectionList](
      path = s"$prefixPathCollections/$collectionType/$offset/$limit",
      headers = headers,
      reads = Some(reads))

  def getSharedCollectionListByCategory(
      collectionType: String,
      category: String,
      offset: Int,
      limit: Int,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollectionList]) =
    serviceClient.get[SharedCollectionList](
      path = s"$prefixPathCollections/$collectionType/$category/$offset/$limit",
      headers = headers,
      reads = Some(reads))

  def searchSharedCollection(
      keywords: String,
      offset: Int,
      limit: Int,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollectionList]) =
    serviceClient.get[SharedCollectionList](
      path = s"$prefixPathCollections/search/$keywords/$offset/$limit",
      headers = headers,
      reads = Some(reads))

  def rateSharedCollection(
      sharedCollectionId: String,
      rate: Double,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollection]) =
    serviceClient.emptyPost[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/rate/$rate",
      headers = headers,
      Some(reads))

  def subscribeSharedCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollectionSubscription]) =
    serviceClient.emptyPut[SharedCollectionSubscription](
      path = s"$prefixPathCollections/$sharedCollectionId/subscribe",
      headers = headers,
      Some(reads))

  def unsubscribeSharedCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext) =
    serviceClient.delete(
      path = s"$prefixPathCollections/$sharedCollectionId/subscribe",
      headers = headers,
      emptyResponse = true)

  def notifyViewCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollection]) =
    serviceClient.emptyPut[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/notifyViews",
      headers = headers,
      reads = Some(reads))

  def notifyInstallCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollection]) =
    serviceClient.emptyPut[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/notifyInstall",
      headers = headers,
      reads = Some(reads))

}
