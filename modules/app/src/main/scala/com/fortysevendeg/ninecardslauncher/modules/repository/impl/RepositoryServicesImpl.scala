package com.fortysevendeg.ninecardslauncher.modules.repository.impl

import java.io.File

import android.content.{ContentResolver, SharedPreferences}
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.models.{Installation, User}
import com.fortysevendeg.ninecardslauncher.modules.repository.{Conversions, _}
import com.fortysevendeg.ninecardslauncher.modules.user.{GoogleTokenNotFoundException, GoogleUserNotFoundException, AndroidIdNotFoundException}
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.repositories.{CacheCategoryRepositoryClient, CardRepositoryClient, CollectionRepositoryClient, GeoInfoRepositoryClient}
import com.fortysevendeg.ninecardslauncher.ui.commons.GoogleServicesConstants._
import com.fortysevendeg.ninecardslauncher.utils.FileUtils

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

class RepositoryServicesImpl(
  cacheCategoryRepositoryClient: CacheCategoryRepositoryClient,
  collectionRepositoryClient: CollectionRepositoryClient,
  cardRepositoryClient: CardRepositoryClient,
  geoInfoRepositoryClient: GeoInfoRepositoryClient,
  cr: ContentResolver,
  filesDir: File,
  preferences: SharedPreferences)
  extends RepositoryServices
  with Conversions
  with FileUtils {

  implicit val contentResolver: ContentResolver = cr

  val FilenameUser = "__user_entity__"

  val FilenameInstallation = "__installation_entity__"

  val GoogleKeyUser = "__google_user__"

  val GoogleKeyToken = "__google_token__"

  override def getCollections(request: GetCollectionsRequest)(implicit ec: ExecutionContext): Future[GetCollectionsResponse] = {
      val promise = Promise[GetCollectionsResponse]()
      collectionRepositoryClient.getSortedCollections(GetSortedCollectionsRequest()) map {
        response =>
          val futures = toCollectionSeq(response.collections) map {
            collection =>
              cardRepositoryClient.getCardByCollection(GetAllCardsByCollectionRequest(collection.id)) map {
                cardResponse =>
                  collection.copy(cards = cardResponse.result map toCard)
              }
          }
          Future.sequence(futures) map {
            collections =>
              promise.success(GetCollectionsResponse(collections))
          } recover {
            case _ => promise.success(GetCollectionsResponse(Seq.empty))
          }
      } recover {
        case _ => promise.success(GetCollectionsResponse(Seq.empty))
      }
      promise.future
    }

  override def insertCacheCategory(request: InsertCacheCategoryRequest)(implicit ec: ExecutionContext): Future[InsertCacheCategoryResponse] =
      cacheCategoryRepositoryClient.addCacheCategory(toAddCacheCategoryRequest(request)) map {
        response =>
          InsertCacheCategoryResponse(response.cacheCategory map toCacheCategory)
      }

  override def getCacheCategory(request: GetCacheCategoryRequest)(implicit ec: ExecutionContext): Future[GetCacheCategoryResponse] =
      cacheCategoryRepositoryClient.getAllCacheCategories(GetAllCacheCategoriesRequest()) map {
        response =>
          GetCacheCategoryResponse(toCacheCategorySeq(response.cacheCategories))
      }

  override def insertGeoInfo(request: InsertGeoInfoRequest)(implicit ec: ExecutionContext): Future[InsertGeoInfoResponse] =
      geoInfoRepositoryClient.addGeoInfo(toAddGeoInfoRequest(request)) map {
        response =>
          InsertGeoInfoResponse(response.geoInfo)
      }

  override def insertCollection(request: InsertCollectionRequest)(implicit ec: ExecutionContext): Future[InsertCollectionResponse] = {
      val promise = Promise[InsertCollectionResponse]()
      collectionRepositoryClient.addCollection(toAddCollectionRequest(request)) map {
        response =>
          response.collection map {
            collection => {
              val futures = request.cards map {
                card =>
                  cardRepositoryClient.addCard(toAddCardRequest(collection.id, card))
              }
              Future.sequence(futures) map (p => promise.success(InsertCollectionResponse(true))) recover {
                case _ => promise.success(InsertCollectionResponse(false))
              }
            }
          } getOrElse promise.success(InsertCollectionResponse(true))
      } recover {
        case _ => promise.success(InsertCollectionResponse(false))
      }

      promise.future
    }

  override def getUser()(implicit ec: ExecutionContext): Future[User] =
    tryToFuture(loadFile[User](getFileUser))

  override def saveUser(user: User)(implicit ec: ExecutionContext): Future[Unit] =
    tryToFuture(writeFile[User](getFileUser, user))

  override def resetUser()(implicit ec: ExecutionContext): Future[Boolean] =
    Future {
      val fileUser = getFileUser
      fileUser.exists() && fileUser.delete()
    }

  override def getAndroidId()(implicit ec: ExecutionContext): Future[String] =
    tryToFuture{
      Try {
        val cursor = Option(contentResolver.query(Uri.parse(ContentGServices), null, null, Array(AndroidId), null))
        val result = cursor filter (c => c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(1).toHexString.toUpperCase)
        result getOrElse (throw AndroidIdNotFoundException())
      }
    }

  override def getInstallation()(implicit ec: ExecutionContext): Future[Installation] =
    tryToFuture(loadFile[Installation](getFileInstallation))

  override def saveInstallation(installation: Installation)(implicit ec: ExecutionContext): Future[Boolean] =
    Future {
      if (getFileInstallation.exists()) false
      else writeFile[Installation](getFileInstallation, installation) match {
        case Success(_) => true
        case Failure(e) => throw e
      }
    }

  private def getFileInstallation = new File(filesDir, FilenameInstallation)

  private def getFileUser = new File(filesDir, FilenameUser)

  override def getGoogleUser()(implicit ec: ExecutionContext): Future[String] =
    Future {
      Option(preferences.getString(GoogleKeyUser, javaNull)) getOrElse (throw GoogleUserNotFoundException())
    }

  override def saveGoogleUser(user: String)(implicit ec: ExecutionContext): Future[Boolean] =
    Future {
      preferences.edit.putString(GoogleKeyUser, user).apply()
      true
    }

  override def getGoogleToken()(implicit ec: ExecutionContext): Future[String] =
    Future {
      Option(preferences.getString(GoogleKeyToken, javaNull)) getOrElse (throw GoogleTokenNotFoundException())
    }

  override def saveGoogleToken(token: String)(implicit ec: ExecutionContext): Future[Boolean] =
    Future {
      preferences.edit.putString(GoogleKeyToken, token).apply()
      true
    }

  override def resetGoogleToken()(implicit ec: ExecutionContext): Future[Boolean] =
    Future {
      preferences.edit().remove(GoogleKeyToken).apply()
      true
    }
}
