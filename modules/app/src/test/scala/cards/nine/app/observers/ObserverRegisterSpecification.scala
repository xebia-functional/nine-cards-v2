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

package cards.nine.app.observers

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.contentresolver.UriCreator
import cards.nine.commons.contexts.ContextSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ObserverRegisterSpecification extends Specification with Mockito {

  trait ObserverRegisterScope extends Scope {

    lazy implicit val contextSupport = mock[ContextSupport]

    lazy val contextResolver = mock[ContentResolver]

    lazy val uriCreator = mock[UriCreator]

    lazy val mockUri = mock[Uri]

    lazy val observerRegister = new ObserverRegister(uriCreator)

  }

}

class ObserverRegisterSpec extends ObserverRegisterSpecification {

  "ObserverRegister" should {

    "call to register observer with the right params" in new ObserverRegisterScope {

      uriCreator.parse(any) returns mockUri
      contextSupport.getContentResolver returns contextResolver

      observerRegister.registerObserverTask().value.run

      there was one(contextResolver)
        .registerContentObserver(any[Uri], any[Boolean], any[ContentObserver])

    }

    "call to unregister observer with the right params" in new ObserverRegisterScope {

      uriCreator.parse(any) returns mockUri
      contextSupport.getContentResolver returns contextResolver

      observerRegister.unregisterObserverTask().value.run

      there was one(contextResolver).unregisterContentObserver(any[ContentObserver])

    }

  }

}
