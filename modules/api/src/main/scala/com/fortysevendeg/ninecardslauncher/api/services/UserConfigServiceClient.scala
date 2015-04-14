package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model.{UserConfigGeoInfo, UserConfigDevice, UserConfig}
import com.fortysevendeg.ninecardslauncher.api.reads.UserConfigImplicits
import com.fortysevendeg.rest.client.ServiceClient
import play.api.libs.json.{Writes, Reads}

import scala.concurrent.ExecutionContext

trait UserConfigServiceClient
    extends ServiceClient
    with UserConfigImplicits {

  val prefixPathUserConfig = "/ninecards/userconfig"

  def getUserConfig(headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig]) =
    get[UserConfig](
      path = prefixPathUserConfig,
      headers = headers,
      reads = Some(reads))

  def saveDevice(
      device: UserConfigDevice,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig], writes: Writes[UserConfigDevice]) =
    put[UserConfigDevice, UserConfig](
      path = s"$prefixPathUserConfig/device",
      headers = headers,
      body = device,
      reads = Some(reads))

  def saveGeoInfo(
      geoInfo: UserConfigGeoInfo,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig], writes: Writes[UserConfigGeoInfo]) =
    put[UserConfigGeoInfo, UserConfig](
      path = s"$prefixPathUserConfig/geoInfo",
      headers = headers,
      body = geoInfo,
      reads = Some(reads))

  def checkpointPurchaseProduct(
      product: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig]) =
    emptyPut[UserConfig](
      path = s"$prefixPathUserConfig/checkpoint/purchase/$product",
      headers = headers,
      reads = Some(reads))

  def checkpointCustomCollection(headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig]) =
    emptyPut[UserConfig](
      path = s"$prefixPathUserConfig/checkpoint/collection",
      headers = headers,
      reads = Some(reads))

  def checkpointJoinedBy(
      otherConfigId: String,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig]) =
    emptyPut[UserConfig](
      path = s"$prefixPathUserConfig/checkpoint/joined/$otherConfigId",
      headers = headers,
      reads = Some(reads))

  def tester(
      replace: Map[String, String],
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[UserConfig]) =
    put[Map[String, String], UserConfig](
      path = s"$prefixPathUserConfig/tester",
      headers = headers,
      body = replace,
      reads = Some(reads))

}
