package com.fortysevendeg.ninecardslauncher.modules.repository

import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.models.{Installation, User}

trait RepositoryServices {

  type Success = Boolean

  type Failure = Throwable

  def getCollections: Service[GetCollectionsRequest, GetCollectionsResponse]

  def insertCacheCategory: Service[InsertCacheCategoryRequest, InsertCacheCategoryResponse]

  def getCacheCategory: Service[GetCacheCategoryRequest, GetCacheCategoryResponse]

  def insertGeoInfo: Service[InsertGeoInfoRequest, InsertGeoInfoResponse]

  def insertCollection: Service[InsertCollectionRequest, InsertCollectionResponse]

  def getUser: Option[User]

  def saveUser(user: User): Either[Failure, Success]

  def resetUser: Either[Failure, Success]

  def getAndroidId: Option[String]

  def getInstallation: Option[Installation]

  def saveInstallation(installation: Installation): Either[Failure, Success]

  def getGoogleUser: Option[String]

  def saveGoogleUser(user: String): Either[Failure, Success]

  def getGoogleToken: Option[String]

  def saveGoogleToken(token: String): Either[Failure, Success]

  def resetGoogleToken: Either[Failure, Success]
}
