package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.ui.collections.ActionsScreenListener
import com.fortysevendeg.ninecardslauncher.app.ui.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid._

class LauncherActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with ActionsScreenListener
  with LauncherUiActionsImpl
  with SystemBarsTint
  with BroadcastDispatcher { self =>

  lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  lazy val presenter: LauncherPresenter = new LauncherPresenter(self)

  lazy val managerContext: FragmentManagerContext[Fragment, FragmentManager] = activityManagerContext

  private[this] var hasFocus = false

  override val actionsFilters: Seq[String] = (MomentsActionFilter.cases map (_.action)) ++ (CollectionsActionFilter.cases map (_.action))

  override def manageCommand(action: String, data: Option[String]): Unit = {
    (MomentsActionFilter(action), data) match {
      case (Some(MomentReloadedActionFilter), _) => presenter.reloadAppsMomentBar()
      case (Some(MomentConstrainsChangedActionFilter), _) => presenter.reloadAppsMomentBar()
      case (Some(MomentForceBestAvailableActionFilter), _) => presenter.changeMomentIfIsAvailable()
      case _ =>
    }
    (CollectionsActionFilter(action), data) match {
      case (Some(CollectionAddedActionFilter), Some(colId)) => presenter.reloadCollection(colId)
      case _ =>
    }
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(R.layout.launcher_activity)
    registerDispatchers
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

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher
    presenter.destroy()
  }

  override def onStartFinishAction(): Unit = presenter.resetAction()

  override def onEndFinishAction(): Unit = presenter.destroyAction()

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

    def getExtraAppWidgetId = Option(data) flatMap(d => Option(d.getExtras)) flatMap { extras =>
      val id = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
      if (id == 0) None else Some(id)
    }

    (requestCode, resultCode) match {
      case (RequestCodes.goToCollectionDetails, _) =>
        presenter.resetFromCollectionDetail()
      case (RequestCodes.goToProfile, ResultCodes.logoutSuccessful) =>
        presenter.logout()
      case (RequestCodes.goToWidgets, Activity.RESULT_OK) =>
        presenter.configureOrAddWidget(getExtraAppWidgetId)
      case (RequestCodes.goToConfigureWidgets, Activity.RESULT_OK) =>
        presenter.addWidget(getExtraAppWidgetId)
      case (RequestCodes.goToConfigureWidgets | RequestCodes.goToWidgets, Activity.RESULT_CANCELED) =>
        presenter.cancelWidget(getExtraAppWidgetId)
      case (RequestCodes.goToPreferences, ResultCodes.preferencesChanged) =>
        presenter.preferencesChanged(data.getStringArrayExtra(ResultData.preferencesResultData))
      case _ =>
    }
  }

}
