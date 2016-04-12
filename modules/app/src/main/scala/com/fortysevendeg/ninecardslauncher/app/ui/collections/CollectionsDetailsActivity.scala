package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.Intent
import android.content.Intent._
import android.graphics.{Bitmap, BitmapFactory}
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.{AppInstalledActionFilter, AppsActionFilter}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiExtensions}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid._

import scala.util.Try

class CollectionsDetailsActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with CollectionsUiActionsImpl
  with TypedFindView
  with UiExtensions
  with ScrolledListener
  with ActionsScreenListener
  with SystemBarsTint
  with BroadcastDispatcher { self =>

  val tagDialog = "dialog"

  val defaultPosition = 0

  val defaultIndexColor = 0

  val defaultIcon = ""

  val defaultStateChanged = false

  override lazy val presenter = new CollectionsPagerPresenter(self)

  override val actionsFilters: Seq[String] = AppsActionFilter.cases map (_.action)

  override def manageCommand(action: String, data: Option[String]): Unit = (AppsActionFilter(action), data) match {
    case (AppInstalledActionFilter, _) => presenter.reloadCards(true)
    case _ =>
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)

    val position = getInt(
      Seq(bundle, getIntent.getExtras),
      startPosition,
      defaultPosition)

    val indexColor = getInt(
      Seq(bundle, getIntent.getExtras),
      indexColorToolbar,
      defaultIndexColor)

    val icon = getString(
      Seq(bundle, getIntent.getExtras),
      iconToolbar,
      defaultIcon)

    val isStateChanged = getBoolean(
      Seq(bundle, getIntent.getExtras),
      stateChanged,
      defaultStateChanged)

    setContentView(R.layout.collections_detail_activity)

    toolbar foreach setSupportActionBar
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    getSupportActionBar.setHomeAsUpIndicator(iconIndicatorDrawable)

    presenter.initialize(indexColor, icon, position, isStateChanged)

    registerDispatchers
  }

  override def onResume(): Unit = {
    super.onResume()
    presenter.resume()
  }

  override def onPause(): Unit = {
    super.onPause()
    presenter.pause()
    overridePendingTransition(0, 0)
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher
  }

  override def finishAfterTransition(): Unit = {
    super.finishAfterTransition()
    (toolbar <~ vVisible).run
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    outState.putInt(startPosition, getCurrentPosition getOrElse defaultPosition)
    outState.putBoolean(stateChanged, true)
    getCurrentCollection foreach { collection =>
      outState.putInt(indexColorToolbar, collection.themedColorIndex)
      outState.putString(iconToolbar, collection.icon)
    }
    super.onSaveInstanceState(outState)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    requestCode match {
      case `shortcutAdded` => Option(data) flatMap (i => Option(i.getExtras)) match {
        case Some(b: Bundle) if b.containsKey(EXTRA_SHORTCUT_NAME) && b.containsKey(EXTRA_SHORTCUT_INTENT) =>
          val shortcutName = b.getString(EXTRA_SHORTCUT_NAME)
          val shortcutIntent = b.getParcelable[Intent](EXTRA_SHORTCUT_INTENT)
          getCurrentCollection foreach { collection =>
            val maybeBitmap = getBitmapFromShortcutIntent(b)
            presenter.addShortcut(collection.id, shortcutName, shortcutIntent, maybeBitmap)
          }
        case _ =>
      }
    }
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.collection_detail_menu, menu)
    super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case android.R.id.home =>
      exitTransition.run
      false
    case _ => super.onOptionsItemSelected(item)
  }

  override def onBackPressed(): Unit = presenter.back()

  override def scrollY(scroll: Int, dy: Int): Unit = translationScrollY(scroll).run

  override def openReorderMode(current: ScrollType): Unit = openReorderModeUi(current).run

  override def closeReorderMode(): Unit = closeReorderModeUi.run

  override def scrollType(sType: ScrollType): Unit = notifyScroll(sType).run

  override def onEmptyCollection(): Unit = loadFabButton(autoHide = false)

  override def onFirstItemInCollection(): Unit = hideFabButton.run

  override def pullToClose(scroll: Int, scrollType: ScrollType, close: Boolean): Unit =
    pullCloseScrollY(scroll, scrollType, close).run

  override def close(): Unit = exitTransition.run

  override def startScroll(): Unit = loadFabButton(autoHide = true)

  override def onStartFinishAction(): Unit = turnOffFragmentContent.run

  override def onEndFinishAction(): Unit = removeActionFragment

  private[this] def getBitmapFromShortcutIntent(bundle: Bundle): Option[Bitmap] = bundle match {
    case b if b.containsKey(EXTRA_SHORTCUT_ICON) =>
      Try(b.getParcelable[Bitmap](EXTRA_SHORTCUT_ICON)).toOption
    case b if b.containsKey(EXTRA_SHORTCUT_ICON_RESOURCE) =>
      val extra = Try(b.getParcelable[ShortcutIconResource](EXTRA_SHORTCUT_ICON_RESOURCE)).toOption
      extra flatMap { e =>
        val resources = getPackageManager.getResourcesForApplication(e.packageName)
        val id = resources.getIdentifier(e.resourceName, javaNull, javaNull)
        Option(BitmapFactory.decodeResource(resources, id))
      }
    case _ => None
  }

  private[this] def loadFabButton(autoHide: Boolean = true): Unit = getCurrentCollection foreach { collection =>
    val color = getIndexColor(collection.themedColorIndex)
    showFabButton(color, autoHide).run
  }

}

trait ScrolledListener {
  def startScroll(): Unit

  def scrollY(scroll: Int, dy: Int): Unit

  def scrollType(sType: ScrollType): Unit

  def pullToClose(scroll: Int, scrollType: ScrollType, close: Boolean): Unit

  def openReorderMode(currentScrollType: ScrollType): Unit

  def closeReorderMode(): Unit

  def onEmptyCollection(): Unit

  def onFirstItemInCollection(): Unit

  def close(): Unit
}

trait ActionsScreenListener {
  def onStartFinishAction()

  def onEndFinishAction()
}

object CollectionsDetailsActivity {
  val startPosition = "start_position"
  val indexColorToolbar = "color_toolbar"
  val iconToolbar = "icon_toolbar"
  val stateChanged = "state_changed"
  val snapshotName = "snapshot"

  def getContentTransitionName(position: Int) = s"icon_$position"
}