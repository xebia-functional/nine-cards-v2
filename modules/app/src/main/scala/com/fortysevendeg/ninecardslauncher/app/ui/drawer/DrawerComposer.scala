package com.fortysevendeg.ninecardslauncher.app.ui.drawer

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.{View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.HeaderedItemAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts.ContactsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.header.HeaderGenerator
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.drawer.DrawerSnails._
import com.fortysevendeg.ninecardslauncher.process.device.{GetByInstallDate, GetAppOrder}
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, Contact}
import com.fortysevendeg.ninecardslauncher.process.theme.models.{SearchBackgroundColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

trait DrawerComposer
  extends DrawerStyles
  with ContextSupportProvider
  with HeaderGenerator {

  self: AppCompatActivity with TypedFindView with SystemBarsTint =>

  lazy val emptyAdapter = new RecyclerView.Adapter[RecyclerView.ViewHolder]() {

    override def getItemCount: Int = 0

    override def onBindViewHolder(vh: ViewHolder, i: Int): Unit = {}

    override def onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder = new RecyclerView.ViewHolder(viewGroup) {}
  }

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val drawerContent = Option(findView(TR.launcher_drawer_content))

  lazy val loadingDrawer = Option(findView(TR.launcher_drawer_loading))

  lazy val scrollerLayout = findView(TR.launcher_drawer_scroller_layout)

  lazy val recycler = Option(findView(TR.launcher_drawer_recycler))

  lazy val searchBoxPanel = Option(findView(TR.launcher_search_box_panel))

  lazy val  searchBoxIcon = Option(findView(TR.launcher_search_box_icon))

  def showDrawerLoading: Ui[_] = (loadingDrawer <~ vVisible) ~
    (recycler <~ vGone) ~
    (scrollerLayout <~ fslInvisible)

  def showDrawerData: Ui[_] = (loadingDrawer <~ vGone) ~
    (recycler <~ vVisible) ~
    (scrollerLayout <~ fslVisible)

  def showGeneralError: Ui[_] = drawerContent <~ uiSnackbarShort(R.string.contactUsError)

  def initDrawerUi(
    onAppMenuClickListener: (AppsMenuOption) => Unit,
    onContactMenuClickListener: (ContactsMenuOption) => Unit)(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (searchBoxPanel <~ searchBoxContentStyle) ~
      (searchBoxIcon <~ searchBoxButtonStyle) ~
      (appDrawerMain <~ appDrawerMainStyle <~ On.click {
        revealInDrawer ~ Ui(onAppMenuClickListener(AppsAlphabetical))
      }) ~
      (loadingDrawer <~ loadingDrawerStyle) ~
      (recycler <~ recyclerStyle) ~
      (scrollerLayout <~ drawerContentStyle) ~
      (drawerContent <~ vGone)

  def isDrawerVisible = drawerContent exists (_.getVisibility == View.VISIBLE)

  def revealInDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    (drawerContent <~ colorContentDialog(paint = true)) ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealInAppDrawer(source)))

  def revealOutDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    (drawerContent <~ colorContentDialog(paint = true)) ~
      (recycler <~
        rvAdapter(emptyAdapter)) ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealOutAppDrawer(source)))

  def addApps(apps: Seq[App], getAppOrder: GetAppOrder, clickListener: (App) => Unit, longClickListener: (App) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] =
    swipeAdapter(new AppsAdapter(
      initialSeq = generateHeaderList(apps, getAppOrder),
      clickListener = clickListener,
      longClickListener = Option(longClickListener)),
      fastScrollerVisible = isScrollerLayoutVisible(getAppOrder))

  private[this] def colorContentDialog(paint: Boolean)(implicit context: ActivityContextWrapper) =
    vBackgroundColorResource(if (paint) R.color.background_dialog else android.R.color.transparent)

  private[this] def isScrollerLayoutVisible(getAppOrder: GetAppOrder) = getAppOrder match {
    case v: GetByInstallDate => false
    case _ => true
  }

  def addContacts(contacts: Seq[Contact], clickListener: (Contact) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] =
    swipeAdapter(new ContactsAdapter(
      initialSeq = generateContactsForList(contacts),
      clickListener = clickListener,
      longClickListener = None))

  private[this] def swipeAdapter(
    adapter: HeaderedItemAdapter[_],
    fastScrollerVisible: Boolean = true) =
    showDrawerData ~
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter) <~
        rvScrollToTop) ~
      scrollerLayoutUi(fastScrollerVisible)

  def scrollerLayoutUi(fastScrollerVisible: Boolean): Ui[_] =
    if (fastScrollerVisible) {
      scrollerLayout <~ fslVisible <~ fslLinkRecycler <~ fslReset
    } else {
      scrollerLayout <~ fslInvisible
    }


}
