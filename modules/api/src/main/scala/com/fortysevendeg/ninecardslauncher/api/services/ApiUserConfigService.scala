package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model.{UserConfig, UserConfigDevice, UserConfigGeoInfo}
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.rest.client.{ServiceClientException, ServiceClient}
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import play.api.libs.json.{Reads, Writes}

class ApiUserConfigService(serviceClient: ServiceClient) {

  val prefixPathUserConfig = "/ninecards/userconfig"

  def getUserConfig(
    headers: Seq[(String, String)]
    )(implicit reads: Reads[UserConfig]): ServiceDef2[ServiceClientResponse[UserConfig], HttpClientException with ServiceClientException] =
    serviceClient.get[UserConfig](
      path = prefixPathUserConfig,
      headers = headers,
      reads = Some(reads))

  def saveDevice(
    device: UserConfigDevice,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[UserConfig],
    writes: Writes[UserConfigDevice]): ServiceDef2[ServiceClientResponse[UserConfig], HttpClientException with ServiceClientException] =
    serviceClient.put[UserConfigDevice, UserConfig](
      path = s"$prefixPathUserConfig/device",
      headers = headers,
      body = device,
      reads = Some(reads))

  def saveGeoInfo(
    geoInfo: UserConfigGeoInfo,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[UserConfig],
    writes: Writes[UserConfigGeoInfo]): ServiceDef2[ServiceClientResponse[UserConfig], HttpClientException with ServiceClientException] =
    serviceClient.put[UserConfigGeoInfo, UserConfig](
      path = s"$prefixPathUserConfig/geoInfo",
      headers = headers,
      body = geoInfo,
      reads = Some(reads))

  def checkpointPurchaseProduct(
    product: String,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[UserConfig]): ServiceDef2[ServiceClientResponse[UserConfig], HttpClientException with ServiceClientException] =
    serviceClient.emptyPut[UserConfig](
      path = s"$prefixPathUserConfig/checkpoint/purchase/$product",
      headers = headers,
      reads = Some(reads))

  def checkpointCustomCollection(
    headers: Seq[(String, String)]
    )(implicit reads: Reads[UserConfig]): ServiceDef2[ServiceClientResponse[UserConfig], HttpClientException with ServiceClientException] =
    serviceClient.emptyPut[UserConfig](
      path = s"$prefixPathUserConfig/checkpoint/collection",
      headers = headers,
      reads = Some(reads))

  def checkpointJoinedBy(
    otherConfigId: String,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[UserConfig]): ServiceDef2[ServiceClientResponse[UserConfig], HttpClientException with ServiceClientException] =
    serviceClient.emptyPut[UserConfig](
      path = s"$prefixPathUserConfig/checkpoint/joined/$otherConfigId",
      headers = headers,
      reads = Some(reads))

  def tester(
    replace: Map[String, String],
    headers: Seq[(String, String)]
    )(implicit reads: Reads[UserConfig]): ServiceDef2[ServiceClientResponse[UserConfig], HttpClientException with ServiceClientException] =
    serviceClient.put[Map[String, String], UserConfig](
      path = s"$prefixPathUserConfig/tester",
      headers = headers,
      body = replace,
      reads = Some(reads))

}
