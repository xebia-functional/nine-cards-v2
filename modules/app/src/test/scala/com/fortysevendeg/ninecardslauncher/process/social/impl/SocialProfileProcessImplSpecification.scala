package com.fortysevendeg.ninecardslauncher.process.social.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.social.SocialProfileProcessException
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.plus.{GooglePlusServices, GooglePlusServicesException}
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import cats.syntax.either._
import com.google.android.gms.common.api.GoogleApiClient

trait SocialProfileProcessImplSpecification
  extends Specification
  with Mockito
  with SocialProfileProcessImplData {

  trait CloudStorageProcessImplScope
    extends Scope {

    implicit val context = mock[ContextSupport]

    val googlePlusServices = mock[GooglePlusServices]

    val persistenceServices = mock[PersistenceServices]

    val mockApiClient = mock[GoogleApiClient]

    val socialProfileProcess = new SocialProfileProcessImpl(googlePlusServices, persistenceServices)

  }

}

class SocialProfileProcessImplSpec
  extends SocialProfileProcessImplSpecification {

  "updateUserProfile" should {

    "return an Answer and update profile in Persistence Service with the right params" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.right(googlePlusProfile)))
        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))
        persistenceServices.updateUser(any) returns TaskService(Task(Either.right(1)))

        val result = socialProfileProcess.updateUserProfile(mockApiClient).value.run
        result should beAnInstanceOf[Right[_,Unit]]

        there was one(persistenceServices).findUserById(findUserByIdRequest)

        there was one(persistenceServices).updateUser(updateUserRequest)

      }

    "return an Errata if the Persistence Service doesn't return an User" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.right(googlePlusProfile)))
        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(None)))

        val result = socialProfileProcess.updateUserProfile(mockApiClient).value.run
        result must beAnInstanceOf[Left[SocialProfileProcessException, _]]

        there was one(persistenceServices).findUserById(findUserByIdRequest)

      }

    "return an Errata with the SocialProfileProcessException if the Google Plus Services returns an Errata" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.left(GooglePlusServicesException("Irrelevant message"))))
        val result = socialProfileProcess.updateUserProfile(mockApiClient).value.run
        result must beAnInstanceOf[Left[SocialProfileProcessException, _]]
      }

    "return an Errata with the SocialProfileProcessException if there is not an active user" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.right(googlePlusProfile)))
        context.getActiveUserId returns None

        val result = socialProfileProcess.updateUserProfile(mockApiClient).value.run
        result must beAnInstanceOf[Left[SocialProfileProcessException, _]]
      }

    "return an Errata with the SocialProfileProcessException if the Persistence Service return an Errata in the findUserById method" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.right(googlePlusProfile)))
        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.left(PersistenceServiceException("Irrelevant message"))))

        val result = socialProfileProcess.updateUserProfile(mockApiClient).value.run
        result must beAnInstanceOf[Left[SocialProfileProcessException, _]]

        there was one(persistenceServices).findUserById(findUserByIdRequest)
      }

    "return an Errata with the SocialProfileProcessException if the Persistence Service return an Errata in the updateUser method" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.right(googlePlusProfile)))
        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))
        persistenceServices.updateUser(any) returns TaskService(Task(Either.left(PersistenceServiceException("Irrelevant message"))))

        val result = socialProfileProcess.updateUserProfile(mockApiClient).value.run
        result must beAnInstanceOf[Left[SocialProfileProcessException, _]]

        there was one(persistenceServices).findUserById(findUserByIdRequest)

        there was one(persistenceServices).updateUser(updateUserRequest)
      }

  }

}