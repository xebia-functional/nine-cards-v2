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

package cards.nine.process.trackevent.impl

import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.data.trackevent.HomeTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait HomeTrackEventProcessSpecification
    extends TaskServiceSpecification
    with HomeTrackEventTestData
    with Mockito {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackServicesScope extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class HomeTrackEventProcessImplSpec extends HomeTrackEventProcessSpecification {

  "openCollectionTitle" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openCollectionTitle(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(openCollectionTitleEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openCollectionTitle(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openCollectionTitleEvent)
    }

  }

  "openCollectionOrder" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openCollectionOrder(position).mustRightUnit

      there was one(mockTrackServices).trackEvent(openCollectionOrderEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openCollectionOrder(position).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openCollectionOrderEvent)
    }

  }

  "deleteCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.deleteCollection(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(deleteCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.deleteCollection(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(deleteCollectionEvent)
    }

  }

  "reorderCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.reorderCollection().mustRightUnit

      there was one(mockTrackServices).trackEvent(reorderCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.reorderCollection().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(reorderCollectionEvent)
    }

  }

  "usingSearchByKeyboard" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.usingSearchByKeyboard().mustRightUnit

      there was one(mockTrackServices).trackEvent(usingSearchByKeyboardEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.usingSearchByKeyboard().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(usingSearchByKeyboardEvent)
    }

  }

  "usingSearchByVoice" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.usingSearchByVoice().mustRightUnit

      there was one(mockTrackServices).trackEvent(usingSearchByVoiceEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.usingSearchByVoice().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(usingSearchByVoiceEvent)
    }

  }

  "createNewCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.createNewCollection().mustRightUnit

      there was one(mockTrackServices).trackEvent(createNewCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.createNewCollection().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(createNewCollectionEvent)
    }

  }

  "editCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.editCollection(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(editCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.editCollection(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(editCollectionEvent)
    }

  }

  "openMyCollections" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openMyCollections().mustRightUnit

      there was one(mockTrackServices).trackEvent(openMyCollectionsEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openMyCollections().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openMyCollectionsEvent)
    }

  }

  "openPublicCollections" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openPublicCollections().mustRightUnit

      there was one(mockTrackServices).trackEvent(openPublicCollectionsEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openPublicCollections().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openPublicCollectionsEvent)
    }

  }

  "createNewCollectionFromMyCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.createNewCollectionFromMyCollection(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(createNewCollectionFromMyCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.createNewCollectionFromMyCollection(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(createNewCollectionFromMyCollectionEvent)
    }

  }

  "createNewCollectionFromPublicCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.createNewCollectionFromPublicCollection(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(createNewCollectionFromPublicCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.createNewCollectionFromPublicCollection(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(createNewCollectionFromPublicCollectionEvent)
    }

  }

  "openDockAppTitle" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openDockAppTitle(packageName).mustRightUnit

      there was one(mockTrackServices).trackEvent(openDockAppTitleEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openDockAppTitle(packageName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openDockAppTitleEvent)
    }

  }

  "openDockAppOrder" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openDockAppOrder(position).mustRightUnit

      there was one(mockTrackServices).trackEvent(openDockAppOrderEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openDockAppOrder(position).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openDockAppOrderEvent)
    }

  }

  "goToAppDrawer" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToAppDrawer().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToAppDrawerEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToAppDrawer().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToAppDrawerEvent)
    }

  }

  "appLinkReceived" should {

    "track the app with the right parameters including a supported app link" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.appLinkReceived(supported).mustRightUnit

      appLinkReceivedEvent
    }

    "track the app with the right parameters including a not supported app link" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.appLinkReceived(notSupported).mustRightUnit

      there was one(mockTrackServices).trackEvent(
        appLinkReceivedEvent.copy(label = Option(notSupportedStr)))
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.appLinkReceived(supported).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(appLinkReceivedEvent)
    }

  }

  "sharedContentReceived" should {

    "track the app with the right parameters including a supported shared content" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.sharedContentReceived(supported).mustRightUnit

      there was one(mockTrackServices).trackEvent(sharedContentReceivedEvent)
    }

    "track the app with the right parameters including a not supported shared content" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.sharedContentReceived(notSupported).mustRightUnit

      there was one(mockTrackServices).trackEvent(
        sharedContentReceivedEvent.copy(label = Option(notSupportedStr)))
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.sharedContentReceived(supported).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(sharedContentReceivedEvent)
    }

  }
}
