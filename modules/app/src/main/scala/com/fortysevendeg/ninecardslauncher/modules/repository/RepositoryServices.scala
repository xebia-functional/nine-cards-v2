package com.fortysevendeg.ninecardslauncher.modules.repository

import com.fortysevendeg.ninecardslauncher.models.{Installation, User}

import scala.concurrent.{Future, ExecutionContext}

trait RepositoryServices {

  def getCollections(request: GetCollectionsRequest)(implicit ec: ExecutionContext): Future[GetCollectionsResponse]

  def insertCacheCategory(request: InsertCacheCategoryRequest)(implicit ec: ExecutionContext): Future[InsertCacheCategoryResponse]

  def getCacheCategory(request: GetCacheCategoryRequest)(implicit ec: ExecutionContext): Future[GetCacheCategoryResponse]

  def insertGeoInfo(request: InsertGeoInfoRequest)(implicit ec: ExecutionContext): Future[InsertGeoInfoResponse]

  def insertCollection(request: InsertCollectionRequest)(implicit ec: ExecutionContext): Future[InsertCollectionResponse]

  def getUser()(implicit ec: ExecutionContext): Future[User]

  def saveUser(user: User)(implicit ec: ExecutionContext): Future[Unit]

  def resetUser()(implicit ec: ExecutionContext): Future[Boolean]

  def getAndroidId()(implicit ec: ExecutionContext): Future[String]

  def getInstallation()(implicit ec: ExecutionContext): Future[Installation]

  def saveInstallation(installation: Installation)(implicit ec: ExecutionContext): Future[Boolean]

  def getGoogleUser()(implicit ec: ExecutionContext): Future[String]

  def saveGoogleUser(user: String)(implicit ec: ExecutionContext): Future[Boolean]

  def getGoogleToken()(implicit ec: ExecutionContext): Future[String]

  def saveGoogleToken(token: String)(implicit ec: ExecutionContext): Future[Boolean]

  def resetGoogleToken()(implicit ec: ExecutionContext): Future[Boolean]
}
