package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{ImplicitsTrackEventException, TrackEventException, TrackEventProcess}

trait HomeTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  override def openCollectionTitle(collectionName: String) = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceCategory,
      action = OpenCollectionTitleAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def openCollectionOrder(position: Int) = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceCategory,
      action = OpenCollectionOrderAction,
      label = Option(position.toString),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def deleteCollection(collectionName: String) = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceDragAndDropCategory,
      action = DeleteCollectionAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def reorderCollection() = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceCategory,
      action = ReorderCollectionAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def usingSearchByKeyboard() = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = SearchButtonsCategory,
      action = UsingSearchByKeyboardAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def usingSearchByVoice() = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = SearchButtonsCategory,
      action = UsingSearchByVoiceAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def createNewCollection() = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceActionsCategory,
      action = CreateNewCollectionAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def editCollection(collectionName: String) = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceActionsCategory,
      action = EditCollectionAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def openMyCollections() = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceActionsCategory,
      action = OpenMyCollectionsAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def openPublicCollections() = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceActionsCategory,
      action = OpenPublicCollectionsAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def createNewCollectionFromMyCollection(collectionName: String) = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceActionsCategory,
      action = CreateNewCollectionFromMyCollectionAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def createNewCollectionFromPublicCollection(collectionName: String) = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceActionsCategory,
      action = CreateNewCollectionFromPublicCollectionAction,
      label = Option(collectionName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def openDockAppTitle(packageName: String) = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceBottomActionsCategory,
      action = OpenDockAppTitleAction,
      label = Option(packageName),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def openDockAppOrder(position: Int) = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceBottomActionsCategory,
      action = OpenDockAppOrderAction,
      label = Option(position.toString),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def goToAppDrawer() = {
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceBottomActionsCategory,
      action = GoToAppDrawerAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def openLinkReceived(supported: Boolean) = {
    val supportedLabel = "Supported"
    val notSupportedLabel = "Not Supported"
    val label = if (supported) supportedLabel else notSupportedLabel
    val event = TrackEvent(
      screen = HomeScreen,
      category = WorkSpaceBottomActionsCategory,
      action = GoToAppDrawerAction,
      label = Option(label),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
