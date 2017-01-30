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
import cards.nine.commons.test.data.trackevent.MomentsTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait MomentsTrackEventProcessSpecification
    extends TaskServiceSpecification
    with MomentsTrackEventTestData
    with Mockito {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackServicesScope extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class MomentsTrackEventProcessImplSpec extends MomentsTrackEventProcessSpecification {

  "openApplicationByMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openApplicationByMoment(momentName).mustRightUnit

      there was one(mockTrackServices).trackEvent(openApplicationByMomentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openApplicationByMoment(momentName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openApplicationByMomentEvent)
    }

  }

  "editMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.editMoment(momentName).mustRightUnit

      there was one(mockTrackServices).trackEvent(editMomentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.editMoment(momentName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(editMomentEvent)
    }

  }

  "changeMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.changeMoment(momentName).mustRightUnit

      there was one(mockTrackServices).trackEvent(changeMomentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.changeMoment(momentName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(changeMomentEvent)
    }

  }

  "addMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addMoment(momentName).mustRightUnit

      there was one(mockTrackServices).trackEvent(addMomentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addMoment(momentName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addMomentEvent)
    }

  }

  "addWidget" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addWidget(widgetName).mustRightUnit

      there was one(mockTrackServices).trackEvent(addWidgetEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addWidget(widgetName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addWidgetEvent)
    }

  }

  "unpinMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.unpinMoment().mustRightUnit

      there was one(mockTrackServices).trackEvent(unpinMomentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.unpinMoment().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(unpinMomentEvent)
    }

  }

  "goToWeather" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToWeather().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToWeatherEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToWeather().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToWeatherEvent)
    }

  }

  "quickAccessToCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.quickAccessToCollection().mustRightUnit

      there was one(mockTrackServices).trackEvent(quickAccessToCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.quickAccessToCollection().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(quickAccessToCollectionEvent)
    }

  }

  "setHours" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.setHours().mustRightUnit

      there was one(mockTrackServices).trackEvent(setHoursEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.setHours().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(setHoursEvent)
    }

  }

  "setWifi" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.setWifi().mustRightUnit

      there was one(mockTrackServices).trackEvent(setWifiEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.setWifi().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(setWifiEvent)
    }

  }

  "setBluetooth" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.setBluetooth().mustRightUnit

      there was one(mockTrackServices).trackEvent(setBluetoothEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.setBluetooth().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(setBluetoothEvent)
    }

  }

  "deleteMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.deleteMoment().mustRightUnit

      there was one(mockTrackServices).trackEvent(deleteMomentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.deleteMoment().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(deleteMomentEvent)
    }

  }
}
