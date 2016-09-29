package com.fortysevendeg.ninecardslauncher.process.social.impl

import android.os.Bundle
import cats.syntax.either._
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.social._
import cards.nine.services.persistence.models.{User => ServicesUser}
import cards.nine.services.persistence.{FindUserByIdRequest, PersistenceServiceException, PersistenceServices}
import cards.nine.services.plus.models.GooglePlusProfile
import cards.nine.services.plus.{GooglePlusServices, GooglePlusServicesException}
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
    googlePlusServices.createGooglePlusClient(clientId, account).resolveSides(
      mapRight = googleAplClient => {
        contextSupport.getOriginal.get match {
          case Some(listener: SocialProfileClientListener) =>
            googleAplClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks {
              override def onConnectionSuspended(cause: Int): Unit =
                listener.onPlusConnectionSuspended(cause)

              override def onConnected(bundle: Bundle): Unit =
                listener.onPlusConnected()
            })
            googleAplClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener {
              override def onConnectionFailed(connectionResult: ConnectionResult): Unit =
                listener.onPlusConnectionFailed(connectionResult)
            })
            Right(googleAplClient)
          case Some(_) =>
            Left(SocialProfileProcessException("The implicit activity is not a SocialProfileClientListener"))
          case None =>
            Left(SocialProfileProcessException("The implicit activity is null"))
        }
      },
      mapLeft = (e) => Left(SocialProfileProcessException(e.getMessage, Option(e))))

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
