package com.fortysevendeg.ninecardslauncher.process.social.impl

import android.os.Bundle
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.google.GoogleServiceClient
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.social._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{User => ServicesUser}
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServiceException, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile
import com.fortysevendeg.ninecardslauncher.services.plus.{GooglePlusServices, GooglePlusServicesException}
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import monix.eval.Task

class SocialProfileProcessImpl(
  googlePlusServices: GooglePlusServices,
  persistenceServices: PersistenceServices)
  extends SocialProfileProcess
  with Conversions {

  val me = "me"

  private[this] val noActiveUserErrorMessage = "No active user"

  override def createSocialProfileClient(clientId: String, account: String)(implicit contextSupport: ContextSupport) =
    googlePlusServices.createGooglePlusClient(clientId, account)
      .resolveLeft(mapLeft = (e) => Left(SocialProfileProcessException(e.getMessage, Option(e))))

  override def updateUserProfile(client: GoogleServiceClient)(implicit context: ContextSupport) = {

    def updateUser(maybeUser: Option[ServicesUser], googlePlusProfile: GooglePlusProfile) =
      maybeUser match {
        case Some(user) => persistenceServices.updateUser(toUpdateRequest(user, googlePlusProfile))
        case None => TaskService(Task(Either.left(PersistenceServiceException(noActiveUserErrorMessage))))
      }

    def findAndUpdateUserProfile(googlePlusProfile: GooglePlusProfile)(implicit context: ContextSupport) =
      context.getActiveUserId map { id =>
        (for {
          maybeUser <- persistenceServices.findUserById(FindUserByIdRequest(id))
          _ <- updateUser(maybeUser, googlePlusProfile)
        } yield ()).leftMap(onException)
      } getOrElse {
        TaskService(Task(Either.left(SocialProfileProcessException(noActiveUserErrorMessage))))
      }

    (for {
      googlePlusProfile <- googlePlusServices.loadUserProfile(client)
      _ <- findAndUpdateUserProfile(googlePlusProfile)
    } yield googlePlusProfile.name).leftMap {
      case gPlusException: GooglePlusServicesException =>
        SocialProfileProcessException(gPlusException.getMessage, Option(gPlusException), gPlusException.recoverable)
      case t => SocialProfileProcessException(t.getMessage, Option(t))
    }
  }

  private[this] def onException: (Throwable) => NineCardException = {
    case e: GooglePlusServicesException =>
      SocialProfileProcessException(e.getMessage, Option(e), e.recoverable)
    case e => SocialProfileProcessException(e.getMessage, Option(e))
  }

}
