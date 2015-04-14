package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model.{SharedCollectionSubscription, SharedCollectionList, SharedCollection}
import com.fortysevendeg.ninecardslauncher.api.reads.SharedCollectionImplicits
import com.fortysevendeg.rest.client.ServiceClient
import play.api.libs.json.Reads

import scala.concurrent.ExecutionContext

trait SharedCollectionsServiceClient
    extends ServiceClient
    with SharedCollectionImplicits {

  val prefixPathCollections = "/ninecards/collections"

  def shareCollection(
      sharedCollection: SharedCollection,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollection]) =
    post[SharedCollection, SharedCollection](
      path = prefixPathCollections,
      headers = headers,
      body = sharedCollection,
      Some(reads))

  def getSharedCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollection]) =
    get[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId",
      headers = headers,
      Some(reads))

  def getSharedCollectionList(
      collectionType: String,
      offset: Int,
      limit: Int,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollectionList]) =
    get[SharedCollectionList](
      path = s"$prefixPathCollections/$collectionType/$offset/$limit",
      headers = headers,
      reads = Some(reads))

  def getSharedCollectionListByCategory(
      collectionType: String,
      category: String,
      offset: Int,
      limit: Int,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollectionList]) =
    get[SharedCollectionList](
      path = s"$prefixPathCollections/$collectionType/$category/$offset/$limit",
      headers = headers,
      reads = Some(reads))

  def searchSharedCollection(
      keywords: String,
      offset: Int,
      limit: Int,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollectionList]) =
    get[SharedCollectionList](
      path = s"$prefixPathCollections/search/$keywords/$offset/$limit",
      headers = headers,
      reads = Some(reads))

  def rateSharedCollection(
      sharedCollectionId: String,
      rate: Double,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollection]) =
    emptyPost[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/rate/$rate",
      headers = headers,
      Some(reads))

  def subscribeSharedCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollectionSubscription]) =
    emptyPut[SharedCollectionSubscription](
      path = s"$prefixPathCollections/$sharedCollectionId/subscribe",
      headers = headers,
      Some(reads))

  def unsubscribeSharedCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext) =
    delete[Unit](
      path = s"$prefixPathCollections/$sharedCollectionId/subscribe",
      headers = headers)

  def notifyViewCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollection]) =
    emptyPut[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/notifyViews",
      headers = headers,
      reads = Some(reads))

  def notifyInstallCollection(
      sharedCollectionId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[SharedCollection]) =
    emptyPut[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/notifyInstall",
      headers = headers,
      reads = Some(reads))

}
