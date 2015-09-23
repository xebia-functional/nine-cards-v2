package com.fortysevendeg.ninecardslauncher.app.ui.drawer

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.HeaderedItemAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts.ContactsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.AppHeadered._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.ContactHeadered._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.TextTab._
import com.fortysevendeg.ninecardslauncher.app.ui.drawer.DrawerSnails._
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, AppCategorized}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

trait DrawerComposer
  extends DrawerStyles
  with ContextSupportProvider {

  self: AppCompatActivity with TypedFindView with SystemBarsTint =>

  lazy val emptyAdapter = new RecyclerView.Adapter[RecyclerView.ViewHolder]() {

    override def getItemCount: Int = 0

    override def onBindViewHolder(vh: ViewHolder, i: Int): Unit = {}

    override def onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder = new RecyclerView.ViewHolder(viewGroup) {}
  }

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val drawerContent = Option(findView(TR.launcher_drawer_content))

  lazy val drawerTabApp = Option(findView(TR.launcher_drawer_tab_app))

  lazy val drawerTabContacts = Option(findView(TR.launcher_drawer_tab_contact))

  lazy val loadingDrawer = Option(findView(TR.launcher_drawer_loading))

  lazy val scrollerLayout = findView(TR.launcher_drawer_scroller_layout)

  lazy val recycler = Option(findView(TR.launcher_drawer_recycler))

  lazy val drawerFabButton = Option(findView(TR.launcher_drawer_button))

  def showDrawerLoading: Ui[_] = (loadingDrawer <~ vVisible) ~
    (recycler <~ vGone) ~
    (scrollerLayout <~ fslInvisible)

  def showDrawerData: Ui[_] = (loadingDrawer <~ vGone) ~
    (recycler <~ vVisible) ~
    (scrollerLayout <~ fslVisible)

  def showGeneralError: Ui[_] = drawerContent <~ uiSnackbarShort(R.string.contactUsError)

  def initDrawerUi(
    onAppDrawerListener: () => Unit,
    onAppTabClickListener: () => Unit,
    onContactTabClickListener: () => Unit)(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (appDrawerMain <~ drawerAppStyle <~ On.click {
      revealInDrawer ~ Ui(onAppDrawerListener())
    }) ~
      (loadingDrawer <~ pbColor(resGetColor(R.color.drawer_toolbar))) ~
      (recycler <~ recyclerStyle) ~
      (scrollerLayout <~ fslColor(resGetColor(R.color.drawer_toolbar))) ~
      (drawerContent <~ vGone) ~
      (drawerFabButton <~ ivSrc(R.drawable.app_drawer_fab_button_play)) ~
      (drawerTabApp <~
        ttInitTab(
          drawableOn = R.drawable.app_drawer_icon_applications,
          drawableOff = R.drawable.app_drawer_icon_applications_inactive) <~
        On.click {
          appsTabClicked(onAppTabClickListener)
        }) ~
      (drawerTabContacts <~
        ttInitTab(
          drawableOn = R.drawable.app_drawer_icon_contacts,
          drawableOff = R.drawable.app_drawer_icon_contacts_inactive,
          selected = false) <~
        On.click {
          contactsTabClicked(onContactTabClickListener)
        })

  def isDrawerVisible = drawerContent exists (_.getVisibility == View.VISIBLE)

  def revealInDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    updateNavigationToBlack ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealInAppDrawer(source))) ~~
      updateStatusColor(resGetColor(R.color.drawer_status_bar))

  def revealOutDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    ((recycler <~
      rvAdapter(emptyAdapter)) ~
      updateStatusToTransparent ~
      updateNavigationToTransparent) ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealOutAppDrawer(source)))

  def addApps(apps: Seq[AppCategorized], clickListener: (AppCategorized) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] =
    swipeAdapter(new AppsAdapter(
      initialSeq = generateAppHeaderedList(apps),
      clickListener = clickListener))


  def addContacts(contacts: Seq[Contact], clickListener: (Contact) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] =
    swipeAdapter(new ContactsAdapter(
      initialSeq = generateContactsForList(contacts),
      clickListener = clickListener))

  private[this] def swipeAdapter(adapter: HeaderedItemAdapter[_]) =
    showDrawerData ~
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter) <~
        rvScrollToTop) ~
      (scrollerLayout <~
        fslLinkRecycler <~
        fslReset)

  private[this] def appsTabClicked(listener: () => Unit): Ui[_] =
    drawerTabApp map (_.isSelected) match {
      case Some(false) =>
        (drawerTabApp <~ ttSelect) ~
          (drawerTabContacts <~ ttUnselect) ~
          (drawerFabButton <~ ivSrc(R.drawable.app_drawer_fab_button_play)) ~
          Ui(listener())
      case Some(true) =>
        Ui.nop
      case _ =>
        Ui.nop
    }

  private[this] def contactsTabClicked(listener: () => Unit): Ui[_] =
    drawerTabContacts map (_.isSelected) match {
      case Some(false) =>
        (drawerTabContacts <~ ttSelect) ~
          (drawerTabApp <~ ttUnselect) ~
          (drawerFabButton <~ ivSrc(R.drawable.app_drawer_fab_button_contact)) ~
          Ui(listener())
      case Some(true) =>
        Ui.nop
      case _ =>
        Ui.nop
    }

}
