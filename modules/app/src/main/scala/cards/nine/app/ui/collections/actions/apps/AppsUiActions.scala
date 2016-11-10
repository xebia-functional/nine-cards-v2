package cards.nine.app.ui.collections.actions.apps

import android.view.View
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.actions.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.adapters.apps.AppsSelectionAdapter
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.styles.CommonStyles
import cards.nine.app.ui.components.commons.SelectedItemDecoration
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import cards.nine.app.ui.preferences.commons.AppDrawerSelectItemsInScroller
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.theme.{DrawerBackgroundColor, DrawerTabsBackgroundColor}
import cards.nine.models.{ApplicationData, TermCounter}
import cards.nine.process.device.models.IterableApps
import macroid.extras.DeviceVersion.Lollipop
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

trait AppsUiActions
  extends AppNineCardsIntentConversions
  with Styles
  with CommonStyles {

  self: BaseActionFragment with AppsDOM with AppsUiListener =>

  val resistance = 2.4f

  def initialize(selectedAppsSeq: Set[String]): TaskService[Unit] = {
    val selectItemsInScrolling = AppDrawerSelectItemsInScroller.readValue
    ((scrollerLayout <~ scrollableStyle(colorPrimary)) ~
      (toolbar <~
        dtbInit(colorPrimary) <~
        dtbChangeText(R.string.allApps) <~
        dtbNavigationOnClickListener((_) => unreveal())) ~
      (fab <~
        fabButtonMenuStyle(colorPrimary) <~
        On.click(Ui(updateCollectionApps()))) ~
      (selectedAppsContent <~
        selectedAppsStyle <~
        vBackgroundColor(theme.get(DrawerBackgroundColor))) ~
      (selectedApps <~
        subtitleTextStyle <~
        vBackgroundColor(theme.get(DrawerTabsBackgroundColor)) <~
        tvText(resGetString(R.string.selectedApps, selectedAppsSeq.size.toString))) ~
      (recycler <~ recyclerStyle <~
        (if (selectItemsInScrolling) rvAddItemDecoration(new SelectedItemDecoration) else Tweak.blank))).toService
  }

  def showLoading(): TaskService[Unit] = ((loading <~ vVisible) ~ (recycler <~ vGone)).toService

  def showError(): TaskService[Unit] = showGeneralError.toService

  def destroy(): TaskService[Unit] = Ui {
    getAdapter foreach(_.close())
  }.toService

  def showErrorLoadingAppsInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.errorLoadingApps, error = true, loadApps()).toService

  def showApps(apps: IterableApps, counters: Seq[TermCounter]): TaskService[Unit] =
    generateAppsSelectionAdapter(apps, counters, updateSelectedApps).toService

  def showUpdateSelectedApps(packages: Set[String]): TaskService[Unit] =
    (Ui(getAdapter foreach (_.notifyDataSetChanged())) ~
      (selectedApps <~
        tvText(resGetString(R.string.selectedApps, packages.size.toString)))).toService

  def close(): TaskService[Unit] = unreveal().toService

  private[this] def showData: Ui[_] = (loading <~ vGone) ~ (recycler <~ vVisible)

  private[this] def showGeneralError: Ui[_] = rootContent <~ vSnackbarShort(R.string.contactUsError)

  private[this] def generateAppsSelectionAdapter(
    apps: IterableApps,
    counters: Seq[TermCounter],
    clickListener: (ApplicationData) => Unit) = {
    val adapter = AppsSelectionAdapter(
      apps = apps,
      clickListener = clickListener)
    showData ~
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter)) ~
      (scrollerLayout <~ fslLinkRecycler(recycler) <~ fslCounters(counters))
  }

  private[this] def selectedAppsStyle: Tweak[View] = Lollipop ifSupportedThen {
    vElevation(resGetDimension(R.dimen.elevation_toolbar))
  } getOrElse Tweak.blank

}