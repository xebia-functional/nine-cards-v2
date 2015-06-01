package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model.{UserConfig, UserConfigDevice, UserConfigGeoInfo}
import com.fortysevendeg.rest.client.ServiceClient
import play.api.libs.json.{Reads, Writes}

import scala.concurrent.ExecutionContext

class ApiUserConfigService(serviceClient: ServiceClient) {

  val prefixPathUserConfig = "/ninecards/userconfig"

  def getUserConfig(headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig]) =
    serviceClient.get[UserConfig](
      path = prefixPathUserConfig,
      headers = headers,
      reads = Some(reads))

  def saveDevice(
      device: UserConfigDevice,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig], writes: Writes[UserConfigDevice]) =
    serviceClient.put[UserConfigDevice, UserConfig](
      path = s"$prefixPathUserConfig/device",
      headers = headers,
      body = device,
      reads = Some(reads))

  def saveGeoInfo(
      geoInfo: UserConfigGeoInfo,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig], writes: Writes[UserConfigGeoInfo]) =
    serviceClient.put[UserConfigGeoInfo, UserConfig](
      path = s"$prefixPathUserConfig/geoInfo",
      headers = headers,
      body = geoInfo,
      reads = Some(reads))

  def checkpointPurchaseProduct(
      product: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig]) =
    serviceClient.emptyPut[UserConfig](
      path = s"$prefixPathUserConfig/checkpoint/purchase/$product",
      headers = headers,
      reads = Some(reads))

  def checkpointCustomCollection(headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig]) =
    serviceClient.emptyPut[UserConfig](
      path = s"$prefixPathUserConfig/checkpoint/collection",
      headers = headers,
      reads = Some(reads))

  def checkpointJoinedBy(
      otherConfigId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig]) =
    serviceClient.emptyPut[UserConfig](
      path = s"$prefixPathUserConfig/checkpoint/joined/$otherConfigId",
      headers = headers,
      reads = Some(reads))

  def tester(
      replace: Map[String, String],
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig]) =
    serviceClient.put[Map[String, String], UserConfig](
      path = s"$prefixPathUserConfig/tester",
      headers = headers,
      body = replace,
      reads = Some(reads))

}
