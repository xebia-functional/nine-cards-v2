package com.fortysevendeg.ninecardslauncher.modules.repository.impl

import java.io.{IOException, File}

import android.content.{SharedPreferences, ContentResolver}
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.models.{Installation, User}
import com.fortysevendeg.ninecardslauncher.modules.repository.Conversions
import com.fortysevendeg.ninecardslauncher.modules.repository._
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.ui.commons.GoogleServicesConstants._
import com.fortysevendeg.ninecardslauncher.utils.FileUtils

import scala.concurrent.{Future, Promise, ExecutionContext}
import scala.util.{Failure, Success, Try}

class RepositoryServicesImpl(
  cr: ContentResolver,
  filesDir: File,
  preferences: SharedPreferences)
  extends RepositoryServices
  with Conversions
  with NineCardRepositoryClient
  with FileUtils {

  override implicit val contentResolver: ContentResolver = cr

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val FilenameUser = "__user_entity__"

  val FilenameInstallation = "__installation_entity__"

  val GoogleKeyUser = "__google_user__"

  val GoogleKeyToken = "__google_token__"

  override def getCollections: Service[GetCollectionsRequest, GetCollectionsResponse] =
    request => {
      val promise = Promise[GetCollectionsResponse]()
      getSortedCollections(GetSortedCollectionsRequest()) map {
        response =>
          val futures = toCollectionSeq(response.collections) map {
            collection =>
              getCardByCollection(GetAllCardsByCollectionRequest(collection.id)) map {
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

  override def insertCacheCategory: Service[InsertCacheCategoryRequest, InsertCacheCategoryResponse] =
    request => {
      addCacheCategory(toAddCacheCategoryRequest(request)) map {
        response =>
          InsertCacheCategoryResponse(response.cacheCategory map toCacheCategory)
      }
    }

  override def getCacheCategory: Service[GetCacheCategoryRequest, GetCacheCategoryResponse] =
    request => {
      getAllCacheCategories(GetAllCacheCategoriesRequest()) map {
        response =>
          GetCacheCategoryResponse(toCacheCategorySeq(response.cacheCategories))
      }
    }

  override def insertGeoInfo: Service[InsertGeoInfoRequest, InsertGeoInfoResponse] =
    request =>
      addGeoInfo(toAddGeoInfoRequest(request)) map {
        response =>
          InsertGeoInfoResponse(response.geoInfo)
      }

  override def insertCollection: Service[InsertCollectionRequest, InsertCollectionResponse] =
    request => {
      val promise = Promise[InsertCollectionResponse]()
      addCollection(toAddCollectionRequest(request)) map {
        response =>
          response.collection map {
            collection => {
              val futures = request.cards map {
                card =>
                  addCard(toAddCardRequest(collection.id, card))
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

  override def getUser: Option[User] =
    loadFile[User](getFileUser) match {
      case Success(us) => Some(us)
      case Failure(ex) => None
    }

  override def getAndroidId: Option[String] = Try {
    val cursor = Option(contentResolver.query(Uri.parse(ContentGServices), null, null, Array(AndroidId), null))
    cursor filter (c => c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(1).toHexString.toUpperCase)
  } match {
    case Success(id) => id
    case Failure(ex) => None
  }

  override def getInstallation: Option[Installation] =
    loadFile[Installation](getFileInstallation) match {
      case Success(inst) => Some(inst)
      case Failure(ex) => None
    }

  override def saveInstallation(installation: Installation): Either[Failure, Success] =
    if (getFileInstallation.exists()) Right(false)
    else tryToWriteFile[Installation](getFileInstallation, installation)


  override def saveUser(user: User): Either[Failure, Success] =
    tryToWriteFile[User](getFileUser, user)

  override def resetUser: Either[Failure, Success] = {
    val fileUser = getFileUser
    if (fileUser.exists()) {
      fileUser.delete() match {
        case true => Right(true)
        case false => Left(new IOException(s"Can't delete file ${fileUser.getAbsolutePath}"))
      }
    }
    else Right(false)
  }

  private def getFileInstallation = new File(filesDir, FilenameInstallation)

  private def getFileUser = new File(filesDir, FilenameUser)

  private def tryToWriteFile[T](file: File, content: T): Either[Failure, Success] =
    writeFile[T](file, content) match {
      case Success(_) => Right(true)
      case Failure(e) => Left(e)
    }

  override def getGoogleUser: Option[String] = Option(preferences.getString(GoogleKeyUser, null))

  override def saveGoogleUser(user: String) = {
    preferences.edit.putString(GoogleKeyUser, user).apply()
    Right(true)
  }

  override def getGoogleToken: Option[String] = Option(preferences.getString(GoogleKeyToken, null))

  override def saveGoogleToken(token: String) = {
    preferences.edit.putString(GoogleKeyToken, token).apply()
    Right(true)
  }

  override def resetGoogleToken: Either[Failure, Success] = {
    preferences.edit().remove(GoogleKeyToken).apply()
    Right(true)
  }
}
