package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.view.{MenuItem, Menu}
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ActivityUiContext, UiContext, SystemBarsTint}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.Contexts
import macroid.FullDsl._
import rapture.core.Answer

import scalaz.concurrent.Task

class ProfileActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with SystemBarsTint
  with ProfileTabListener
  with ProfileComposer
  with AppBarLayout.OnOffsetChangedListener {

  implicit lazy val di = new Injector

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  implicit lazy val theme: NineCardsTheme = di.themeProcess.getSelectedTheme.run.run match {
    case Answer(t) => t
    case _ => getDefaultTheme
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    loadUserInfo
    setContentView(R.layout.profile_activity)
    runUi(initUi)

    toolbar foreach setSupportActionBar
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    getSupportActionBar.setHomeAsUpIndicator(iconIndicatorDrawable)

    barLayout foreach (_.addOnOffsetChangedListener(this))
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.profile_menu, menu)
    super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case android.R.id.home =>
      finish()
      true
    case _ =>
      super.onOptionsItemSelected(item)
  }

  override def onOffsetChanged(appBarLayout: AppBarLayout, offset: Int): Unit = {
    val maxScroll = appBarLayout.getTotalScrollRange.toFloat
    val percentage = Math.abs(offset) / maxScroll

    runUi(handleToolbarVisibility(percentage) ~ handleProfileVisibility(percentage))
  }

  def sampleItems(tab: String) = 1 to 20 map (i => s"$tab Item $i")

  override def onProfileTabSelected(profileTab: ProfileTab): Unit = profileTab match {
    case PublicationsTab =>
      // TODO - Load publications and set adapter
      runUi(setPublicationsAdapter(sampleItems("Publication")))
    case SubscriptionsTab =>
      // TODO - Load subscriptions and set adapter
      runUi(setSubscriptionsAdapter(sampleItems("Subscription")))
    case AccountsTab =>
      // TODO - Load accounts and set adapter
      runUi(setAccountsAdapter(sampleItems("Account")))
  }

  private[this] def loadUserInfo(implicit uiContext: UiContext[_]) = Task.fork(di.userConfigProcess.getUserInfo.run).resolveAsyncUi(
    onResult = (userInfo) => userProfile(userInfo.email, userInfo.imageUrl)
  )

}
