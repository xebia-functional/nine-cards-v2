package cards.nine.process.trackevent.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.trackevent.WizardTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import monix.eval.Task
import org.specs2.mutable.Specification

trait WizardTrackEventProcessSpecification
  extends Specification
  with WizardTrackEventTestData
  with TrackServicesScope {

}

class WizardTrackEventProcessImplSpec extends WizardTrackEventProcessSpecification {

  "chooseAccount" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseAccount().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseAccountEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseAccount().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseAccountEvent)
    }

  }

  "chooseNewConfiguration" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseNewConfiguration().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseNewConfigurationEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseNewConfiguration().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseNewConfigurationEvent)
    }

  }

  "chooseCurrentDevice" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseCurrentDevice().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseCurrentDeviceEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseCurrentDevice().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseCurrentDeviceEvent)
    }

  }

  "chooseOtherDevices" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseOtherDevices().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseOtherDevicesEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseOtherDevices().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseOtherDevicesEvent)
    }

  }

  "chooseAllApps" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseAllApps().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseAllAppsEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseAllApps().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseAllAppsEvent)
    }

  }

  "chooseBestNineApps" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseBestNineApps().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseBestNineAppsEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseBestNineApps().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseBestNineAppsEvent)
    }

  }

  "chooseHome" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseHome().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseHomeEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseHome().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseHomeEvent)
    }

  }

  "chooseHomeWifi" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseHomeWifi().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseHomeWifiEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseHomeWifi().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseHomeWifiEvent)
    }

  }

  "chooseWork" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseWork().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseWorkEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseWork().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseWorkEvent)
    }

  }

  "chooseWorkWifi" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseWorkWifi().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseWorkWifiEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseWorkWifi().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseWorkWifiEvent)
    }

  }

  "chooseStudy" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseStudy().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseStudyEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseStudy().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseStudyEvent)
    }

  }

  "chooseStudyWifi" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseStudyWifi().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseStudyWifiEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseStudyWifi().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseStudyWifiEvent)
    }

  }

  "chooseMusic" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseMusic().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseMusicEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseMusic().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseMusicEvent)
    }

  }

  "chooseCar" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseCar().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseCarEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseCar().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseCarEvent)
    }

  }

  "chooseSport" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.chooseSport().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(chooseSportEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.chooseSport().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(chooseSportEvent)
    }

  }

}
