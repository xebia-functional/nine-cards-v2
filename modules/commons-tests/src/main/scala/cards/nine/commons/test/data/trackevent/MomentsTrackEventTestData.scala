package cards.nine.commons.test.data.trackevent

import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.models.TrackEvent
import cards.nine.models.types._

trait MomentsTrackEventTestData {

  val goToApplicationByMomentEvent = TrackEvent(
    screen = MomentsScreen,
    category = IconBarCategory,
    action = GoToApplicationByMomentAction,
    label = Option(momentName),
    value = None)

  val editMomentEvent = TrackEvent(
    screen = MomentsScreen,
    category = WorkSpaceActionsCategory,
    action = EditMomentAction,
    label = Option(momentName),
    value = None)

  val changeMomentEvent = TrackEvent(
    screen = MomentsScreen,
    category = WorkSpaceActionsCategory,
    action = ChangeMomentAction,
    label = Option(momentName),
    value = None)

  val addMomentEvent = TrackEvent(
    screen = MomentsScreen,
    category = WorkSpaceActionsCategory,
    action = AddMomentAction,
    label = Option(momentName),
    value = None)

  val addWidgetEvent = TrackEvent(
    screen = MomentsScreen,
    category = WorkSpaceActionsCategory,
    action = AddWidgetAction,
    label = Option(widgetName),
    value = None)

  val unpinMomentEvent = TrackEvent(
    screen = MomentsScreen,
    category = TopBarCategory,
    action = UnpinMomentAction,
    label = None,
    value = None)

  val goToWeatherEvent = TrackEvent(
    screen = MomentsScreen,
    category = TopBarCategory,
    action = GoToWeatherAction,
    label = None,
    value = None)

  val goToGoogleSearchEvent = TrackEvent(
    screen = MomentsScreen,
    category = TopBarCategory,
    action = GoToGoogleSearchAction,
    label = None,
    value = None)

  val quickAccessToCollectionEvent = TrackEvent(
    screen = MomentsScreen,
    category = EditMomentCategory,
    action = QuickAccessToCollectionAction,
    label = None,
    value = None)

  val setHoursEvent = TrackEvent(
    screen = MomentsScreen,
    category = EditMomentCategory,
    action = SetHoursAction,
    label = None,
    value = None)

  val setWifiEvent = TrackEvent(
    screen = MomentsScreen,
    category = EditMomentCategory,
    action = SetWifiAction,
    label = None,
    value = None)

  val chooseMomentEvent = TrackEvent(
    screen = MomentsScreen,
    category = MomentsMenuCategory,
    action = ChooseMomentAction,
    label = Option(momentName),
    value = None)

  val editMomentByMenuEvent = TrackEvent(
    screen = MomentsScreen,
    category = MomentsMenuCategory,
    action = EditMomentAction,
    label = Option(momentName),
    value = None)

  val deleteMomentEvent = TrackEvent(
    screen = MomentsScreen,
    category = MomentsMenuCategory,
    action = DeleteMomentAction,
    label = None,
    value = None)

}
