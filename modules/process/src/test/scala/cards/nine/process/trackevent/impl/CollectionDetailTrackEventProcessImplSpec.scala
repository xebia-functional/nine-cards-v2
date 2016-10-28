package cards.nine.process.trackevent.impl

import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.data.trackevent.CollectionDetailTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait CollectionDetailTrackEventProcessSpecification
  extends TaskServiceSpecification
  with CollectionDetailTrackEventTestData
  with Mockito {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackServicesScope
    extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class CollectionDetailTrackEventProcessImplSpec extends CollectionDetailTrackEventProcessSpecification {

  "openAppFromCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openAppFromCollection(entertainmentPackageName, entertainmentCategory).mustRightUnit

      there was one(mockTrackServices).trackEvent(openAppFromCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openAppFromCollection(entertainmentPackageName, entertainmentCategory).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openAppFromCollectionEvent)
    }

  }

  "addAppToCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addAppToCollection(entertainmentPackageName, entertainmentCategory).mustRightUnit

      there was one(mockTrackServices).trackEvent(addAppEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addAppToCollection(entertainmentPackageName, entertainmentCategory).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addAppEvent)
    }

  }

  "removeFromCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.removeFromCollection(entertainmentPackageName, entertainmentCategory).mustRightUnit

      there was one(mockTrackServices).trackEvent(removeEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.removeFromCollection(entertainmentPackageName, entertainmentCategory).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(removeEvent)
    }

  }

}
