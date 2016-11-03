package cards.nine.app.ui.wizard.jobs

import cards.nine.app.di.Injector
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.{ApiTestData, ApplicationTestData, MomentTestData}
import cards.nine.models.{MomentData, MomentTimeSlot}
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
    visibilityUiActions.hideFistStepAndShowLoadingBetterCollections(any) returns serviceRight(Unit)
    visibilityUiActions.hideSecondStepAndShowLoadingSavingCollection returns serviceRight(Unit)
    visibilityUiActions.showLoadingSavingMoments returns serviceRight(Unit)
    visibilityUiActions.showNewConfiguration() returns serviceRight(Unit)

    val newConfigurationUiActions = mock[NewConfigurationUiActions]
    newConfigurationUiActions.loadFifthStep() returns serviceRight(Unit)
    newConfigurationUiActions.loadSecondStep(any) returns serviceRight(Unit)
    newConfigurationUiActions.loadThirdStep() returns serviceRight(Unit)
    newConfigurationUiActions.loadFourthStep(any, any) returns serviceRight(Unit)
    newConfigurationUiActions.loadFifthStep() returns serviceRight(Unit)
    newConfigurationUiActions.loadSixthStep() returns serviceRight(Unit)

    val mockInjector: Injector = mock[Injector]

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val newConfigurationJobs = new NewConfigurationJobs(newConfigurationUiActions, visibilityUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

    }

  }

}

class NewConfigurationJobsSpec
  extends NewConfigurationJobsSpecification {


  "loadBetterCollections" should {

    "return a Seq of PackagesByCategory " in new NewConfigurationJobsScope {

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps(any) returns serviceRight(Unit)
      mockCollectionProcess.rankApps()(any) returns serviceRight(seqPackagesByCategory)

      newConfigurationJobs.loadBetterCollections(hidePrevious = true) mustRightUnit

      there was one(visibilityUiActions).hideFistStepAndShowLoadingBetterCollections(any)
      there was one(mockDeviceProcess).resetSavedItems()
      there was one(mockDeviceProcess).synchronizeInstalledApps(any)
    }

    "return a Seq empty if the category of the collections are Misc" in new NewConfigurationJobsScope {

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps(any) returns serviceRight(Unit)
      mockCollectionProcess.rankApps()(any) returns serviceRight(seqPackagesByCategory map (_.copy(category = Misc)))

      newConfigurationJobs.loadBetterCollections(hidePrevious = true) mustRightUnit

      there was one(visibilityUiActions).hideFistStepAndShowLoadingBetterCollections(any)
      there was one(mockDeviceProcess).resetSavedItems()
      there was one(mockDeviceProcess).synchronizeInstalledApps(any)
    }

    "return a Seq empty if the packages size of the collections are less than 3" in new NewConfigurationJobsScope {

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps(any) returns serviceRight(Unit)
      mockCollectionProcess.rankApps()(any) returns serviceRight(seqPackagesByCategory map (_.copy(packages = Seq.empty)))

      newConfigurationJobs.loadBetterCollections(hidePrevious = true) mustRightUnit

      there was one(visibilityUiActions).hideFistStepAndShowLoadingBetterCollections(any)
      there was one(mockDeviceProcess).resetSavedItems()
      there was one(mockDeviceProcess).synchronizeInstalledApps(any)
    }

    "return a CollectionException when the service returns an exception" in new NewConfigurationJobsScope {

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps(any) returns serviceRight(Unit)
      mockCollectionProcess.rankApps()(any) returns serviceLeft(collectionException)

      newConfigurationJobs.loadBetterCollections(hidePrevious = true).mustLeft[CollectionException]

      there was one(visibilityUiActions).hideFistStepAndShowLoadingBetterCollections(any)
      there was one(mockDeviceProcess).resetSavedItems()
      there was one(mockDeviceProcess).synchronizeInstalledApps(any)
    }
  }


  "saveCollections" should {

    "return a DeviceException when the service returns an exception" in new NewConfigurationJobsScope {

      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)

      newConfigurationJobs.saveCollections(seqPackagesByCategory).mustRightUnit

      there was one(visibilityUiActions).hideSecondStepAndShowLoadingSavingCollection()
      there was no(mockCollectionProcess).createCollectionsFromCollectionData(any)(any)
      there was no(mockDeviceProcess).generateDockApps(===(newConfigurationJobs.defaultDockAppsSize))(any)

    }.pendingUntilFixed("Issue #984")


    "return a DeviceException when the service returns an exception" in new NewConfigurationJobsScope {

      mockDeviceProcess.getSavedApps(any)(any) returns serviceLeft(appException)

      newConfigurationJobs.saveCollections(seqPackagesByCategory).mustLeft[AppException]

      there was one(visibilityUiActions).hideSecondStepAndShowLoadingSavingCollection()
      there was no(mockCollectionProcess).createCollectionsFromCollectionData(any)(any)
      there was no(mockDeviceProcess).generateDockApps(===(newConfigurationJobs.defaultDockAppsSize))(any)
    }

    "return a DeviceException when the service returns an exception" in new NewConfigurationJobsScope {

      mockDeviceProcess.getSavedApps(any)(any) returns serviceLeft(appException)

      newConfigurationJobs.saveCollections(seqPackagesByCategory).mustLeft[AppException]

      there was one(visibilityUiActions).hideSecondStepAndShowLoadingSavingCollection()
      there was no(mockCollectionProcess).createCollectionsFromCollectionData(any)(any)
      there was no(mockDeviceProcess).generateDockApps(===(newConfigurationJobs.defaultDockAppsSize))(any)
    }
  }

  "loadMomentWithWifi" should {

    "return a Seq that contains all configured networks" in new NewConfigurationJobsScope {

      val networks = 0 to 10 map (c => s"Networks $c")
      mockDeviceProcess.getConfiguredNetworks(any) returns serviceRight(networks)

      newConfigurationJobs.loadMomentWithWifi(hidePrevious = true).mustRightUnit

      there was one(visibilityUiActions).hideThirdStep()
    }

    "return a DeviceException when the service returns an exception" in new NewConfigurationJobsScope {

      mockDeviceProcess.getConfiguredNetworks(any) returns serviceLeft(deviceException)

      newConfigurationJobs.loadMomentWithWifi(hidePrevious = true).mustLeft[DeviceException]

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
      there was one(mockMomentProcess).saveMoments(===(momentsWithWifi ++ minMomentsWithWifi ++ homeNightMoment))(any)
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

      val moments: Seq[NineCardsMoment] = Seq(HomeMorningMoment, MusicMoment, CarMoment, SportMoment, OutAndAboutMoment)
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
