package com.fortysevendeg.ninecardslauncher.api.version1.services

import com.fortysevendeg.ninecardslauncher.api.version1.model._
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

}
