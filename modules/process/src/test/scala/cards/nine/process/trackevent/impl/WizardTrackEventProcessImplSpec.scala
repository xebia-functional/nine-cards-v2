package cards.nine.process.trackevent.impl

import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.trackevent.WizardTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.data.trackevent.LauncherTrackEventTestData
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

  trait TrackServicesScope
    extends Scope {

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

  "chooseAppNumber" should {

    "track the app with the right parameter for all the apps" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseAppNumber(bestNine = false).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseAppNumberEvent)
    }

    "track the app with the right parameter for the best nine apps" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseAppNumber(bestNine = true).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseAppNumberEvent.copy(label = Some("BestNineApps")))
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseAppNumber(bestNine = false).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseAppNumberEvent)
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

      there was one(mockTrackServices).trackEvent(chooseMomentWifiEvent.copy(label = Some("NIGHT")))
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
