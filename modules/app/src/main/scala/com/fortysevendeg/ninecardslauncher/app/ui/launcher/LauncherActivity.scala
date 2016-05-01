package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.collections.ActionsScreenListener
import com.fortysevendeg.ninecardslauncher.app.ui.commons._
import com.fortysevendeg.ninecardslauncher.process.user.models.User
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid._

class LauncherActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with ActionsScreenListener
  with LauncherUiActionsImpl
  with SystemBarsTint { self =>

  lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  lazy val presenter: LauncherPresenter = new LauncherPresenter(self)

  lazy val managerContext: FragmentManagerContext[Fragment, FragmentManager] = activityManagerContext

  private[this] var hasFocus = false

  var userProfileStatuses = UserProfileStatuses()

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(R.layout.launcher_activity)
    presenter.initialize()
  }

  override def onResume(): Unit = {
    super.onResume()
    presenter.resume()
  }

  override def onPause(): Unit = {
    super.onPause()
    presenter.pause()
  }

  override def onStartFinishAction(): Unit = presenter.resetAction()

  override def onEndFinishAction(): Unit = removeActionFragment

  override def onBackPressed(): Unit = presenter.back()

  override def onWindowFocusChanged(hasFocus: Boolean): Unit = {
    super.onWindowFocusChanged(hasFocus)
    this.hasFocus = hasFocus
  }

  override def onNewIntent(intent: Intent): Unit = {
    super.onNewIntent(intent)
    val alreadyOnHome = hasFocus && ((intent.getFlags &
      Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
      != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
    if (alreadyOnHome) presenter.back()
  }

  override def dispatchKeyEvent(event: KeyEvent): Boolean = (event.getAction, event.getKeyCode) match {
    case (KeyEvent.ACTION_DOWN | KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME) => true
    case _ => super.dispatchKeyEvent(event)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    userProfileStatuses.userProfile foreach (_.connectUserProfile(requestCode, resultCode, data))
    (requestCode, resultCode) match {
      case (RequestCodes.goToCollectionDetails, _) =>
        presenter.resetFromCollectionDetail()
      case (RequestCodes.goToProfile, ResultCodes.logoutSuccessful) =>
        presenter.logout()
      case _ =>
    }
  }

  override def loadUserProfile(user: User): Ui[Any] = Ui {
    val userProfile = user.email map { email =>
      new UserProfileProvider(
        account = email,
        onConnectedUserProfile = presenter.connectUserProfile,
        onConnectedPlusProfile = presenter.connectPlusProfile)
    }
    userProfileStatuses = userProfileStatuses.copy(userProfile = userProfile)
    userProfileStatuses.userProfile foreach (_.connect())
  }

}
