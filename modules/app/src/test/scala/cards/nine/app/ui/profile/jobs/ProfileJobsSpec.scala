/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.profile.jobs

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.{BroadAction, JobException, RequestCodes}
import cards.nine.app.ui.launcher.jobs.LauncherTestData
import cards.nine.app.ui.profile.ProfileActivity._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CloudStorageValues._
import cards.nine.commons.test.data.UserValues._
import cards.nine.commons.test.data.{CloudStorageTestData, SharedCollectionTestData, UserTestData}
import cards.nine.models.RawCloudStorageDevice
import cards.nine.models.types.{PublishedByMe, PublishedByOther}
import cards.nine.process.cloud.CloudStorageProcess
import cards.nine.process.cloud.impl.CloudStorageProcessImplData
import cards.nine.process.collection.{CollectionException, CollectionProcess}
import cards.nine.process.device.{AppException, DeviceProcess}
import cards.nine.process.intents.LauncherExecutorProcess
import cards.nine.process.moment.MomentProcess
import cards.nine.process.sharedcollections.SharedCollectionsProcess
import cards.nine.process.theme.ThemeProcess
import cards.nine.process.trackevent.TrackEventProcess
import cards.nine.process.user.{UserException, UserProcess}
import cards.nine.process.widget.WidgetProcess
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait ProfileJobsSpecification extends TaskServiceSpecification with Mockito {

  trait ProfileJobsScope
      extends Scope
      with LauncherTestData
      with UserTestData
      with SharedCollectionTestData
      with CloudStorageTestData
      with CloudStorageProcessImplData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    implicit val contextSupport = mock[ContextSupport]

    val mockResources = mock[Resources]

    contextSupport.getResources returns mockResources

    val mockInjector = mock[Injector]

    val mockProfileUiActions = mock[ProfileUiActions]

    val mockApiClient = mock[GoogleApiClient]

    val mockThemeProcess = mock[ThemeProcess]

    mockInjector.themeProcess returns mockThemeProcess

    val mockUserProcess = mock[UserProcess]

    mockInjector.userProcess returns mockUserProcess

    val mockCloudStorageProcess = mock[CloudStorageProcess]

    mockInjector.cloudStorageProcess returns mockCloudStorageProcess

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockLauncherExecutorProcess = mock[LauncherExecutorProcess]

    mockInjector.launcherExecutorProcess returns mockLauncherExecutorProcess

    val mockSharedCollectionsProcess = mock[SharedCollectionsProcess]

    mockInjector.sharedCollectionsProcess returns mockSharedCollectionsProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val mockWidgetProcess = mock[WidgetProcess]

    mockInjector.widgetsProcess returns mockWidgetProcess

    val mockIntent = mock[Intent]

    val profileJobs = new ProfileJobs(mockProfileUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def getThemeTask = TaskService.right(theme)

      override def themeFile = ""

      override def sendBroadCastTask(broadAction: BroadAction) = TaskService.empty

      override def withActivityTask(f: (AppCompatActivity => Unit)) = TaskService.empty

      override def getString(res: Int): String = ""

      override def getString(res: Int, args: AnyRef*): String = ""

    }

  }

}

class ProfileJobsSpec extends ProfileJobsSpecification {

  sequential
  "driveConnected" should {

    "returns a valid response when apiclient is connected" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.isConnected returns true

      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockCloudStorageProcess.getCloudStorageDevices(any)(any) returns serviceRight(Seq.empty)
      mockProfileUiActions.showEmptyAccountsContent(any) returns serviceRight(Unit)
      mockTrackEventProcess.showAccountsContent() returns serviceRight(Unit)

      profileJobs.driveConnected().mustRightUnit

      there was one(mockProfileUiActions).showLoading()
    }

    "returns a valid response when apiclient isn't connected" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.isConnected returns false
      mockProfileUiActions.showLoading() returns serviceRight(Unit)

      profileJobs.driveConnected().mustRightUnit

      there was one(mockProfileUiActions).showLoading()
    }

    "returns a JobException when the connection throws a exception" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.isConnected returns false
      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockApiClient.connect() throws new RuntimeException("")

      profileJobs.driveConnected().mustLeft[JobException]

      there was one(mockProfileUiActions).showLoading()
    }

    "Do nothing when ApiClient is None" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = None)
      mockProfileUiActions.showLoading() returns serviceRight(Unit)

      profileJobs.driveConnected().mustRightUnit

      there was one(mockProfileUiActions).showLoading()
    }
  }

  "initialize" should {
    "initialize profile jobs when the service returns a right response" in new ProfileJobsScope {

      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockProfileUiActions.initialize(any) returns serviceRight(Unit)
      mockUserProcess.getUser(any) returns serviceRight(user)
      mockCloudStorageProcess.createCloudStorageClient(any)(any) returns serviceRight(
        mockApiClient)
      mockProfileUiActions.userProfile(any, any, any) returns serviceRight(Unit)

      profileJobs.initialize().mustRightUnit

      there was one(mockProfileUiActions).initialize(theme)
      there was one(mockCloudStorageProcess).createCloudStorageClient(
        ===(user.email.getOrElse("")))(any)
      there was one(mockProfileUiActions)
        .userProfile(user.userProfile.name, user.email.getOrElse(""), user.userProfile.avatar)
    }

    "returns a JobException when the user doesn't have email" in new ProfileJobsScope {

      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockProfileUiActions.initialize(any) returns serviceRight(Unit)
      mockUserProcess.getUser(any) returns serviceRight(user.copy(email = None))

      profileJobs.initialize().mustLeft[JobException]

      there was one(mockProfileUiActions).initialize(theme)
    }

    "returns a JobException when the process returns a exception" in new ProfileJobsScope {

      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockProfileUiActions.initialize(any) returns serviceRight(Unit)
      mockUserProcess.getUser(any) returns serviceLeft(UserException(""))

      profileJobs.initialize().mustLeft[UserException]

      there was one(mockProfileUiActions).initialize(theme)
    }
  }

  "resume" should {
    "call to askBroadCastTask and return a valid response when the service returns a right response" in new ProfileJobsScope {

      override val profileJobs = new ProfileJobs(mockProfileUiActions)(contextWrapper) {
        override def askBroadCastTask(broadAction: BroadAction) = TaskService.empty
      }

      profileJobs.resume().mustRightUnit
    }

    "returns a JobException when call to askBroadCastTask and return an exception" in new ProfileJobsScope {

      override val profileJobs = new ProfileJobs(mockProfileUiActions)(contextWrapper) {
        override def sendBroadCastTask(broadAction: BroadAction) =
          TaskService.left(JobException(""))
      }

      profileJobs.resume().mustLeft[JobException]
    }
  }

  "stop" should {
    "returns a valid response when apiclient is disconnect" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      profileJobs.stop().mustRightUnit
    }

    "Do nothing when ApiClient is None" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = None)
      profileJobs.stop().mustRightUnit
    }

    "returns a JobException when the disconnect throws a exception" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.disconnect() throws new RuntimeException("")
      profileJobs.stop().mustLeft[JobException]
    }
  }

  "onOffsetChanged" should {
    "returns a valid response when the actions return a right responses" in new ProfileJobsScope {
      val percentage: Float = 1

      mockProfileUiActions.handleToolbarVisibility(any) returns serviceRight(Unit)
      mockProfileUiActions.handleProfileVisibility(any) returns serviceRight(Unit)

      profileJobs.onOffsetChanged(percentage).mustRightUnit

      there was one(mockProfileUiActions).handleToolbarVisibility(percentage)
      there was one(mockProfileUiActions).handleProfileVisibility(percentage)
    }
  }

  "accountSynced" should {
    "returns a valid response when the service returns a right response" in new ProfileJobsScope {

      mockTrackEventProcess.synchronizeConfiguration() returns serviceRight(Unit)

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.isConnected returns true
      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockCloudStorageProcess.getCloudStorageDevices(any)(any) returns serviceRight(Seq.empty)
      mockProfileUiActions.showEmptyAccountsContent(any) returns serviceRight(Unit)
      mockTrackEventProcess.showAccountsContent() returns serviceRight(Unit)
      mockProfileUiActions.showMessageAccountSynced() returns serviceRight(Unit)

      profileJobs.accountSynced().mustRightUnit

    }
  }

  "errorSyncing" should {
    "shows a message syncing error" in new ProfileJobsScope {

      mockProfileUiActions.showSyncingError() returns serviceRight(Unit)

      profileJobs.errorSyncing().mustRightUnit

      there was one(mockProfileUiActions).showSyncingError()
      profileJobs.syncEnabled shouldEqual true
    }
  }

  "stateSyncing" should {
    "updated the variable syncEnabled" in new ProfileJobsScope {

      profileJobs.stateSyncing().mustRightUnit
      profileJobs.syncEnabled shouldEqual false
    }
  }

  "saveSharedCollection" should {

    "returns a valid response and call to subscribe when the process returns a right" in new ProfileJobsScope {

      mockTrackEventProcess.addToMyCollectionsFromProfile(any) returns serviceRight(Unit)
      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)
      mockCollectionProcess.addCollection(any) returns serviceRight(collection)
      mockProfileUiActions.showAddCollectionMessage(any) returns serviceRight(Unit)
      mockSharedCollectionsProcess.subscribe(any)(any) returns serviceRight(Unit)

      profileJobs
        .saveSharedCollection(sharedCollection.copy(publicCollectionStatus = PublishedByOther))
        .mustRightUnit

      there was one(mockTrackEventProcess).addToMyCollectionsFromProfile(sharedCollection.name)
      there was one(mockProfileUiActions).showAddCollectionMessage(
        sharedCollection.sharedCollectionId)
      there was one(mockSharedCollectionsProcess).subscribe(
        ===(sharedCollection.sharedCollectionId))(any)
    }

    "doesn't call to subscribe when the status is PublishedByMe" in new ProfileJobsScope {

      mockTrackEventProcess.addToMyCollectionsFromProfile(any) returns serviceRight(Unit)
      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)
      mockCollectionProcess.addCollection(any) returns serviceRight(collection)
      mockProfileUiActions.showAddCollectionMessage(any) returns serviceRight(Unit)
      mockSharedCollectionsProcess.subscribe(any)(any) returns serviceRight(Unit)

      profileJobs
        .saveSharedCollection(sharedCollection.copy(publicCollectionStatus = PublishedByMe))
        .mustRightUnit

      there was one(mockTrackEventProcess).addToMyCollectionsFromProfile(sharedCollection.name)
      there was one(mockProfileUiActions).showAddCollectionMessage(
        sharedCollection.sharedCollectionId)
      there was no(mockSharedCollectionsProcess).subscribe(any)(any)
    }

    "returns an AppException when the process returns an exception" in new ProfileJobsScope {

      mockTrackEventProcess.addToMyCollectionsFromProfile(any) returns serviceRight(Unit)
      mockDeviceProcess.getSavedApps(any)(any) returns serviceLeft(AppException(""))

      profileJobs.saveSharedCollection(sharedCollection).mustLeft[AppException]

      there was one(mockTrackEventProcess).addToMyCollectionsFromProfile(sharedCollection.name)
      there was no(mockProfileUiActions).showAddCollectionMessage(
        sharedCollection.sharedCollectionId)
    }

    "returns an CollectionException when the process returns an exception" in new ProfileJobsScope {

      mockTrackEventProcess.addToMyCollectionsFromProfile(any) returns serviceRight(Unit)
      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)
      mockCollectionProcess.addCollection(any) returns serviceLeft(CollectionException(""))

      profileJobs.saveSharedCollection(sharedCollection).mustLeft[CollectionException]

      there was one(mockTrackEventProcess).addToMyCollectionsFromProfile(sharedCollection.name)
      there was no(mockProfileUiActions).showAddCollectionMessage(
        sharedCollection.sharedCollectionId)
    }
  }

  "shareCollection" should {
    "call to launchShare" in new ProfileJobsScope {

      mockTrackEventProcess.shareCollectionFromProfile(any) returns serviceRight(Unit)
      mockLauncherExecutorProcess.launchShare(any)(any) returns serviceRight(Unit)

      profileJobs.shareCollection(sharedCollection).mustRightUnit

    }
  }

  "loadPublications" should {
    "load publications when the service returns a right response" in new ProfileJobsScope {

      mockTrackEventProcess.showPublicationsContent() returns serviceRight(Unit)
      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getPublishedCollections()(any) returns serviceRight(
        seqSharedCollection)
      mockProfileUiActions.loadPublications(any) returns serviceRight(Unit)

      profileJobs.loadPublications().mustRightUnit

      there was one(mockProfileUiActions).loadPublications(seqSharedCollection)
    }

    "call to showEmptyPublicationsContent when doesn't have sharecollections." in new ProfileJobsScope {

      mockTrackEventProcess.showPublicationsContent() returns serviceRight(Unit)
      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getPublishedCollections()(any) returns serviceRight(Seq.empty)
      mockProfileUiActions.showEmptyPublicationsContent(any) returns serviceRight(Unit)

      profileJobs.loadPublications().mustRightUnit

      there was one(mockProfileUiActions).showEmptyPublicationsContent(false)
    }
  }

  "loadSubscriptions" should {
    "load subscription when the service returns a right response" in new ProfileJobsScope {

      mockTrackEventProcess.showSubscriptionsContent() returns serviceRight(Unit)
      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getSubscriptions()(any) returns serviceRight(seqSubscriptions)
      mockProfileUiActions.setSubscriptionsAdapter(any) returns serviceRight(Unit)

      profileJobs.loadSubscriptions().mustRightUnit

      there was one(mockProfileUiActions).setSubscriptionsAdapter(seqSubscriptions)
    }

    "call to showEmptySubscriptionsContent when doesn't have susbcription." in new ProfileJobsScope {

      mockTrackEventProcess.showSubscriptionsContent() returns serviceRight(Unit)
      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockSharedCollectionsProcess.getSubscriptions()(any) returns serviceRight(Seq.empty)
      mockProfileUiActions.showEmptySubscriptionsContent(any) returns serviceRight(Unit)

      profileJobs.loadSubscriptions().mustRightUnit

      there was one(mockProfileUiActions).showEmptySubscriptionsContent(false)
    }
  }

  "changeSubscriptionStatus" should {
    "change subscription status to Subscribe" in new ProfileJobsScope {

      mockTrackEventProcess.subscribeToCollection(any) returns serviceRight(Unit)
      mockSharedCollectionsProcess.subscribe(any)(any) returns serviceRight(Unit)
      mockTrackEventProcess.unsubscribeFromCollection(any) returns serviceRight(Unit)
      mockSharedCollectionsProcess.unsubscribe(any)(any) returns serviceRight(Unit)
      mockProfileUiActions.showUpdatedSubscriptions(any, any) returns serviceRight(Unit)

      profileJobs.changeSubscriptionStatus(sharedCollection.id, true).mustRightUnit

      there was one(mockProfileUiActions).showUpdatedSubscriptions(sharedCollection.id, true)

    }

    "change subscription status to Unsubscribe" in new ProfileJobsScope {

      mockTrackEventProcess.subscribeToCollection(any) returns serviceRight(Unit)
      mockSharedCollectionsProcess.subscribe(any)(any) returns serviceRight(Unit)
      mockTrackEventProcess.unsubscribeFromCollection(any) returns serviceRight(Unit)
      mockSharedCollectionsProcess.unsubscribe(any)(any) returns serviceRight(Unit)
      mockProfileUiActions.showUpdatedSubscriptions(any, any) returns serviceRight(Unit)

      profileJobs.changeSubscriptionStatus(sharedCollection.id, false).mustRightUnit

      there was one(mockProfileUiActions).showUpdatedSubscriptions(sharedCollection.id, false)

    }
  }

  "activityResult" should {
    "returns a JobException when the connection throws a exceptionp" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.connect() throws new RuntimeException("")

      profileJobs
        .activityResult(RequestCodes.resolveGooglePlayConnection, Activity.RESULT_OK, mockIntent)
        .mustLeft[JobException]
    }

    "try connect when the service returns a right response" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      profileJobs
        .activityResult(RequestCodes.resolveGooglePlayConnection, Activity.RESULT_OK, mockIntent)
        .mustRightUnit
    }

    "shows an empty accounts" in new ProfileJobsScope {

      mockProfileUiActions.showEmptyAccountsContent(any) returns serviceRight(Unit)
      profileJobs
        .activityResult(RequestCodes.resolveGooglePlayConnection, 2, mockIntent)
        .mustRightUnit
      there was one(mockProfileUiActions).showEmptyAccountsContent(true)
    }
  }

  "quit" should {
    "call to all process for delete and clean" in new ProfileJobsScope {

      mockTrackEventProcess.logout() returns serviceRight(Unit)
      mockCollectionProcess.cleanCollections() returns serviceRight(Unit)
      mockDeviceProcess.deleteAllDockApps() returns serviceRight(Unit)
      mockMomentProcess.deleteAllMoments() returns serviceRight(Unit)
      mockWidgetProcess.deleteAllWidgets() returns serviceRight(Unit)
      mockUserProcess.unregister(any) returns serviceRight(Unit)

      profileJobs.quit().mustRightUnit

    }
  }

  "launchService" should {
    "launch service when synEnable is true" in new ProfileJobsScope {

      mockProfileUiActions.showMessageSyncingAccount() returns serviceRight(Unit)

      profileJobs.launchService().mustRightUnit
      profileJobs.syncEnabled shouldEqual false
    }

    "Do nothing if syncEnable is false" in new ProfileJobsScope {

      mockProfileUiActions.showMessageSyncingAccount() returns serviceRight(Unit)

      profileJobs.syncEnabled = false
      profileJobs.launchService().mustRightUnit
    }
  }

  "deleteDevice" should {
    "return a valid response when the service returns a right response" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.isConnected returns true

      mockTrackEventProcess.showAccountsContent() returns serviceRight(Unit)
      mockCloudStorageProcess.getCloudStorageDevices(any)(any) returns serviceRight(
        Seq(cloudStorageDeviceSummary))
      mockProfileUiActions.showEmptyAccountsContent(any) returns serviceRight(Unit)

      mockTrackEventProcess.deleteConfiguration() returns serviceRight(Unit)
      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockCloudStorageProcess.deleteCloudStorageDevice(any, any) returns serviceRight(Unit)

      profileJobs.deleteDevice(cloudId).mustRightUnit

      there was two(mockProfileUiActions).showLoading()
    }

    "returns a valid response when the client don't have connection" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.isConnected returns false

      mockTrackEventProcess.showAccountsContent() returns serviceRight(Unit)
      mockCloudStorageProcess.getCloudStorageDevices(any)(===(contextSupport)) returns serviceRight(
        Seq(cloudStorageDeviceSummary))
      mockProfileUiActions.showEmptyAccountsContent(any) returns serviceRight(Unit)

      mockTrackEventProcess.deleteConfiguration() returns serviceRight(Unit)
      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockCloudStorageProcess.deleteCloudStorageDevice(any, any) returns serviceRight(Unit)

      profileJobs.deleteDevice(deviceCloudId).mustRightUnit

    }

    "returns a valid response when the user doesn't have email" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))

      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockUserProcess.getUser(any) returns serviceRight(user.copy(email = None))

      mockCloudStorageProcess.createCloudStorageClient(any)(any) returns serviceRight(
        mockApiClient)

      profileJobs.deleteDevice(deviceCloudId).mustRightUnit

    }

    "return a valid response when the service returns a right response" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = None)

      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockUserProcess.getUser(any) returns serviceRight(user)

      mockCloudStorageProcess.createCloudStorageClient(any)(any) returns serviceRight(
        mockApiClient)

      profileJobs.deleteDevice(deviceCloudId).mustRightUnit

    }
  }

  "printDeviceInfo" should {
    "return a valid response when the service returns a right response" in new ProfileJobsScope {

      val expected = RawCloudStorageDevice(
        cloudId = cloudId,
        uuid = driveServiceFile.summary.uuid,
        deviceId = driveServiceFile.summary.deviceId,
        title = driveServiceFile.summary.title,
        createdDate = driveServiceFile.summary.createdDate,
        modifiedDate = driveServiceFile.summary.modifiedDate,
        json = driveServiceFile.content)

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.isConnected returns true
      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockCloudStorageProcess.getRawCloudStorageDevice(any, any) returns serviceRight(expected)

      profileJobs.printDeviceInfo(deviceCloudId).mustRightUnit

    }
  }
}
