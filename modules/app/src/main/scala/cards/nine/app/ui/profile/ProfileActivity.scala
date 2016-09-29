package cards.nine.app.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.view.{Menu, MenuItem}
import cards.nine.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher.{R, TypedFindView}
import macroid.Contexts

class ProfileActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with ProfileUiActionsImpl
  with AppBarLayout.OnOffsetChangedListener
  with BroadcastDispatcher { self =>

  import SyncDeviceState._

  lazy val presenter = new ProfilePresenter(self)

  lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  override val actionsFilters: Seq[String] = SyncActionFilter.cases map (_.action)

  override def manageCommand(action: String, data: Option[String]): Unit = (SyncActionFilter(action), data) match {
    case (SyncStateActionFilter, Some(`stateSuccess`)) =>
      presenter.accountSynced()
    case (SyncStateActionFilter, Some(`stateFailure`)) =>
      presenter.errorSyncing()
    case (SyncAnswerActionFilter, Some(`stateSyncing`)) =>
      presenter.stateSyncing()
    case _ =>
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(R.layout.profile_activity)
    presenter.initialize()

    setSupportActionBar(toolbar)
    Option(getSupportActionBar) foreach { actionBar =>
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setHomeAsUpIndicator(iconIndicatorDrawable)
    }

    barLayout.addOnOffsetChangedListener(this)
  }

  override def onResume(): Unit = {
    super.onResume()
    registerDispatchers
    self ? SyncAskActionFilter.action
  }

  override def onPause(): Unit = {
    super.onPause()
    unregisterDispatcher
  }

  override def onStop(): Unit = {
    presenter.stop()
    super.onStop()
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.profile_menu, menu)
    super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case android.R.id.home =>
      finish()
      true
    case R.id.action_logout =>
      presenter.quit()
      true
    case _ =>
      super.onOptionsItemSelected(item)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit =
    if (!presenter.activityResult(requestCode, resultCode, data)) {
      super.onActivityResult(requestCode, resultCode, data)
    }

  override def onOffsetChanged(appBarLayout: AppBarLayout, offset: Int): Unit = {
    val maxScroll = appBarLayout.getTotalScrollRange.toFloat
    val percentage = Math.abs(offset) / maxScroll
    presenter.onOffsetChanged(percentage)
  }
}
