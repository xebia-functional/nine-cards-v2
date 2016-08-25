package com.fortysevendeg.ninecardslauncher.process.accounts.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.process.accounts.SocialProfileProcessException
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.plus.{GooglePlusServices, GooglePlusServicesException}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task

trait UserAccountsProcessImplSpecification
  extends Specification
  with Mockito
  with UserAccountsProcessImplData {

  trait CloudStorageProcessImplScope
    extends Scope {

    implicit val context = mock[ContextSupport]

    val googlePlusServices = mock[GooglePlusServices]

    val persistenceServices = mock[PersistenceServices]

    val socialProfileProcess = new UserAccountsProcessImpl(googlePlusServices, persistenceServices)

  }

}

class UserAccountsProcessImplSpec
  extends UserAccountsProcessImplSpecification {

  "updateUserProfile" should {

    "return an Answer and update profile in Persistence Service with the right params" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile returns CatsService(Task(Xor.right(googlePlusProfile)))

        context.getActiveUserId returns Some(activeUserId)

        persistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))

        persistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))

        val result = socialProfileProcess.updateUserProfile().value.run

        result should beAnInstanceOf[Xor.Right[Unit]]

        there was one(persistenceServices).findUserById(findUserByIdRequest)

        there was one(persistenceServices).updateUser(updateUserRequest)

    }

    "return an Errata if the Persistence Service doesn't return an User" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile returns CatsService(Task(Xor.right(googlePlusProfile)))

        context.getActiveUserId returns Some(activeUserId)

        persistenceServices.findUserById(any) returns CatsService(Task(Xor.right(None)))

        val result = socialProfileProcess.updateUserProfile().value.run

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[SocialProfileProcessException]
        }

        there was one(persistenceServices).findUserById(findUserByIdRequest)

    }

    "return an Errata with the SocialProfileProcessException if the Google Plus Services returns an Errata" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile returns CatsService(Task(Xor.left(GooglePlusServicesException("Irrelevant message"))))

        val result = socialProfileProcess.updateUserProfile().value.run

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[SocialProfileProcessException]
          }

    }

    "return an Errata with the SocialProfileProcessException if there is not an active user" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile returns CatsService(Task(Xor.right(googlePlusProfile)))

        context.getActiveUserId returns None

        val result = socialProfileProcess.updateUserProfile().value.run

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[SocialProfileProcessException]
        }

    }

    "return an Errata with the SocialProfileProcessException if the Persistence Service return an Errata in the findUserById method" in
      new CloudStorageProcessImplScope {

      googlePlusServices.loadUserProfile returns CatsService(Task(Xor.right(googlePlusProfile)))

      context.getActiveUserId returns Some(activeUserId)

      persistenceServices.findUserById(any) returns CatsService(Task(Xor.left(PersistenceServiceException("Irrelevant message"))))

      val result = socialProfileProcess.updateUserProfile().value.run

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[SocialProfileProcessException]
        }

      there was one(persistenceServices).findUserById(findUserByIdRequest)

    }

    "return an Errata with the SocialProfileProcessException if the Persistence Service return an Errata in the updateUser method" in
      new CloudStorageProcessImplScope {

      googlePlusServices.loadUserProfile returns CatsService(Task(Xor.right(googlePlusProfile)))

      context.getActiveUserId returns Some(activeUserId)

      persistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))

      persistenceServices.updateUser(any) returns CatsService(Task(Xor.left(PersistenceServiceException("Irrelevant message"))))

      val result = socialProfileProcess.updateUserProfile().value.run

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[SocialProfileProcessException]
        }

      there was one(persistenceServices).findUserById(findUserByIdRequest)

      there was one(persistenceServices).updateUser(updateUserRequest)

    }

  }

}