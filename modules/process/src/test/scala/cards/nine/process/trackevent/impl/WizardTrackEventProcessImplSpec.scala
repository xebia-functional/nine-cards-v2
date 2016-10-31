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
import cards.nine.models.types.{MusicMoment, HomeMorningMoment, AppCategory, Game}
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

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseAppNumber(any).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseAppNumberEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseAppNumber(any).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseAppNumberEvent)
    }

  }

  "chooseMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseMoment(HomeMorningMoment).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseMomentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseMoment(HomeMorningMoment).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseMomentEvent)
    }

  }

  "chooseMomentWifi" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseMomentWifi(HomeMorningMoment).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseMomentWifiEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseMomentWifi(HomeMorningMoment).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseMomentWifiEvent)
    }

  }

  "chooseOtherMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseOtherMoment(MusicMoment).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseOtherMomentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseOtherMoment(MusicMoment).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseOtherMomentEvent)
    }

  }

}
