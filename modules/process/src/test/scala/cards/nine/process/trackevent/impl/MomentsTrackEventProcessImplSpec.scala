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

  trait TrackServicesScope
    extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class MomentsTrackEventProcessImplSpec extends MomentsTrackEventProcessSpecification {

  "goToApplicationByMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToApplicationByMoment(momentName).mustRightUnit

      there was one(mockTrackServices).trackEvent(goToApplicationByMomentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToApplicationByMoment(momentName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToApplicationByMomentEvent)
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

  "chooseActiveMomentAction" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseActiveMomentAction(momentName).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseActiveMomentActionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseActiveMomentAction(momentName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseActiveMomentActionEvent)
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

  "goToGoogleSearch" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToGoogleSearch().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToGoogleSearchEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToGoogleSearch().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToGoogleSearchEvent)
    }

  }

  "quickAccessToCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.quickAccessToCollection(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(quickAccessToCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.quickAccessToCollection(collectionName).mustLeft[TrackEventException]

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

  "chooseMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.chooseMoment(momentName).mustRightUnit

      there was one(mockTrackServices).trackEvent(chooseMomentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.chooseMoment(momentName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(chooseMomentEvent)
    }

  }

  "editMomentByMenu" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.editMomentByMenu(momentName).mustRightUnit

      there was one(mockTrackServices).trackEvent(editMomentByMenuEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.editMomentByMenu(momentName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(editMomentByMenuEvent)
    }

  }

  "deleteMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.deleteMoment(momentName).mustRightUnit

      there was one(mockTrackServices).trackEvent(deleteMomentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.deleteMoment(momentName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(deleteMomentEvent)
    }

  }
}
