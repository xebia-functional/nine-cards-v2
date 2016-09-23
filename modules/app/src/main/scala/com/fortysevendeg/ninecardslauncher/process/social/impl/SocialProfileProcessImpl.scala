package com.fortysevendeg.ninecardslauncher.process.social.impl

import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.ConnectionSuspendedCause
import com.fortysevendeg.ninecardslauncher.process.social._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{User => ServicesUser}
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServiceException, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile
import com.fortysevendeg.ninecardslauncher.services.plus.{GooglePlusConnectionFailedServicesException, GooglePlusConnectionSuspendedServicesException, GooglePlusServices, GooglePlusServicesException}
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
    googlePlusServices.createGooglePlusClient(clientId, account).leftMap {
      case gPlusException: GooglePlusServicesException =>
        SocialProfileProcessException(gPlusException.getMessage, Option(gPlusException), gPlusException.recoverable)
      case gPlusException: GooglePlusConnectionSuspendedServicesException =>
        SocialProfileConnectionSuspendedServicesException(gPlusException.getMessage, ConnectionSuspendedCause(gPlusException.googleCauseCode), Option(gPlusException))
      case gPlusException: GooglePlusConnectionFailedServicesException =>
        SocialProfileConnectionFailedServicesException(gPlusException.getMessage, gPlusException.connectionResult, Option(gPlusException.copy(connectionResult = None)))
      case t => SocialProfileProcessException(t.getMessage, Option(t))
    }

  override def updateUserProfile(client: GoogleApiClient)(implicit context: ContextSupport) = {

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
        } yield ()).resolve[SocialProfileProcessException]
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

}
