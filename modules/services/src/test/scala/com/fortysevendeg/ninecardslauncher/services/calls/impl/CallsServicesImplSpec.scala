package com.fortysevendeg.ninecardslauncher.services.calls.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.ContentResolverWrapperImpl
import com.fortysevendeg.ninecardslauncher.services.calls.models.Call
import com.fortysevendeg.ninecardslauncher.services.calls.{CallsServicesException, CallsServicesPermissionException}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait CallsServicesSpecification
  extends Specification
    with Mockito
    with CallsServicesImplData {

  trait CallsServicesScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val callServices = new CallsServicesImpl(contentResolverWrapper)

  }
}

class CallsServicesImplSpec
  extends CallsServicesSpecification {

  "CallsService component" should {

    "getCalls" should {

      "returns all the contacts from the content resolver" in
        new CallsServicesScope {

          contentResolverWrapper.fetchAll[Call](any, any, any, any, any)(any) returns calls
          val result = callServices.getLastCalls.value.run
          result shouldEqual Xor.Right(calls)
        }

      "return a CallsServicePermissionException when the content resolver throws a SecurityException" in
        new CallsServicesScope {

          val contentResolverException = new SecurityException("Irrelevant message")
          contentResolverWrapper.fetchAll[Call](any, any, any, any, any)(any) throws contentResolverException
          val result = callServices.getLastCalls.value.run
          result must beAnInstanceOf[Xor.Left[CallsServicesPermissionException]]
        }

      "return a CallsServicesException when the content resolver throws an exception" in
        new CallsServicesScope {

          val contentResolverException = new RuntimeException("Irrelevant message")
          contentResolverWrapper.fetchAll[Call](any, any, any, any, any)(any) throws contentResolverException
          val result = callServices.getLastCalls.value.run
          result must beAnInstanceOf[Xor.Left[CallsServicesException]]
        }
    }

  }
}