package cards.nine.app.ui.commons.dialogs.publicCollections

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.dialogs.publicollections.{PublicCollectionsJobs, PublicCollectionsUiActions}
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.{ApplicationTestData, SharedCollectionTestData}
import cards.nine.models.types.{GetByName, TopSharedCollection}
import cards.nine.process.collection.{CollectionException, CollectionProcess}
import cards.nine.process.device.{AppException, DeviceProcess}
import cards.nine.process.intents.LauncherExecutorProcess
import cards.nine.process.sharedcollections.SharedCollectionsProcess
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import cards.nine.app.ui.commons.dialogs.publicollections.PublicCollectionsFragment._


trait PublicCollectionsJobsSpecification
  extends TaskServiceSpecification
    with Mockito {

  trait PublicCollectionsScope
    extends Scope
      with SharedCollectionTestData
      with ApplicationTestData {

    val exception = new Throwable("")

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector: Injector = mock[Injector]

    val mockPublicCollectionsUiActions = mock[PublicCollectionsUiActions]

    val mockLauncherExecutorProcess = mock[LauncherExecutorProcess]

    mockInjector.launcherExecutorProcess returns mockLauncherExecutorProcess

    val mockSharedCollectionsProcess = mock[SharedCollectionsProcess]

    mockInjector.sharedCollectionsProcess returns mockSharedCollectionsProcess

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val publicCollectionsJobs = new PublicCollectionsJobs(mockPublicCollectionsUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def getString(res: Int, formatArgs: scala.AnyRef*): String = ""
    }

  }

}

class PublicCollectionsJobsJobsSpec
  extends PublicCollectionsJobsSpecification {

  sequential
  "initialize" should {
    "returns a valid response when the service returns a right response" in new PublicCollectionsScope {

      mockTrackEventProcess.openPublicCollections() returns serviceRight(Unit)
      mockPublicCollectionsUiActions.initialize() returns serviceRight(Unit)
      mockPublicCollectionsUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getSharedCollectionsByCategory(any, any, any, any)(any) returns serviceRight(seqSharedCollection)
      mockPublicCollectionsUiActions.loadPublicCollections(any) returns serviceRight(Unit)

      publicCollectionsJobs.initialize().mustRightUnit

      there was one(mockTrackEventProcess).openPublicCollections()
      there was one(mockPublicCollectionsUiActions).initialize()
    }

    "shows a empty messages when the service returns a Sequence empty" in new PublicCollectionsScope {

      mockTrackEventProcess.openPublicCollections() returns serviceRight(Unit)
      mockPublicCollectionsUiActions.initialize() returns serviceRight(Unit)
      mockPublicCollectionsUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getSharedCollectionsByCategory(any, any, any, any)(any) returns serviceRight(Seq.empty)
      mockPublicCollectionsUiActions.showEmptyMessageInScreen() returns serviceRight(Unit)

      publicCollectionsJobs.initialize().mustRightUnit

      there was one(mockTrackEventProcess).openPublicCollections()
      there was one(mockPublicCollectionsUiActions).initialize()
    }
  }

  "loadPublicCollections" should {
    "returns a valid response and load a public collections when the service returns a right response" in new PublicCollectionsScope {

      mockPublicCollectionsUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getSharedCollectionsByCategory(any, any, any, any)(any) returns serviceRight(seqSharedCollection)
      mockPublicCollectionsUiActions.loadPublicCollections(any) returns serviceRight(Unit)

      publicCollectionsJobs.loadPublicCollections().mustRightUnit

      there was one(mockPublicCollectionsUiActions).showLoading()
      there was one(mockSharedCollectionsProcess).getSharedCollectionsByCategory(===(statuses.category), ===(statuses.typeSharedCollection), any, any)(any)
      there was one(mockPublicCollectionsUiActions).loadPublicCollections(seqSharedCollection)
    }

    "shows a empty messages when the service returns a Sequence empty" in new PublicCollectionsScope {

      mockPublicCollectionsUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getSharedCollectionsByCategory(any, any, any, any)(any) returns serviceRight(Seq.empty)
      mockPublicCollectionsUiActions.showEmptyMessageInScreen() returns serviceRight(Unit)

      publicCollectionsJobs.loadPublicCollections().mustRightUnit

      there was one(mockPublicCollectionsUiActions).showLoading()
      there was one(mockSharedCollectionsProcess).getSharedCollectionsByCategory(===(statuses.category), ===(statuses.typeSharedCollection), any, any)(any)
      there was one(mockPublicCollectionsUiActions).showEmptyMessageInScreen()
    }
  }

  "loadPublicCollectionsByCategory" should {
    "returns a valid response and updated category when the service returns a right response" in new PublicCollectionsScope {

      mockPublicCollectionsUiActions.updateCategory(any) returns serviceRight(Unit)
      mockPublicCollectionsUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getSharedCollectionsByCategory(any, any, any, any)(any) returns serviceRight(seqSharedCollection)
      mockPublicCollectionsUiActions.loadPublicCollections(any) returns serviceRight(Unit)

      publicCollectionsJobs.loadPublicCollectionsByCategory(sharedCollection.category).mustRightUnit

      there was one(mockPublicCollectionsUiActions).updateCategory(category)
    }

    "shows a empty messages when the service returns a Sequence empty" in new PublicCollectionsScope {

      mockPublicCollectionsUiActions.updateCategory(any) returns serviceRight(Unit)
      mockPublicCollectionsUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getSharedCollectionsByCategory(any, any, any, any)(any) returns serviceRight(Seq.empty)
      mockPublicCollectionsUiActions.showEmptyMessageInScreen() returns serviceRight(Unit)

      publicCollectionsJobs.loadPublicCollectionsByCategory(sharedCollection.category).mustRightUnit

      there was one(mockPublicCollectionsUiActions).updateCategory(category)
    }
  }

  "loadPublicCollectionsByTypeSharedCollection" should {
    "returns a valid response and updated type category when the service returns a right response" in new PublicCollectionsScope {

      mockPublicCollectionsUiActions.updateTypeCollection(any) returns serviceRight(Unit)
      mockPublicCollectionsUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getSharedCollectionsByCategory(any, any, any, any)(any) returns serviceRight(seqSharedCollection)
      mockPublicCollectionsUiActions.loadPublicCollections(any) returns serviceRight(Unit)

      publicCollectionsJobs.loadPublicCollectionsByTypeSharedCollection(TopSharedCollection).mustRightUnit

      there was one(mockPublicCollectionsUiActions).updateTypeCollection(TopSharedCollection)
    }

    "shows a empty messages when the service returns a Sequence empty" in new PublicCollectionsScope {

      mockPublicCollectionsUiActions.updateTypeCollection(any) returns serviceRight(Unit)
      mockPublicCollectionsUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getSharedCollectionsByCategory(any, any, any, any)(any) returns serviceRight(Seq.empty)
      mockPublicCollectionsUiActions.showEmptyMessageInScreen() returns serviceRight(Unit)

      publicCollectionsJobs.loadPublicCollectionsByTypeSharedCollection(TopSharedCollection).mustRightUnit

      there was one(mockPublicCollectionsUiActions).updateTypeCollection(TopSharedCollection)
    }
  }

  "saveSharedCollection" should {
    "returns a valid response  when the service returns a right response" in new PublicCollectionsScope {

      mockSharedCollectionsProcess.updateViewSharedCollection(any)(any) returns serviceRight(Unit)
      mockTrackEventProcess.createNewCollectionFromPublicCollection(any) returns serviceRight(Unit)
      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)
      mockCollectionProcess.addCollection(any) returns serviceRight(collection)
      mockPublicCollectionsUiActions.close() returns serviceRight(Unit)

      publicCollectionsJobs.saveSharedCollection(sharedCollection) mustRight (_ shouldEqual collection)

      there was one(mockSharedCollectionsProcess).updateViewSharedCollection(===(sharedCollection.id))(any)
      there was one(mockDeviceProcess).getSavedApps(===(GetByName))(any)

    }

    "returns a AppException when the service returns an exception" in new PublicCollectionsScope {

      mockSharedCollectionsProcess.updateViewSharedCollection(any)(any) returns serviceRight(Unit)
      mockTrackEventProcess.createNewCollectionFromPublicCollection(any) returns serviceRight(Unit)
      mockDeviceProcess.getSavedApps(any)(any) returns serviceLeft(AppException(""))

      publicCollectionsJobs.saveSharedCollection(sharedCollection).mustLeft[AppException]

      there was one(mockSharedCollectionsProcess).updateViewSharedCollection(===(sharedCollection.id))(any)
      there was one(mockDeviceProcess).getSavedApps(===(GetByName))(any)

    }
    "returns a CollectionException when the service returns an exception" in new PublicCollectionsScope {

      mockSharedCollectionsProcess.updateViewSharedCollection(any)(any) returns serviceRight(Unit)
      mockTrackEventProcess.createNewCollectionFromPublicCollection(any) returns serviceRight(Unit)
      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)
      mockCollectionProcess.addCollection(any) returns serviceLeft(CollectionException(""))

      publicCollectionsJobs.saveSharedCollection(sharedCollection).mustLeft[CollectionException]

      there was one(mockSharedCollectionsProcess).updateViewSharedCollection(===(sharedCollection.id))(any)
      there was one(mockDeviceProcess).getSavedApps(===(GetByName))(any)

    }
  }

  "shareCollection" should {
    "returns a valid response when call to launchShare " in new PublicCollectionsScope {

      mockLauncherExecutorProcess.launchShare(any)(any) returns serviceRight(Unit)
      publicCollectionsJobs.shareCollection(sharedCollection).mustRightUnit
      there was one(mockLauncherExecutorProcess).launchShare(any)(any)

    }
  }

}
