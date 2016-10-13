package cards.nine.process.social.impl

import android.os.Bundle
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{UserProfile, User}
import cards.nine.process.social._
import cards.nine.services.persistence.{PersistenceServiceException, PersistenceServices}
import cards.nine.services.plus.models.GooglePlusProfile
import cards.nine.services.plus.{GooglePlusServices, GooglePlusServicesException}
import cats.syntax.either._
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import monix.eval.Task

class SocialProfileProcessImpl(
  googlePlusServices: GooglePlusServices,
  persistenceServices: PersistenceServices)
  extends SocialProfileProcess {

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

    def updateUser(maybeUser: Option[User], googlePlusProfile: GooglePlusProfile) ={

      def toUser(user: User, googlePlusProfile: GooglePlusProfile) =
        user.copy(
          userProfile = UserProfile(
            name = googlePlusProfile.name,
            avatar = googlePlusProfile.avatarUrl,
            cover = googlePlusProfile.coverUrl))

      maybeUser match {
        case Some(user) => persistenceServices.updateUser(toUser(user, googlePlusProfile))
        case None => TaskService(Task(Either.left(PersistenceServiceException(noActiveUserErrorMessage))))
      }

    }

    def findAndUpdateUserProfile(googlePlusProfile: GooglePlusProfile)(implicit context: ContextSupport) =
      context.getActiveUserId map { userId =>
        (for {
          maybeUser <- persistenceServices.findUserById(userId)
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
