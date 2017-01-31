/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.services.calls.impl

import cards.nine.commons.contentresolver.ContentResolverWrapperImpl
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.models.Call
import cards.nine.services.calls.{CallsServicesException, CallsServicesPermissionException}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait CallsServicesSpecification extends Specification with Mockito with CallsServicesImplData {

  trait CallsServicesScope extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val callServices = new CallsServicesImpl(contentResolverWrapper)

  }
}

class CallsServicesImplSpec extends CallsServicesSpecification {

  "CallsService component" should {

    "getCalls" should {

      "returns all the contacts from the content resolver" in
        new CallsServicesScope {

          contentResolverWrapper.fetchAll[Call](any, any, any, any, any)(any) returns calls
          val result = callServices.getLastCalls.value.run
          result shouldEqual Right(calls)
        }

      "return a CallsServicePermissionException when the content resolver throws a SecurityException" in
        new CallsServicesScope {

          val contentResolverException = new SecurityException("Irrelevant message")
          contentResolverWrapper.fetchAll[Call](any, any, any, any, any)(any) throws contentResolverException
          val result = callServices.getLastCalls.value.run
          result must beAnInstanceOf[Left[CallsServicesPermissionException, _]]
        }

      "return a CallsServicesException when the content resolver throws an exception" in
        new CallsServicesScope {

          val contentResolverException = new RuntimeException("Irrelevant message")
          contentResolverWrapper.fetchAll[Call](any, any, any, any, any)(any) throws contentResolverException
          val result = callServices.getLastCalls.value.run
          result must beAnInstanceOf[Left[CallsServicesException, _]]
        }
    }

  }
}
