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

import cards.nine.app.ui.commons.{ImplicitsObserverExceptions, ObserverException}
import cards.nine.commons.CatchAll
import cards.nine.commons.contentresolver.UriCreator
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.contentresolver.NotificationUri
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService

class ObserverRegister(uriCreator: UriCreator)(implicit contextSupport: ContextSupport)
    extends ImplicitsObserverExceptions {

  import NotificationUri._

  val baseUri = uriCreator.parse(baseUriNotificationString)

  val observer = new NineCardsObserver

  def registerObserverTask(): TaskService[Unit] = TaskService {
    CatchAll[ObserverException] {
      contextSupport.getContentResolver.registerContentObserver(baseUri, true, observer)
    }
  }

  def unregisterObserverTask(): TaskService[Unit] = TaskService {
    CatchAll[ObserverException] {
      contextSupport.getContentResolver.unregisterContentObserver(observer)
    }
  }

}
