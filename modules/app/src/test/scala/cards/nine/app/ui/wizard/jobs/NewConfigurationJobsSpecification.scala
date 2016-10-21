package cards.nine.app.ui.wizard.jobs

import cards.nine.app.di.Injector
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.{MomentTestData, ApplicationTestData, ApiTestData}
import cards.nine.models.{MomentTimeSlot, MomentData}
import cards.nine.models.types._
import cards.nine.process.collection.{CollectionException, CollectionProcess}
import cards.nine.process.device.{AppException, DeviceException, DeviceProcess}
import cards.nine.process.moment.MomentProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait NewConfigurationJobsSpecification
  extends TaskServiceSpecification
    with Mockito
    with ApiTestData
    with ApplicationTestData
    with MomentTestData {

  trait NewConfigurationJobsScope
    extends Scope {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    implicit val contextSupport = mock[ContextSupport]

    val deviceException = DeviceException("")

    val collectionException = CollectionException("")

    val appException = AppException("")

    val visibilityUiActions = mock[VisibilityUiActions]
    visibilityUiActions.hideThirdStep() returns serviceRight(Unit)
    visibilityUiActions.fadeOutInAllChildInStep returns serviceRight(Unit)
    visibilityUiActions.hideFistStepAndShowLoadingBetterCollections returns serviceRight(Unit)
    visibilityUiActions.hideSecondStepAndShowLoadingSavingCollection returns serviceRight(Unit)
    visibilityUiActions.showLoadingSavingMoments returns serviceRight(Unit)

    val mockInjector: Injector = mock[Injector]

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val newConfigurationJobs = new NewConfigurationJobs(visibilityUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

    }

  }

}

class NewConfigurationJobsSpec
  extends NewConfigurationJobsSpecification {

  //TODO: For now, we are looking the better experience and we are filtering the collections
  "loadBetterCollections" should {

    "loadBetterCollections ok" in new NewConfigurationJobsScope {

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps(any) returns serviceRight(Unit)
      mockCollectionProcess.rankApps()(any) returns serviceRight(packagesByCategory)

      newConfigurationJobs.loadBetterCollections() mustRight (_ shouldEqual Seq.empty)

      there was one(visibilityUiActions).hideFistStepAndShowLoadingBetterCollections
      there was one(mockDeviceProcess).resetSavedItems()
      there was one(mockDeviceProcess).synchronizeInstalledApps(any)
    }

    "return a CollectionException when the service returns an exception" in new NewConfigurationJobsScope {

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps(any) returns serviceRight(Unit)
      mockCollectionProcess.rankApps()(any) returns serviceLeft(collectionException)

      newConfigurationJobs.loadBetterCollections().mustLeft[CollectionException]

      there was one(visibilityUiActions).hideFistStepAndShowLoadingBetterCollections
      there was one(mockDeviceProcess).resetSavedItems()
      there was one(mockDeviceProcess).synchronizeInstalledApps(any)
    }
  }

  //TODO:Review when modified FormedCollection
  "saveCollections" should {

    "return a DeviceException when the service returns an exception" in new NewConfigurationJobsScope {

      mockDeviceProcess.getSavedApps(any)(any) returns serviceLeft(appException)

      newConfigurationJobs.saveCollections(packagesByCategory, false).mustLeft[AppException]

      there was one(visibilityUiActions).hideSecondStepAndShowLoadingSavingCollection()
      there was no(mockCollectionProcess).createCollectionsFromFormedCollections(any)(any)
      there was no(mockDeviceProcess).generateDockApps(===(newConfigurationJobs.defaultDockAppsSize))(any)
    }

    "return a DeviceException when the service returns an exception" in new NewConfigurationJobsScope {

      mockDeviceProcess.getSavedApps(any)(any) returns serviceLeft(appException)

      newConfigurationJobs.saveCollections(packagesByCategory, true).mustLeft[AppException]

      there was one(visibilityUiActions).hideSecondStepAndShowLoadingSavingCollection()
      there was no(mockCollectionProcess).createCollectionsFromFormedCollections(any)(any)
      there was no(mockDeviceProcess).generateDockApps(===(newConfigurationJobs.defaultDockAppsSize))(any)
    }
  }

  "loadMomentWithWifi" should {

    "return a Seq that contains all configured networks" in new NewConfigurationJobsScope {

      val networks = 0 to 10 map (c => s"Networks $c")
      mockDeviceProcess.getConfiguredNetworks(any) returns serviceRight(networks)

      newConfigurationJobs.loadMomentWithWifi() mustRight (_ shouldEqual networks)

      there was one(visibilityUiActions).hideThirdStep()
    }

    "return a DeviceException when the service returns an exception" in new NewConfigurationJobsScope {

      mockDeviceProcess.getConfiguredNetworks(any) returns serviceLeft(deviceException)

      newConfigurationJobs.loadMomentWithWifi().mustLeft[DeviceException]

      there was one(visibilityUiActions).hideThirdStep()
    }

  }

  "saveMomentsWithWifi" should {

    "call to saveMoments with Seq.empty and include WalkMoment" in new NewConfigurationJobsScope {

      val infoMoment = Seq.empty
      mockMomentProcess.saveMoments(any)(any) returns serviceRight(Seq.empty)
      newConfigurationJobs.saveMomentsWithWifi(infoMoment).mustRightUnit

      there was one(visibilityUiActions).fadeOutInAllChildInStep
      there was one(mockMomentProcess).saveMoments(===(minMomentsWithWifi))(any)
    }

    "call to saveMoments with HomeMorningMoment and include WalkMoment and HomeNightMoment" in new NewConfigurationJobsScope {

      val infoMoment: Seq[(NineCardsMoment, Option[String])] = Seq((HomeMorningMoment, Option("wifi")))
      val momentsWithWifi = infoMoment map {
        case (moment, wifi) => momentData(moment, wifi)
      }
      mockMomentProcess.saveMoments(any)(any) returns serviceRight(Seq.empty)
      newConfigurationJobs.saveMomentsWithWifi(infoMoment).mustRightUnit

      there was one(visibilityUiActions).fadeOutInAllChildInStep
      there was one(mockMomentProcess).saveMoments(===(momentsWithWifi ++ minMomentsWithWifi ++ nightMoment))(any)
    }

    "call to saveMoments with other moments and include WalkMoment" in new NewConfigurationJobsScope {

      val infoMoment: Seq[(NineCardsMoment, Option[String])] = Seq((WorkMoment, Option("wifi")), (StudyMoment, Option("wifi1")))

      val momentsWithWifi = infoMoment map {
        case (moment, wifi) => momentData(moment, wifi)
      }
      mockMomentProcess.saveMoments(any)(any) returns serviceRight(Seq.empty)
      newConfigurationJobs.saveMomentsWithWifi(infoMoment).mustRightUnit

      there was one(visibilityUiActions).fadeOutInAllChildInStep
      there was one(mockMomentProcess).saveMoments(===(momentsWithWifi ++ minMomentsWithWifi))(any)
    }

  }

  "saveMoments" should {

    "call to saveMoments with Seq.empty" in new NewConfigurationJobsScope {

      val moments: Seq[NineCardsMoment] = Seq.empty

      mockMomentProcess.saveMoments(any)(any) returns serviceRight(Seq.empty)
      newConfigurationJobs.saveMoments(moments).mustRightUnit

      there was one(visibilityUiActions).showLoadingSavingMoments()
      there was one(mockMomentProcess).saveMoments(===(Seq.empty))(any)
    }

    "call to saveMoments with the right param " in new NewConfigurationJobsScope {

      val moments: Seq[NineCardsMoment] = Seq(HomeMorningMoment, MusicMoment, CarMoment, RunningMoment, BikeMoment)
      val momentsWithoutWifi = moments map {
        moment => momentData(moment, None)
      }
      mockMomentProcess.saveMoments(any)(any) returns serviceRight(Seq.empty)
      newConfigurationJobs.saveMoments(moments).mustRightUnit

      there was one(visibilityUiActions).showLoadingSavingMoments()
      there was one(mockMomentProcess).saveMoments(===(momentsWithoutWifi))(any)
    }
  }
}
