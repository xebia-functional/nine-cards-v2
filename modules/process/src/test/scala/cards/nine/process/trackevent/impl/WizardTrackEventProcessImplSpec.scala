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
import cards.nine.commons.test.data.trackevent.WizardTrackEventTestData
import cards.nine.models.types._
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait WizardTrackEventProcessSpecification
    extends TaskServiceSpecification
    with WizardTrackEventTestData
    with Mockito {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackServicesScope extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class WizardTrackEventProcessImplSpec extends WizardTrackEventProcessSpecification {

  "chooseAccount" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseAccount().mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseAccountEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseAccount().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseAccountEvent)
    }

  }

  "chooseNewConfiguration" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseNewConfiguration().mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseNewConfigurationEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseNewConfiguration().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseNewConfigurationEvent)
    }

  }

  "chooseExistingDevice" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseExistingDevice().mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseExistingDeviceEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseExistingDevice().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseExistingDeviceEvent)
    }

  }

  "chooseMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseMoment(OutAndAboutMoment).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseMomentEvent)
    }

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseMoment(HomeMorningMoment).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseMomentEvent.copy(label = Some("HOME")))
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseMoment(OutAndAboutMoment).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseMomentEvent)
    }

  }

  "chooseMomentWifi" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseMomentWifi(OutAndAboutMoment).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseMomentWifiEvent)
    }

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseMomentWifi(HomeNightMoment).mustRightUnit

      there was one(mockTrackServices).trackEvent(
        chooseMomentWifiEvent.copy(label = Some("NIGHT")))
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseMomentWifi(OutAndAboutMoment).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseMomentWifiEvent)
    }

  }

  "chooseOtherMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseOtherMoment(MusicMoment).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseOtherMomentEvent)
    }

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseOtherMoment(CarMoment).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseOtherMomentEvent.copy(label = Some("CAR")))
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseOtherMoment(MusicMoment).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseOtherMomentEvent)
    }

  }

}
