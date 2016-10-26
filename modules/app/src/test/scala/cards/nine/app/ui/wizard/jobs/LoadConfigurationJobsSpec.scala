package cards.nine.app.ui.wizard.jobs

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.JobException
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CloudStorageValues._
import cards.nine.commons.test.data._
import cards.nine.models.{MomentData, CloudStorageDevice, CloudStorageDeviceData}
import cards.nine.process.cloud.{CloudStorageProcessException, CloudStorageProcess}
import cards.nine.process.collection.CollectionProcess
import cards.nine.process.device.{DeviceException, DeviceProcess}
import cards.nine.process.moment.MomentProcess
import cards.nine.process.user.UserProcess
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait LoadConfigurationJobsSpecification
  extends TaskServiceSpecification
    with Mockito
    with ApiTestData
    with ApplicationTestData
    with MomentTestData
    with CloudStorageTestData
    with CollectionTestData
    with DockAppTestData {

  trait LoadConfigurationJobsScope
    extends Scope {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    implicit val contextSupport = mock[ContextSupport]

    val cloudStorageProcessException = CloudStorageProcessException("")

    val mockInjector: Injector = mock[Injector]

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val mockCloudStorageProcess = mock[CloudStorageProcess]

    mockInjector.cloudStorageProcess returns mockCloudStorageProcess

    val mockUserProcess = mock[UserProcess]

    mockInjector.userProcess returns mockUserProcess

    val mockApiClient = mock[GoogleApiClient]


    val loadConfigurationJobs = new LoadConfigurationJobs() {

      override lazy val di: Injector = mockInjector

    }

  }

}

class LoadConfigurationJobsSpec
  extends LoadConfigurationJobsSpecification {

  "loadConfiguration" should {

    "return a valid response when the service returns a right response" in new LoadConfigurationJobsScope {

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps(any) returns serviceRight(Unit)
      mockCloudStorageProcess.getCloudStorageDevice(mockApiClient, cloudId) returns serviceRight(cloudStorageDevice)

      mockCollectionProcess.createCollectionsFromCollectionData(any)(any) returns serviceRight(seqCollection)
      mockMomentProcess.saveMoments(any)(any) returns serviceRight(seqMoment)
      mockDeviceProcess.saveDockApps(any) returns serviceRight(seqDockApp)
      mockUserProcess.updateUserDevice(any, any, any)(any) returns serviceRight(Unit)

      loadConfigurationJobs.loadConfiguration(mockApiClient, cloudId).mustRightUnit

//      there was one(mockCollectionProcess).createCollectionsFromFormedCollections(seqCollection)
      there was one(mockMomentProcess).saveMoments(===(momentSeq getOrElse Seq.empty))(any)
//      there was one(mockDeviceProcess).saveDockApps(any)
      there was one(mockUserProcess).updateUserDevice(===(cloudStorageDevice.data.deviceName),===(cloudStorageDevice.cloudId),any)(any)

    }

    "return a valid response when the device doesn't have moments" in new LoadConfigurationJobsScope {

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps(any) returns serviceRight(Unit)
      mockCloudStorageProcess.getCloudStorageDevice(mockApiClient, cloudId) returns serviceRight(cloudStorageDevice.copy(data = cloudStorageDevice.data.copy(moments = Option(Seq.empty))))

      mockCollectionProcess.createCollectionsFromCollectionData(any)(any) returns serviceRight(seqCollection)
      mockMomentProcess.saveMoments(any)(any) returns serviceRight(seqMoment)
      mockDeviceProcess.saveDockApps(any) returns serviceRight(seqDockApp)
      mockUserProcess.updateUserDevice(any, any, any)(any) returns serviceRight(Unit)

      loadConfigurationJobs.loadConfiguration(mockApiClient, cloudId).mustRightUnit

//      there was one(mockCollectionProcess).createCollectionsFromFormedCollections(seqCollection)
      there was one(mockMomentProcess).saveMoments(===(Seq.empty))(any)
      //      there was one(mockDeviceProcess).saveDockApps(any)
      there was one(mockUserProcess).updateUserDevice(===(cloudStorageDevice.data.deviceName),===(cloudStorageDevice.cloudId),any)(any)

    }

    "return a valid response when the device doesn't have dockApps" in new LoadConfigurationJobsScope {

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps(any) returns serviceRight(Unit)
      mockCloudStorageProcess.getCloudStorageDevice(mockApiClient, cloudId) returns serviceRight(cloudStorageDevice.copy(data = cloudStorageDevice.data.copy(dockApps = Option(Seq.empty))))

      mockCollectionProcess.createCollectionsFromCollectionData(any)(any) returns serviceRight(seqCollection)
      mockMomentProcess.saveMoments(any)(any) returns serviceRight(seqMoment)
      mockDeviceProcess.saveDockApps(any) returns serviceRight(seqDockApp)
      mockUserProcess.updateUserDevice(any, any, any)(any) returns serviceRight(Unit)

      loadConfigurationJobs.loadConfiguration(mockApiClient, cloudId).mustRightUnit

      //there was one(mockCollectionProcess).createCollectionsFromFormedCollections(seqCollection)
      there was one(mockMomentProcess).saveMoments(===(momentSeq getOrElse Seq.empty))(any)
      there was one(mockDeviceProcess).saveDockApps(Seq.empty)
      there was one(mockUserProcess).updateUserDevice(===(cloudStorageDevice.data.deviceName),===(cloudStorageDevice.cloudId),any)(any)

    }

    "return a JobException when the device doesn't have collections" in new LoadConfigurationJobsScope {

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps(any) returns serviceRight(Unit)
      mockCloudStorageProcess.getCloudStorageDevice(mockApiClient, cloudId) returns serviceRight(cloudStorageDevice.copy(data = cloudStorageDevice.data.copy(collections = Seq.empty)))

      loadConfigurationJobs.loadConfiguration(mockApiClient, cloudId).mustLeft[JobException]

    }

    "return a CloudStorageProcessException when the service returns an exception" in new LoadConfigurationJobsScope {

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps(any) returns serviceRight(Unit)
      mockCloudStorageProcess.getCloudStorageDevice(mockApiClient, cloudId) returns serviceLeft(cloudStorageProcessException)

      loadConfigurationJobs.loadConfiguration(mockApiClient, cloudId).mustLeft[CloudStorageProcessException]

    }
  }

}
