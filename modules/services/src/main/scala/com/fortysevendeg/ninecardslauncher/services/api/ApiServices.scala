package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.api.models.{UserConfigGeoInfo, UserConfigDevice, GoogleDevice}

import scalaz.\/
import scalaz.concurrent.Task

trait ApiServices {

  def login(
    email: String,
    device: GoogleDevice
    ): Task[NineCardsException \/ LoginResponse]

  def linkGoogleAccount(
    email: String,
    devices: Seq[GoogleDevice]
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ LoginResponse]

  def createInstallation(
    id: Option[String],
    deviceType: Option[String],
    deviceToken: Option[String],
    userId: Option[String]
    ): Task[NineCardsException \/ InstallationResponse]

  def updateInstallation(
    id: Option[String],
    deviceType: Option[String],
    deviceToken: Option[String],
    userId: Option[String]
    ): Task[NineCardsException \/ UpdateInstallationResponse]

  def googlePlayPackage(
    packageName: String
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ GooglePlayPackageResponse]

  def googlePlayPackages(
    packageNames: Seq[String]
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ GooglePlayPackagesResponse]

  def googlePlaySimplePackages(
    items: Seq[String]
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ GooglePlaySimplePackagesResponse]

  def getUserConfig(
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ GetUserConfigResponse]

  def saveDevice(
    userConfigDevice: UserConfigDevice
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ SaveDeviceResponse]

  def saveGeoInfo(
    userConfigGeoInfo: UserConfigGeoInfo
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ SaveGeoInfoResponse]

  def checkpointPurchaseProduct(
    productId: String
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ CheckpointPurchaseProductResponse]

  def checkpointCustomCollection(
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ CheckpointCustomCollectionResponse]

  def checkpointJoinedBy(
    otherConfigId: String
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ CheckpointJoinedByResponse]

  def tester(
    replace: Map[String, String]
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ TesterResponse]
}
