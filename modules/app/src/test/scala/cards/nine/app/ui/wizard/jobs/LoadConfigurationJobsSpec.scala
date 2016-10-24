package cards.nine.app.ui.wizard.jobs

import cards.nine.app.di.Injector
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.{MomentTestData, ApplicationTestData, ApiTestData}
import cards.nine.models.CloudStorageDevice
import cards.nine.process.cloud.CloudStorageProcess
import cards.nine.process.collection.{CollectionProcess, CollectionException}
import cards.nine.process.device.{DeviceProcess, AppException, DeviceException}
import cards.nine.process.moment.MomentProcess
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.Builder
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait LoadConfigurationJobsSpecification
  extends TaskServiceSpecification
    with Mockito
    with ApiTestData
    with ApplicationTestData
    with MomentTestData {

  trait LoadConfigurationJobsScope
    extends Scope {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    implicit val contextSupport = mock[ContextSupport]

    val deviceException = DeviceException("")

    val mockInjector: Injector = mock[Injector]

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val mockCloudStorageProcess = mock[CloudStorageProcess]

    mockInjector.cloudStorageProcess returns mockCloudStorageProcess

    val loadConfigurationJobs = new LoadConfigurationJobs() {

      override lazy val di: Injector = mockInjector

    }

  }

}

class LoadConfigurationJobsSpec
  extends LoadConfigurationJobsSpecification {

  "loadConfiguration" should {

    "loadConfiguration ok" in new LoadConfigurationJobsScope {

      val mockApiClient = mock[GoogleApiClient]
      val mockCloudId = "CloudId"

      mockDeviceProcess.resetSavedItems() returns serviceRight(Unit)
      mockDeviceProcess.synchronizeInstalledApps returns serviceRight(Unit)
      val result = loadConfigurationJobs.loadConfiguration(mockApiClient, mockCloudId)

    }
  }

}
