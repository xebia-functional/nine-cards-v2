package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model._
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import com.fortysevendeg.rest.client.{ServiceClient, ServiceClientException}
import play.api.libs.json.{Reads, Writes}

class ApiSharedCollectionsService(serviceClient: ServiceClient) {

  val prefixPathCollections = "/ninecards/collections"

  def shareCollection(
    sharedCollection: ShareCollection,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[SharedCollection],
    writes: Writes[ShareCollection]): ServiceDef2[ServiceClientResponse[SharedCollection], HttpClientException with ServiceClientException] =
    serviceClient.post[ShareCollection, SharedCollection](
      path = prefixPathCollections,
      headers = headers,
      body = sharedCollection,
      Some(reads))

  def getSharedCollection(
    sharedCollectionId: String,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[SharedCollection]): ServiceDef2[ServiceClientResponse[SharedCollection], HttpClientException with ServiceClientException] =
    serviceClient.get[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId",
      headers = headers,
      Some(reads))

  def getSharedCollectionList(
    collectionType: String,
    offset: Int,
    limit: Int,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[SharedCollectionList]): ServiceDef2[ServiceClientResponse[SharedCollectionList], HttpClientException with ServiceClientException] =
    serviceClient.get[SharedCollectionList](
      path = s"$prefixPathCollections/$collectionType/$offset/$limit",
      headers = headers,
      reads = Some(reads))

  def getSharedCollectionListByCategory(
    collectionType: String,
    category: String,
    offset: Int,
    limit: Int,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[SharedCollectionList]): ServiceDef2[ServiceClientResponse[SharedCollectionList], HttpClientException with ServiceClientException] =
    serviceClient.get[SharedCollectionList](
      path = s"$prefixPathCollections/$collectionType/$category/$offset/$limit",
      headers = headers,
      reads = Some(reads))

  def searchSharedCollection(
    keywords: String,
    offset: Int,
    limit: Int,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[SharedCollectionList]): ServiceDef2[ServiceClientResponse[SharedCollectionList], HttpClientException with ServiceClientException] =
    serviceClient.get[SharedCollectionList](
      path = s"$prefixPathCollections/search/$keywords/$offset/$limit",
      headers = headers,
      reads = Some(reads))

  def rateSharedCollection(
    sharedCollectionId: String,
    rate: Double,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[SharedCollection]): ServiceDef2[ServiceClientResponse[SharedCollection], HttpClientException with ServiceClientException] =
    serviceClient.emptyPost[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/rate/$rate",
      headers = headers,
      Some(reads))

  def subscribeSharedCollection(
    sharedCollectionId: String,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[SharedCollectionSubscription]): ServiceDef2[ServiceClientResponse[SharedCollectionSubscription], HttpClientException with ServiceClientException] =
    serviceClient.emptyPut[SharedCollectionSubscription](
      path = s"$prefixPathCollections/$sharedCollectionId/subscribe",
      headers = headers,
      Some(reads))

  def unsubscribeSharedCollection(
    sharedCollectionId: String,
    headers: Seq[(String, String)]
    ): ServiceDef2[ServiceClientResponse[Nothing], HttpClientException with ServiceClientException] =
    serviceClient.delete(
      path = s"$prefixPathCollections/$sharedCollectionId/subscribe",
      headers = headers,
      emptyResponse = true)

  def notifyViewCollection(
    sharedCollectionId: String,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[SharedCollection]): ServiceDef2[ServiceClientResponse[SharedCollection], HttpClientException with ServiceClientException] =
    serviceClient.emptyPut[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/notifyViews",
      headers = headers,
      reads = Some(reads))

  def notifyInstallCollection(
    sharedCollectionId: String,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[SharedCollection]): ServiceDef2[ServiceClientResponse[SharedCollection], HttpClientException with ServiceClientException] =
    serviceClient.emptyPut[SharedCollection](
      path = s"$prefixPathCollections/$sharedCollectionId/notifyInstall",
      headers = headers,
      reads = Some(reads))

}
