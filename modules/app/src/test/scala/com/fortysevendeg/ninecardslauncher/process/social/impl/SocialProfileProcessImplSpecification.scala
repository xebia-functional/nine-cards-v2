package com.fortysevendeg.ninecardslauncher.process.social.impl

import android.content.Context
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceSpecification
import com.fortysevendeg.ninecardslauncher.process.social.{SocialProfileClientListener, SocialProfileProcessException}
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.plus.{GooglePlusServices, GooglePlusServicesException}
import com.google.android.gms.common.api.GoogleApiClient
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

import scala.ref.WeakReference

trait SocialProfileProcessImplSpecification
  extends TaskServiceSpecification
  with Mockito
  with SocialProfileProcessImplData {

  trait CloudStorageProcessImplScope
    extends Scope {

    implicit val mockContextSupport = mock[ContextSupport]

    val mockContext = mock[Context]

    val mockContextListener = mock[MyListener]

    val googlePlusServicesException = GooglePlusServicesException("Irrelevant message")

    val googlePlusServices = mock[GooglePlusServices]

    val persistenceServices = mock[PersistenceServices]

    val mockApiClient = mock[GoogleApiClient]

    val socialProfileProcess = new SocialProfileProcessImpl(googlePlusServices, persistenceServices)

  }

  trait MyListener extends Context with SocialProfileClientListener

}

class SocialProfileProcessImplSpec
  extends SocialProfileProcessImplSpecification {

  "createSocialProfileClient" should {

    "return a valid response when the service returns a right response" in
      new CloudStorageProcessImplScope {

        mockContextSupport.getOriginal returns new WeakReference(mockContextListener)
        googlePlusServices.createGooglePlusClient(any, any)(any) returns TaskService(Task(Right(mockApiClient)))
        socialProfileProcess.createSocialProfileClient(clientId, account).run shouldEqual Right(mockApiClient)
      }

    "return a CloudStorageProcessException when the context doesn't implement SocialProfileClientListener" in
      new CloudStorageProcessImplScope  {

        mockContextSupport.getOriginal returns new WeakReference(mockContext)
        googlePlusServices.createGooglePlusClient(any, any)(any) returns TaskService(Task(Either.left(googlePlusServicesException)))
        socialProfileProcess.createSocialProfileClient(clientId, account).mustLeft[SocialProfileProcessException]
      }

    "return a CloudStorageProcessException when the context doesn't exists" in
      new CloudStorageProcessImplScope  {

        mockContextSupport.getOriginal returns new WeakReference(null)
        googlePlusServices.createGooglePlusClient(any, any)(any) returns TaskService(Task(Either.left(googlePlusServicesException)))
        socialProfileProcess.createSocialProfileClient(clientId, account).mustLeft[SocialProfileProcessException]
      }

    "return a CloudStorageProcessException when the service returns an exception" in
      new CloudStorageProcessImplScope  {

        mockContextSupport.getOriginal returns new WeakReference(mockContextListener)
        googlePlusServices.createGooglePlusClient(any, any)(any) returns TaskService(Task(Either.left(googlePlusServicesException)))
        socialProfileProcess.createSocialProfileClient(clientId, account).mustLeft[SocialProfileProcessException]
      }

  }

  "updateUserProfile" should {

    "return an Answer and update profile in Persistence Service with the right params" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.right(googlePlusProfile)))
        mockContextSupport.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))
        persistenceServices.updateUser(any) returns TaskService(Task(Either.right(1)))

        val result = socialProfileProcess.updateUserProfile(mockApiClient).run
        result should beAnInstanceOf[Right[_,Unit]]

        there was one(persistenceServices).findUserById(findUserByIdRequest)

        there was one(persistenceServices).updateUser(updateUserRequest)

      }

    "return an Errata if the Persistence Service doesn't return an User" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.right(googlePlusProfile)))
        mockContextSupport.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(None)))

        val result = socialProfileProcess.updateUserProfile(mockApiClient).run
        result must beAnInstanceOf[Left[SocialProfileProcessException, _]]

        there was one(persistenceServices).findUserById(findUserByIdRequest)

      }

    "return an Errata with the SocialProfileProcessException if the Google Plus Services returns an Errata" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.left(GooglePlusServicesException("Irrelevant message"))))
        socialProfileProcess.updateUserProfile(mockApiClient).mustLeft[SocialProfileProcessException]
      }

    "return an Errata with the SocialProfileProcessException if there is not an active user" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.right(googlePlusProfile)))
        mockContextSupport.getActiveUserId returns None

        socialProfileProcess.updateUserProfile(mockApiClient).mustLeft[SocialProfileProcessException]
      }

    "return an Errata with the SocialProfileProcessException if the Persistence Service return an Errata in the findUserById method" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.right(googlePlusProfile)))
        mockContextSupport.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.left(PersistenceServiceException("Irrelevant message"))))

        socialProfileProcess.updateUserProfile(mockApiClient).mustLeft[SocialProfileProcessException]

        there was one(persistenceServices).findUserById(findUserByIdRequest)
      }

    "return an Errata with the SocialProfileProcessException if the Persistence Service return an Errata in the updateUser method" in
      new CloudStorageProcessImplScope {

        googlePlusServices.loadUserProfile(any) returns TaskService(Task(Either.right(googlePlusProfile)))
        mockContextSupport.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))
        persistenceServices.updateUser(any) returns TaskService(Task(Either.left(PersistenceServiceException("Irrelevant message"))))

        socialProfileProcess.updateUserProfile(mockApiClient).mustLeft[SocialProfileProcessException]

        there was one(persistenceServices).findUserById(findUserByIdRequest)

        there was one(persistenceServices).updateUser(updateUserRequest)
      }

  }

}