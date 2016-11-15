package cards.nine.commons.test.data.trackevent

import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.models.TrackEvent
import cards.nine.models.types._

trait HomeTrackEventTestData {

  val openCollectionTitleEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceCategory,
    action = OpenCollectionTitleAction,
    label = Option(collectionName),
    value = None)

  val openCollectionOrderEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceCategory,
    action = OpenCollectionOrderAction,
    label = Option(position.toString),
    value = None)

  val deleteCollectionEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceDragAndDropCategory,
    action = DeleteCollectionAction,
    label = Option(collectionName),
    value = None)

  val reorderCollectionEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceCategory,
    action = ReorderCollectionAction,
    label = None,
    value = None)

  val usingSearchByKeyboardEvent = TrackEvent(
    screen = HomeScreen,
    category = SearchButtonsCategory,
    action = UsingSearchByKeyboardAction,
    label = None,
    value = None)

  val usingSearchByVoiceEvent = TrackEvent(
    screen = HomeScreen,
    category = SearchButtonsCategory,
    action = UsingSearchByVoiceAction,
    label = None,
    value = None)

  val createNewCollectionEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceActionsCategory,
    action = CreateNewCollectionAction,
    label = None,
    value = None)

  val editCollectionEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceActionsCategory,
    action = EditCollectionAction,
    label = Option(collectionName),
    value = None)

  val openMyCollectionsEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceActionsCategory,
    action = OpenMyCollectionsAction,
    label = None,
    value = None)

  val openPublicCollectionsEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceActionsCategory,
    action = OpenPublicCollectionsAction,
    label = None,
    value = None)

  val createNewCollectionFromMyCollectionEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceActionsCategory,
    action = CreateNewCollectionFromMyCollectionAction,
    label = Option(collectionName),
    value = None)

  val createNewCollectionFromPublicCollectionEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceActionsCategory,
    action = CreateNewCollectionFromPublicCollectionAction,
    label = Option(collectionName),
    value = None)

  val openDockAppTitleEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceBottomActionsCategory,
    action = OpenDockAppTitleAction,
    label = Option(packageName),
    value = None)

  val openDockAppOrderEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceBottomActionsCategory,
    action = OpenDockAppOrderAction,
    label = Option(position.toString),
    value = None)

  val goToAppDrawerEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceBottomActionsCategory,
    action = GoToAppDrawerAction,
    label = None,
    value = None)

  val openLinkReceivedEvent = TrackEvent(
    screen = HomeScreen,
    category = WorkSpaceBottomActionsCategory,
    action = GoToAppDrawerAction,
    label = Option(supportedStr),
    value = None)

}
