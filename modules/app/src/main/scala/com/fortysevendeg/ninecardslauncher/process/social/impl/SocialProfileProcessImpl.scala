package com.fortysevendeg.ninecardslauncher.process.social.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.social.{Conversions, ImplicitsSocialProfileProcessExceptions, SocialProfileProcess, SocialProfileProcessException}
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServiceException, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.plus.GooglePlusServices
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{User => ServicesUser}
import rapture.core.Result

import scalaz.concurrent.Task

class SocialProfileProcessImpl(
  googlePlusServices: GooglePlusServices,
  persistenceServices: PersistenceServices)
  extends SocialProfileProcess
  with ImplicitsSocialProfileProcessExceptions
  with Conversions {

  val me = "me"

  private[this] val noActiveUserErrorMessage = "No active user"

  override def updateUserProfile()(implicit context: ContextSupport) = (for {
    googlePlusProfile <- googlePlusServices.loadUserProfile
    _ <- findAndUpdateUserProfile(googlePlusProfile)
  } yield googlePlusProfile.name).resolve[SocialProfileProcessException]

  private[this] def findAndUpdateUserProfile(googlePlusProfile: GooglePlusProfile)(implicit context: ContextSupport) =
    context.getActiveUserId map { id =>
      (for {
        maybeUser <- persistenceServices.findUserById(FindUserByIdRequest(id))
        _ <- updateUser(maybeUser, googlePlusProfile)
      } yield ()).resolve[SocialProfileProcessException]
    } getOrElse {
      Service(Task(Result.errata[Unit, SocialProfileProcessException](SocialProfileProcessException(noActiveUserErrorMessage))))
    }

  private[this] def updateUser(maybeUser: Option[ServicesUser], googlePlusProfile: GooglePlusProfile) =
    maybeUser match {
      case Some(user) => persistenceServices.updateUser(toUpdateRequest(user, googlePlusProfile))
      case None => Service(Task(Result.errata[Unit, PersistenceServiceException](PersistenceServiceException(noActiveUserErrorMessage))))
    }

}
