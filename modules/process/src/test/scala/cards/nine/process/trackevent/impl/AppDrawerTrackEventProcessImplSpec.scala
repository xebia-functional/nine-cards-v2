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
import cards.nine.commons.test.data.trackevent.AppDrawerTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait AppDrawerTrackEventProcessSpecification
    extends TaskServiceSpecification
    with AppDrawerTrackEventTestData
    with Mockito {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackServicesScope extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class AppDrawerTrackEventProcessImplSpec extends AppDrawerTrackEventProcessSpecification {

  "usingFastScroller" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.usingFastScroller().mustRightUnit

      there was one(mockTrackServices).trackEvent(usingFastScrollerEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.usingFastScroller().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(usingFastScrollerEvent)
    }

  }

  "goToContacts" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToContacts().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToContactsEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToContacts().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToContactsEvent)
    }

  }

  "goToApps" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToApps().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToAppsEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToApps().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToAppsEvent)
    }

  }

  "addAppToCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addAppToCollection(packageName).mustRightUnit

      there was one(mockTrackServices).trackEvent(addAppToCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addAppToCollection(packageName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addAppToCollectionEvent)
    }

  }

  "addContactToCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addContactToCollection().mustRightUnit

      there was one(mockTrackServices).trackEvent(addContactToCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addContactToCollection().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addContactToCollectionEvent)
    }

  }

  "goToGooglePlayButton" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToGooglePlayButton().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToGooglePlayButtonEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToGooglePlayButton().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToGooglePlayButtonEvent)
    }

  }

  "goToGoogleCallButton" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToGoogleCallButton().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToGoogleCallButtonEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToGoogleCallButton().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToGoogleCallButtonEvent)
    }

  }

  "goToFiltersByButton" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToFiltersByButton(filterName).mustRightUnit

      there was one(mockTrackServices).trackEvent(goToFiltersByButtonEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToFiltersByButton(filterName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToFiltersByButtonEvent)
    }

  }

}
