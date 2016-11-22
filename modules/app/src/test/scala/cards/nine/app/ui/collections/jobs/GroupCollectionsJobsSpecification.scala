package cards.nine.app.ui.collections.jobs

import cards.nine.app.di.Injector
import cards.nine.app.observers.ObserverRegister
import cards.nine.app.ui.collections.CollectionsDetailsActivity.statuses
import cards.nine.app.ui.collections.jobs.uiactions.{GroupCollectionsDOM, GroupCollectionsUiActions, NavigationUiActions, ToolbarUiActions}
import cards.nine.app.ui.commons.{RequestCodes, JobException, BroadAction, UiException}
import cards.nine.app.ui.launcher.jobs.LauncherTestData
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CollectionTestData
import cards.nine.commons.test.data.CommonValues._
import cards.nine.models.types.{ReadCallLog, PermissionResult, CallPhone}
import cards.nine.process.accounts.UserAccountsProcess
import cards.nine.process.collection.{CardException, CollectionException, CollectionProcess}
import cards.nine.process.device.DeviceProcess
import cards.nine.process.intents.LauncherExecutorProcess
import cards.nine.process.moment.MomentProcess
import cards.nine.process.theme.ThemeProcess
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.commons.test.data.CardValues._
import cards.nine.commons.test.data.CollectionValues._

trait GroupCollectionsJobsSpecification extends TaskServiceSpecification
  with Mockito {

  trait GroupCollectionsJobsScope
    extends Scope
      with CollectionTestData
      with LauncherTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockGroupCollectionsDOM = mock[GroupCollectionsDOM]

    val mockGroupCollectionsUiActions = mock[GroupCollectionsUiActions]

    mockGroupCollectionsUiActions.dom returns mockGroupCollectionsDOM

    val mockNavigationUiActions = mock[NavigationUiActions]

    val mockToolbarUiActions = mock[ToolbarUiActions]

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val mockThemeProcess = mock[ThemeProcess]

    mockInjector.themeProcess returns mockThemeProcess

    val mockObserverRegister = mock[ObserverRegister]

    mockInjector.observerRegister returns mockObserverRegister

    val mockUserAccountsProcess = mock[UserAccountsProcess]

    mockInjector.userAccountsProcess returns mockUserAccountsProcess

    val mockLauncherExecutorProcess = mock[LauncherExecutorProcess]

    mockInjector.launcherExecutorProcess returns mockLauncherExecutorProcess

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val groupCollectionsJobs = new GroupCollectionsJobs(mockGroupCollectionsUiActions, mockToolbarUiActions, mockNavigationUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def themeFile = ""

      override def sendBroadCastTask(broadAction: BroadAction) = TaskService.empty
    }

  }

}


class GroupCollectionsJobsSpec
  extends GroupCollectionsJobsSpecification {

  "initialize" should {
    "shows the collections when the service returns a right response" in new GroupCollectionsJobsScope {

      mockToolbarUiActions.initialize(any, any, any, any) returns serviceRight(Unit)
      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockGroupCollectionsUiActions.initialize() returns serviceRight(Unit)
      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockGroupCollectionsUiActions.showCollections(any, any) returns serviceRight(Unit)

      groupCollectionsJobs.initialize(backgroundColor, initialToolbarColor, icon, position, stateChanged).mustRightUnit

      there was one(mockToolbarUiActions).initialize(backgroundColor, initialToolbarColor, icon, stateChanged)
      there was one(mockGroupCollectionsUiActions).showCollections(seqCollection, position)

    }

    "return a CollectionException if the service throws an exception" in new GroupCollectionsJobsScope {

      mockToolbarUiActions.initialize(any, any, any, any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.initialize() returns serviceRight(Unit)
      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockCollectionProcess.getCollections returns serviceLeft(CollectionException(""))

      groupCollectionsJobs.initialize(backgroundColor, initialToolbarColor, icon, position, stateChanged).mustLeft[CollectionException]

      there was one(mockToolbarUiActions).initialize(backgroundColor, initialToolbarColor, icon, stateChanged)
      there was no(mockGroupCollectionsUiActions).showCollections(seqCollection, position)

    }
  }

  "resume" should {
    "calls to register Observer" in new GroupCollectionsJobsScope {

      mockObserverRegister.registerObserverTask() returns serviceRight(Unit)
      groupCollectionsJobs.resume().mustRightUnit
      there was one(mockObserverRegister).registerObserverTask()
    }
  }

  "pause" should {
    "calls to unregister Observer" in new GroupCollectionsJobsScope {

      mockObserverRegister.unregisterObserverTask() returns serviceRight(Unit)
      groupCollectionsJobs.pause().mustRightUnit
      there was one(mockObserverRegister).unregisterObserverTask()
    }
  }

  "back" should {
    "calls to back" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.back() returns serviceRight(Unit)
      groupCollectionsJobs.back().mustRightUnit
      there was one(mockGroupCollectionsUiActions).back()
    }
  }

  "destroy" should {
    "calls to destroy" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.destroy() returns serviceRight(Unit)
      groupCollectionsJobs.destroy().mustRightUnit
      there was one(mockGroupCollectionsUiActions).destroy()
    }
  }

  "reloadCards" should {
    "reloads cards when the service returns a right response " in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockGroupCollectionsUiActions.reloadCards(any) returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)

      groupCollectionsJobs.reloadCards() mustRight { r => r shouldEqual seqCard }

      there was one(mockCollectionProcess).getCollectionById(collection.id)
      there was one(mockGroupCollectionsUiActions).reloadCards(seqCard)
    }

    "reloads card the database when current the collection cards are different" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection.copy(cards = Seq(card(3), card(3), card(5)))))
      mockGroupCollectionsUiActions.reloadCards(any) returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)

      groupCollectionsJobs.reloadCards() mustRight { r => r shouldEqual Seq(card(3), card(3), card(5)) }

      there was one(mockCollectionProcess).getCollectionById(collection.id)
      there was one(mockGroupCollectionsUiActions).reloadCards(Seq(card(3), card(3), card(5)))
    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))
      groupCollectionsJobs.reloadCards().mustLeft[UiException]
      there was no(mockCollectionProcess).getCollectionById(any)
    }

    "return a CollectionException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.getCollectionById(any) returns serviceLeft(CollectionException(""))

      groupCollectionsJobs.reloadCards().mustLeft[CollectionException]

      there was one(mockGroupCollectionsUiActions).getCurrentCollection
      there was one(mockCollectionProcess).getCollectionById(collection.id)
    }
  }

  sequential
  "editCard" should {
    "call to edit card when the service returns a right response" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(positionsEditing = Set(1))
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockGroupCollectionsUiActions.editCard(any, any, any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.closeEditingModeUi() returns serviceRight(Unit)

      groupCollectionsJobs.editCard().mustRightUnit

    }
    "return a JobException because you only can edit one card" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(positionsEditing = Set(1, 2))
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      groupCollectionsJobs.editCard().mustLeft[JobException]
    }

    "return a JobException when the current positionsEditing of statuses is Set.empty" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      groupCollectionsJobs.editCard().mustLeft[JobException]
    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))
      groupCollectionsJobs.editCard().mustLeft[UiException]
    }
  }

  "removeCardsInEditMode" should {
    "call to remove cards when the service returns a right response" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(positionsEditing = Set(0, 1, 2))
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockGroupCollectionsUiActions.closeEditingModeUi() returns serviceRight(Unit)
      mockTrackEventProcess.removeApplications(any) returns serviceRight(Unit)
      mockCollectionProcess.deleteCards(any, any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.removeCards(any) returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)

      groupCollectionsJobs.removeCardsInEditMode() mustRight {
        _ shouldEqual Seq(card(0), card(1), card(2))
      }

      there was one(mockGroupCollectionsUiActions).getCurrentCollection
      there was one(mockTrackEventProcess).removeApplications(cardPackageSeq)
      there was one(mockCollectionProcess).deleteCards(collection.id, cardIdSeq)
      there was one(mockGroupCollectionsUiActions).removeCards(seqCard)
      there was one(mockMomentProcess).getMoments
    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))
      groupCollectionsJobs.removeCardsInEditMode().mustLeft[UiException]
      there was one(mockGroupCollectionsUiActions).getCurrentCollection
    }
  }

  "removeCardsByPackagesName" should {
    "remove cards by packages name when the service right response" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.removeAppsByFab(any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockTrackEventProcess.removeApplications(any) returns serviceRight(Unit)
      mockCollectionProcess.deleteCards(any, any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.removeCards(any) returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)

      groupCollectionsJobs.removeCardsByPackagesName(cardPackageSeq) mustRight (_ shouldEqual seqCard)

      there was one(mockTrackEventProcess).removeAppsByFab(cardPackageSeq)
      there was one(mockGroupCollectionsUiActions).getCurrentCollection
      there was one(mockTrackEventProcess).removeApplications(cardPackageSeq)
      there was one(mockCollectionProcess).deleteCards(collection.id, cardIdSeq)
      there was one(mockGroupCollectionsUiActions).removeCards(seqCard)
      there was one(mockMomentProcess).getMoments
    }

    "Do nothing when not found cards by packages name, cards is Seq.empty" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.removeAppsByFab(any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockTrackEventProcess.removeApplications(any) returns serviceRight(Unit)
      mockCollectionProcess.deleteCards(any, any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.removeCards(any) returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)

      groupCollectionsJobs.removeCardsByPackagesName(Seq.empty) mustRight (_ shouldEqual Seq.empty)

      there was one(mockTrackEventProcess).removeAppsByFab(Seq.empty)
      there was one(mockGroupCollectionsUiActions).getCurrentCollection
      there was one(mockTrackEventProcess).removeApplications(Seq.empty)
      there was one(mockCollectionProcess).deleteCards(collection.id, Seq.empty)
      there was one(mockGroupCollectionsUiActions).removeCards(Seq.empty)
      there was one(mockMomentProcess).getMoments
    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.removeAppsByFab(any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))
      groupCollectionsJobs.removeCardsByPackagesName(cardPackageSeq).mustLeft[UiException]
      there was one(mockGroupCollectionsUiActions).getCurrentCollection
    }
  }

  "removeCards" should {
    "remove cards when the services returns right response" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.removeApplications(any) returns serviceRight(Unit)
      mockCollectionProcess.deleteCards(any, any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.removeCards(any) returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)

      groupCollectionsJobs.removeCards(collection.id, seqCard) mustRight (_ shouldEqual seqCard)

      there was one(mockTrackEventProcess).removeApplications(cardPackageSeq)
      there was one(mockCollectionProcess).deleteCards(collection.id, cardIdSeq)
      there was one(mockGroupCollectionsUiActions).removeCards(seqCard)
      there was one(mockMomentProcess).getMoments
    }

    "return a CardException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.removeApplications(any) returns serviceRight(Unit)
      mockCollectionProcess.deleteCards(any, any) returns serviceLeft(CardException(""))

      groupCollectionsJobs.removeCards(collection.id, seqCard).mustLeft[CardException]

      there was one(mockTrackEventProcess).removeApplications(cardPackageSeq)
      there was one(mockCollectionProcess).deleteCards(collection.id, cardIdSeq)
      there was no(mockGroupCollectionsUiActions).removeCards(any)
      there was no(mockMomentProcess).getMoments
    }
  }

  "moveToCollection" should {
    "move to collection when the service returns right response" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(positionsEditing = Set(0, 1, 2))
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockTrackEventProcess.moveApplications(any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCollection(any) returns serviceRight(Option(collection))
      mockGroupCollectionsUiActions.closeEditingModeUi() returns serviceRight(Unit)
      mockCollectionProcess.deleteCards(any, any) returns serviceRight(Unit)
      mockCollectionProcess.addCards(any, any) returns serviceRight(seqCard)
      mockGroupCollectionsUiActions.removeCards(any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.addCardsToCollection(any, any) returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)

      groupCollectionsJobs.moveToCollection(collection.id, collection.position) mustRight (_ shouldEqual seqCard)

      there was one(mockGroupCollectionsUiActions).getCurrentCollection
      there was one(mockTrackEventProcess).moveApplications(collection.name)
      there was one(mockGroupCollectionsUiActions).getCollection(position)
      there was one(mockGroupCollectionsUiActions).closeEditingModeUi()
      there was one(mockCollectionProcess).deleteCards(collection.id, cardIdSeq)
      there was one(mockCollectionProcess).addCards(collection.id, seqCardData)
      there was one(mockGroupCollectionsUiActions).removeCards(seqCard)
      there was one(mockGroupCollectionsUiActions).addCardsToCollection(collection.position, seqCard)
      there was one(mockMomentProcess).getMoments
    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(positionsEditing = Set(0, 1, 2))
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))

      groupCollectionsJobs.moveToCollection(collection.id, collection.position).mustLeft[UiException]

      there was one(mockGroupCollectionsUiActions).getCurrentCollection
    }
  }

  "savePublishStatus" should {
    "save publish status the current collection" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))

      groupCollectionsJobs.savePublishStatus().mustRightUnit

      statuses.publishStatus shouldEqual collection.publicCollectionStatus
    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))
      groupCollectionsJobs.savePublishStatus().mustLeft[UiException]
      there was no(mockCollectionProcess).getCollectionById(any)
    }
  }

  sequential
  "performCard" should {
    "call to launcher executor when collectionMode is NormalCollection" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(collectionMode = NormalCollectionMode)
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)
      mockGroupCollectionsDOM.getCurrentCollection returns Option(collection)
      mockTrackEventProcess.openAppFromCollection(any, any) returns serviceRight(Unit)
      mockMomentProcess.getMomentByCollectionId(any) returns serviceRight(Option(moment))

      groupCollectionsJobs.performCard(card, card.position).mustRightUnit

      there was one(mockGroupCollectionsDOM).getCurrentCollection
      there was two(mockTrackEventProcess).openAppFromCollection(===(card.packageName.getOrElse("")), any)
      there was one(mockMomentProcess).getMomentByCollectionId(collection.id)
    }

    "call to launcher executor when collectionMode is NormalCollection and don't track  if doesn't have a moment." in new GroupCollectionsJobsScope {

      statuses = statuses.copy(collectionMode = NormalCollectionMode)
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)
      mockGroupCollectionsDOM.getCurrentCollection returns Option(collection)
      mockTrackEventProcess.openAppFromCollection(any, any) returns serviceRight(Unit)
      mockMomentProcess.getMomentByCollectionId(any) returns serviceRight(None)

      groupCollectionsJobs.performCard(card.copy(packageName = None), card.position).mustRightUnit

      there was one(mockGroupCollectionsDOM).getCurrentCollection
      there was one(mockTrackEventProcess).openAppFromCollection(===(""), any)
      there was one(mockMomentProcess).getMomentByCollectionId(collection.id)
    }

    "call to launcher executor when collectionMode is NormalCollection" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(collectionMode = NormalCollectionMode)
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)
      mockGroupCollectionsDOM.getCurrentCollection returns Option(collection)
      mockTrackEventProcess.openAppFromCollection(any, any) returns serviceRight(Unit)
      mockMomentProcess.getMomentByCollectionId(any) returns serviceRight(Option(moment))

      groupCollectionsJobs.performCard(card, card.position).mustRightUnit

      there was one(mockGroupCollectionsDOM).getCurrentCollection
      there was two(mockTrackEventProcess).openAppFromCollection(===(card.packageName.getOrElse("")), any)
      there was one(mockMomentProcess).getMomentByCollectionId(collection.id)
    }

    "call to launcher executor when collectionMode is NormalCollection" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(collectionMode = NormalCollectionMode)
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)
      mockGroupCollectionsDOM.getCurrentCollection returns None

      groupCollectionsJobs.performCard(card, card.position).mustRightUnit

      there was one(mockGroupCollectionsDOM).getCurrentCollection
      there was no(mockTrackEventProcess).openAppFromCollection(===(card.packageName.getOrElse("")), any)
      there was no(mockMomentProcess).getMomentByCollectionId(any)
    }

    "call to reloadItemCollection when collectionMode is EditingCollectionMode" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(collectionMode = EditingCollectionMode, positionsEditing = Set(0, 1, 2))
      mockGroupCollectionsUiActions.reloadItemCollection(any, any) returns serviceRight(Unit)

      groupCollectionsJobs.performCard(card, card.position).mustRightUnit
      there was one(mockGroupCollectionsUiActions).reloadItemCollection(2, card.position)

    }

    "call to closeEditingModeUi when collectionMode is EditingCollectionMode and positions editing are empty" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(collectionMode = EditingCollectionMode, positionsEditing = Set(1))
      mockGroupCollectionsUiActions.closeEditingModeUi() returns serviceRight(Unit)

      groupCollectionsJobs.performCard(card, card.position).mustRightUnit

      there was one(mockGroupCollectionsUiActions).closeEditingModeUi
    }
  }

  "requestCallPhonePermission" should {
    "call to requestPermissions" in new GroupCollectionsJobsScope {

      mockUserAccountsProcess.requestPermission(any, any)(any) returns serviceRight(Unit)
      groupCollectionsJobs.requestCallPhonePermission(Option(numberPhone)).mustRightUnit
      there was one(mockUserAccountsProcess).requestPermission(===(RequestCodes.phoneCallPermission), ===(CallPhone))(any)
    }
  }

  "requestPermissionsResult" should {

    "Do nothing if requestCode is differnt phoneCallPermission" in new GroupCollectionsJobsScope {

      groupCollectionsJobs.requestPermissionsResult(RequestCodes.callLogPermission,Array(ReadCallLog.value), Array.empty).mustRightUnit
    }

    "call to launcherExecutorProcess for the specified permissions: phoneCallPermission " in new GroupCollectionsJobsScope {

      mockUserAccountsProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(CallPhone, result = true)))
      statuses = statuses.copy(lastPhone = Option(lastPhone))
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)

      groupCollectionsJobs.requestPermissionsResult(RequestCodes.phoneCallPermission,Array(CallPhone.value), Array.empty).mustRightUnit
    }

    "Do nothing for the specified permissions :phoneCallPermission if hasn't lastPhone" in new GroupCollectionsJobsScope {

      mockUserAccountsProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(CallPhone, result = true)))
      statuses = statuses.copy(lastPhone = None)
      groupCollectionsJobs.requestPermissionsResult(RequestCodes.phoneCallPermission,Array(CallPhone.value), Array.empty).mustRightUnit
    }

    "Show a message error if haven't permissions phoneCallPermission " in new GroupCollectionsJobsScope {

      mockUserAccountsProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(CallPhone, result = false)))
      statuses = statuses.copy(lastPhone = Option(lastPhone))
      mockLauncherExecutorProcess.launchDial(any)(any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.showNoPhoneCallPermissionError() returns serviceRight(Unit)

      groupCollectionsJobs.requestPermissionsResult(RequestCodes.phoneCallPermission,Array(CallPhone.value), Array.empty).mustRightUnit
    }

    "Do nothing for the specified permissions :phoneCallPermission if hasn't lastPhone " in new GroupCollectionsJobsScope {

      mockUserAccountsProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(CallPhone, result = false)))
      statuses = statuses.copy(lastPhone = None)

      groupCollectionsJobs.requestPermissionsResult(RequestCodes.phoneCallPermission,Array(CallPhone.value), Array.empty).mustRightUnit
    }
  }

  "addCards" should {
    "added cards when the service retursn right response" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.addAppsByFab(any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.addCards(any, any) returns serviceRight(seqCard)
      mockGroupCollectionsUiActions.addCards(any) returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)

      groupCollectionsJobs.addCards(seqCardData) mustRight (_ shouldEqual seqCard)

      there was one(mockTrackEventProcess).addAppsByFab(cardDataPackageSeq)
      there was one(mockGroupCollectionsUiActions).getCurrentCollection
      there was one(mockGroupCollectionsUiActions).addCards(seqCard)
      there was one(mockCollectionProcess).addCards(collection.id, seqCardData)
    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.addAppsByFab(any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))

      groupCollectionsJobs.addCards(seqCardData).mustLeft[UiException]

      there was one(mockTrackEventProcess).addAppsByFab(cardDataPackageSeq)
      there was one(mockGroupCollectionsUiActions).getCurrentCollection
    }
  }

  "addShortcut" should {
    "added shortcut when the service retursn right response" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.addShortcutByFab(any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockDeviceProcess.saveShortcutIcon(any,any)(any) returns serviceRight(cardImagePath)
      mockCollectionProcess.addCards(any,any) returns serviceRight(seqCard)
      mockGroupCollectionsUiActions.addCards(any) returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)

      groupCollectionsJobs.addShortcut(term, jsonToNineCardIntent(intent), None) mustRight (_ shouldEqual seqCard)

      there was one(mockTrackEventProcess).addShortcutByFab(term)
      there was one(mockGroupCollectionsUiActions).getCurrentCollection
      there was one(mockGroupCollectionsUiActions).addCards(seqCard)
    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.addShortcutByFab(any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))

      groupCollectionsJobs.addShortcut(term, jsonToNineCardIntent(intent), None).mustLeft[UiException]

      there was one(mockTrackEventProcess).addShortcutByFab(term)
      there was one(mockGroupCollectionsUiActions).getCurrentCollection
    }
  }

  sequential
  "openReorderMode" should {
    "call to closeEditingMode and openReorderMode when statuses mode is EditingCollectionMode" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(collectionMode = EditingCollectionMode)
      mockGroupCollectionsUiActions.closeEditingModeUi() returns serviceRight(Unit)
      mockGroupCollectionsUiActions.openReorderModeUi() returns serviceRight(Unit)

      groupCollectionsJobs.openReorderMode().mustRightUnit

      there was one(mockGroupCollectionsUiActions).closeEditingModeUi()
      there was one(mockGroupCollectionsUiActions).openReorderModeUi()
    }

    "call to openReorderMode and modified the statuses to EditingCollectionMode when statuses mode is NormalCollectionMode" in new GroupCollectionsJobsScope {

      statuses = statuses.copy(collectionMode = NormalCollectionMode)
      mockGroupCollectionsUiActions.openReorderModeUi() returns serviceRight(Unit)

      groupCollectionsJobs.openReorderMode().mustRightUnit

      there was no(mockGroupCollectionsUiActions).closeEditingModeUi()
      there was one(mockGroupCollectionsUiActions).openReorderModeUi()
    }
  }

  "closeReorderMode" should {
    "call to closeReorderMode" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.startEditing(any) returns serviceRight(Unit)
      groupCollectionsJobs.closeReorderMode(position).mustRightUnit
      there was one(mockGroupCollectionsUiActions).startEditing(position)
    }
  }

  "closeEditingMode" should {
    "call to closeEditingMode" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.closeEditingModeUi() returns serviceRight(Unit)
      groupCollectionsJobs.closeEditingMode().mustRightUnit
      there was one(mockGroupCollectionsUiActions).closeEditingModeUi()
    }
  }

  "emptyCollection" should {
    "shows menu when the service returns a right response" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockGroupCollectionsUiActions.showMenu(any, any, any) returns serviceRight(Unit)

      groupCollectionsJobs.emptyCollection.mustRightUnit

      there was one(mockGroupCollectionsUiActions).showMenu(false, false, collection.themedColorIndex)
    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))
      groupCollectionsJobs.emptyCollection.mustLeft[UiException]
      there was no(mockGroupCollectionsUiActions).showMenu(any, any, any)
    }
  }

  "firstItemInCollection" should {
    "call to hide menu button" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.hideMenuButton() returns serviceRight(Unit)
      groupCollectionsJobs.firstItemInCollection().mustRightUnit
      there was one(mockGroupCollectionsUiActions).hideMenuButton()
    }
  }

  "close" should {
    "call action close" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.closeCollectionByGesture() returns serviceRight(Unit)
      mockGroupCollectionsUiActions.close() returns serviceRight(Unit)
      groupCollectionsJobs.close().mustRightUnit
    }
  }

  "showMenu" should {
    "shows menu when the service returns a right response" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.addCardByMenu() returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockGroupCollectionsUiActions.showMenu(any, any, any) returns serviceRight(Unit)

      groupCollectionsJobs.showMenu(true).mustRightUnit

      there was one(mockGroupCollectionsUiActions).showMenu(true, true, collection.themedColorIndex)
    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.addCardByMenu() returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))

      groupCollectionsJobs.showMenu(true).mustLeft[UiException]

      there was no(mockGroupCollectionsUiActions).showMenu(any, any, any)
    }
  }
}