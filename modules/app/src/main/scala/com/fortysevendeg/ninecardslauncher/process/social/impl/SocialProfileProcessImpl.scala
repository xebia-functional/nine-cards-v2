package com.fortysevendeg.ninecardslauncher.process.social.impl

import android.os.Bundle
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.{ActivityContextSupport, ContextSupport}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.social._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{User => ServicesUser}
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServiceException, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.plus.GooglePlusServices
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile
import monix.eval.Task
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.plus.Plus

class SocialProfileProcessImpl(
  googlePlusServices: GooglePlusServices,
  persistenceServices: PersistenceServices)
  extends SocialProfileProcess
  with ImplicitsSocialProfileProcessExceptions
  with Conversions {

  val me = "me"

  private[this] val noActiveUserErrorMessage = "No active user"

  override def createSocialProfileClient(clientId: String, account: String)(implicit contextSupport: ContextSupport) =
    TaskService {
      contextSupport.getOriginal.get match {
        case Some(activity: SocialProfileClientListener) =>
          CatchAll[SocialProfileProcessException] {
            val gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestScopes(Plus.SCOPE_PLUS_PROFILE)
              .requestIdToken(clientId)
              .setAccountName(account)
              .build()

            new GoogleApiClient.Builder(contextSupport.context)
              .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
              .addApi(Plus.API)
              .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks {
                override def onConnectionSuspended(cause: Int): Unit =
                  activity.onPlusConnectionSuspended(cause)

                override def onConnected(bundle: Bundle): Unit =
                  activity.onPlusConnected()
              })
              .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener {
                override def onConnectionFailed(connectionResult: ConnectionResult): Unit =
                  activity.onPlusConnectionFailed(connectionResult)
              })
              .build()
          }
        case Some(_) =>
          Task(Left(SocialProfileProcessException("The implicit activity is not a SocialProfileClientListener")))
        case None =>
          Task(Left(SocialProfileProcessException("The implicit activity is null")))
      }
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
    } yield googlePlusProfile.name).resolve[SocialProfileProcessException]
  }

}
