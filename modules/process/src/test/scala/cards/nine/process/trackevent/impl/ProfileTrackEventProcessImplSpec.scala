package cards.nine.process.trackevent.impl

import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.data.trackevent.ProfileTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait ProfileTrackEventProcessSpecification
  extends TaskServiceSpecification
  with ProfileTrackEventTestData
  with Mockito {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackServicesScope
    extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class ProfileTrackEventProcessImplSpec extends ProfileTrackEventProcessSpecification {

  "logout" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.logout().mustRightUnit

      there was one(mockTrackServices).trackEvent(logoutEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.logout().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(logoutEvent)
    }

  }

  "showAccountsContent" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.showAccountsContent().mustRightUnit

      there was one(mockTrackServices).trackEvent(showAccountsContentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.showAccountsContent().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(showAccountsContentEvent)
    }

  }

  "copyConfiguration" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.copyConfiguration().mustRightUnit

      there was one(mockTrackServices).trackEvent(copyConfigurationEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.copyConfiguration().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(copyConfigurationEvent)
    }

  }

  "synchronizeConfiguration" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.synchronizeConfiguration().mustRightUnit

      there was one(mockTrackServices).trackEvent(synchronizeConfigurationEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.synchronizeConfiguration().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(synchronizeConfigurationEvent)
    }

  }

  "changeConfigurationName" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.changeConfigurationName().mustRightUnit

      there was one(mockTrackServices).trackEvent(changeConfigurationNameEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.changeConfigurationName().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(changeConfigurationNameEvent)
    }

  }

  "showPublicationsContent" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.showPublicationsContent().mustRightUnit

      there was one(mockTrackServices).trackEvent(showPublicationsContentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.showPublicationsContent().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(showPublicationsContentEvent)
    }

  }

  "addToMyCollectionsFromProfile" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addToMyCollectionsFromProfile(publication).mustRightUnit

      there was one(mockTrackServices).trackEvent(addToMyCollectionsFromProfileEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addToMyCollectionsFromProfile(publication).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addToMyCollectionsFromProfileEvent)
    }

  }

  "shareCollectionFromProfile" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.shareCollectionFromProfile(publication).mustRightUnit

      there was one(mockTrackServices).trackEvent(shareCollectionFromProfileEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.shareCollectionFromProfile(publication).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(shareCollectionFromProfileEvent)
    }

  }

  "showSubscriptionsContent" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.showSubscriptionsContent().mustRightUnit

      there was one(mockTrackServices).trackEvent(showSubscriptionsContentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.showSubscriptionsContent().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(showSubscriptionsContentEvent)
    }

  }

  "subscribeToCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.subscribeToCollection(sharedCollectionId).mustRightUnit

      there was one(mockTrackServices).trackEvent(subscribeToCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.subscribeToCollection(sharedCollectionId).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(subscribeToCollectionEvent)
    }

  }

  "unsubscribeFromCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.unsubscribeFromCollection(sharedCollectionId).mustRightUnit

      there was one(mockTrackServices).trackEvent(unsubscribeFromCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.unsubscribeFromCollection(sharedCollectionId).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(unsubscribeFromCollectionEvent)
    }

  }

}
