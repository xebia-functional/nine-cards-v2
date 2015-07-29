package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.services.api.models.{Installation, User}
import com.fortysevendeg.ninecardslauncher.services.persistence.models._

import scalaz.\/
import scalaz.concurrent.Task

trait PersistenceServices {

  def addCacheCategory(request: AddCacheCategoryRequest): Task[NineCardsException \/ CacheCategory]

  def deleteCacheCategory(request: DeleteCacheCategoryRequest): Task[NineCardsException \/ Int]

  def deleteCacheCategoryByPackage(request: DeleteCacheCategoryByPackageRequest): Task[NineCardsException \/ Int]

  def fetchCacheCategoryByPackage(request: FetchCacheCategoryByPackageRequest): Task[NineCardsException \/ Option[CacheCategory]]

  def fetchCacheCategories: ServiceDef2[Seq[CacheCategory], RepositoryException]

  def findCacheCategoryById(request: FindCacheCategoryByIdRequest): Task[NineCardsException \/ Option[CacheCategory]]

  def updateCacheCategory(request: UpdateCacheCategoryRequest): Task[NineCardsException \/ Int]

  def addCard(request: AddCardRequest): Task[NineCardsException \/ Card]

  def deleteCard(request: DeleteCardRequest): Task[NineCardsException \/ Int]

  def fetchCardsByCollection(request: FetchCardsByCollectionRequest): Task[NineCardsException \/ Seq[Card]]

  def findCardById(request: FindCardByIdRequest): Task[NineCardsException \/ Option[Card]]

  def updateCard(request: UpdateCardRequest): Task[NineCardsException \/ Int]

  def addCollection(request: AddCollectionRequest): Task[NineCardsException \/ Collection]

  def deleteCollection(request: DeleteCollectionRequest): Task[NineCardsException \/ Int]

  def fetchCollections: Task[NineCardsException \/ Seq[Collection]]

  def fetchCollectionBySharedCollection(request: FetchCollectionBySharedCollectionRequest): Task[NineCardsException \/ Option[Collection]]

  def fetchCollectionByPosition(request: FetchCollectionByPositionRequest): Task[NineCardsException \/ Option[Collection]]

  def findCollectionById(request: FindCollectionByIdRequest): Task[NineCardsException \/ Option[Collection]]

  def updateCollection(request: UpdateCollectionRequest): Task[NineCardsException \/ Int]

  def addGeoInfo(request: AddGeoInfoRequest): Task[NineCardsException \/ GeoInfo]

  def deleteGeoInfo(request: DeleteGeoInfoRequest): Task[NineCardsException \/ Int]

  def fetchGeoInfoByConstrain(request: FetchGeoInfoByConstrainRequest): Task[NineCardsException \/ Option[GeoInfo]]

  def fetchGeoInfoItems: Task[NineCardsException \/ Seq[GeoInfo]]

  def findGeoInfoById(request: FindGeoInfoByIdRequest): Task[NineCardsException \/ Option[GeoInfo]]

  def updateGeoInfo(request: UpdateGeoInfoRequest): Task[NineCardsException \/ Int]

  def getUser(implicit context: ContextSupport): Task[NineCardsException \/ User]

  def saveUser(user: User)(implicit context: ContextSupport): Task[NineCardsException \/ Unit]

  def resetUser(implicit context: ContextSupport): Task[NineCardsException \/ Boolean]

  def getAndroidId(implicit context: ContextSupport): Task[NineCardsException \/ String]

  def getInstallation(implicit context: ContextSupport): Task[NineCardsException \/ Installation]

  def existsInstallation(implicit context: ContextSupport): Task[NineCardsException \/ Boolean]

  def saveInstallation(installation: Installation)(implicit context: ContextSupport): Task[NineCardsException \/ Unit]

}
