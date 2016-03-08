package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.Intent
import android.content.Intent._
import android.graphics.{Bitmap, BitmapFactory}
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.{AppInstalledActionFilter, AppsActionFilter}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiExtensions}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid._
import rapture.core.Answer

import scala.util.Try
import scalaz.concurrent.Task

class CollectionsDetailsActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with CollectionsDetailsComposer
  with TypedFindView
  with UiExtensions
  with ScrolledListener
  with ActionsScreenListener
  with SystemBarsTint
  with BroadcastDispatcher
  with CollectionDetailsTasks {

  val tagDialog = "dialog"

  val defaultPosition = 0

  val defaultIcon = ""

  val defaultToDoAnimation = true

  implicit lazy val di = new Injector

  var collections: Seq[Collection] = Seq.empty

  implicit lazy val theme: NineCardsTheme = di.themeProcess.getSelectedTheme.run.run match {
    case Answer(t) => t
    case _ => getDefaultTheme
  }

  override val actionsFilters: Seq[String] = AppsActionFilter.cases map (_.action)

  override def manageCommand(action: String, data: Option[String]): Unit = (AppsActionFilter(action), data) match {
    case (AppInstalledActionFilter, _) => reloadCards()
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
      defaultPosition)

    val icon = getString(
      Seq(bundle, getIntent.getExtras),
      iconToolbar,
      defaultIcon)

    val doAnimation = getBoolean(
      Seq(bundle, getIntent.getExtras),
      toDoAnimation,
      defaultToDoAnimation)

    setContentView(R.layout.collections_detail_activity)

    initUi(indexColor, icon).run

    toolbar foreach setSupportActionBar
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    getSupportActionBar.setHomeAsUpIndicator(iconIndicatorDrawable)

    initSystemStatusBarTint

    if (doAnimation) {
      configureEnterTransition(position, () => ensureDrawCollection(position).run)
      Task.fork(di.collectionProcess.getCollections.run).resolveAsync(
        onResult = (c: Seq[Collection]) => collections = c,
        onException = (ex: Throwable) => showError().run
      )
    } else {
      Task.fork(di.collectionProcess.getCollections.run).resolveAsyncUi(
        onResult = (c: Seq[Collection]) => drawCollections(c, position),
        onException = (ex: Throwable) => showError()
      )
    }

    registerDispatchers
  }

  override def onPause(): Unit = {
    super.onPause()
    overridePendingTransition(0, 0)
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher
  }

  def ensureDrawCollection(position: Int): Ui[_] = if (collections.isEmpty) {
    uiHandlerDelayed(ensureDrawCollection(position), 200)
  } else {
    drawCollections(collections, position)
  }

  override def finishAfterTransition(): Unit = {
    super.finishAfterTransition()
    (toolbar <~ vVisible).run
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    outState.putInt(startPosition, getCurrentPosition getOrElse defaultPosition)
    outState.putBoolean(toDoAnimation, false)
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
            Task.fork(createShortcut(collection.id, shortcutName, shortcutIntent, maybeBitmap).run).resolveAsync(
              onResult = addCardsToCurrentFragment(_)
            )
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

  override def scrollY(scroll: Int, dy: Int): Unit = translationScrollY(scroll).run

  override def scrollType(sType: Int): Unit = notifyScroll(sType).run

  override def onBackPressed(): Unit = backByPriority.run

  override def pullToClose(scroll: Int, scrollType: Int, close: Boolean): Unit =
    pullCloseScrollY(scroll, scrollType, close).run

  override def close(): Unit = exitTransition.run

  override def startScroll(): Unit = getCurrentCollection foreach { collection =>
    val color = getIndexColor(collection.themedColorIndex)
    showFabButton(color).run
  }

  override def onStartFinishAction(): Unit = turnOffFragmentContent.run

  override def onEndFinishAction(): Unit = removeActionFragment

  def addCards(cards: Seq[AddCardRequest]): Unit =
    getCurrentCollection foreach { collection =>
      Task.fork(createCards(collection.id, cards).run).resolveAsync(
        onResult = addCardsToCurrentFragment(_)
      )
    }

  def removeCard(card: Card): Unit = {
    val ft = getSupportFragmentManager.beginTransaction()
    Option(getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
    ft.addToBackStack(javaNull)
    val dialog = new RemoveCardDialogFragment(() => {
      getCurrentCollection foreach { collection =>
        Task.fork(removeCard(collection.id, card.id).run).resolveAsync(
          onResult = (_) => removeCardFromCurrentFragment(card)
        )
      }
    })
    dialog.show(ft, tagDialog)
  }

  def reloadCards(): Unit =
    getCurrentCollection foreach { currentCollection =>
      Task.fork(di.collectionProcess.getCollectionById(currentCollection.id).run).resolveAsync(
        onResult = (c) => c map (newCollection => if (newCollection.cards != currentCollection.cards) {
          reloadCardsToCurrentFragment(newCollection.cards)
        })
      )
    }

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

}

trait ScrolledListener {
  def startScroll()

  def scrollY(scroll: Int, dy: Int)

  def scrollType(sType: Int)

  def pullToClose(scroll: Int, scrollType: Int, close: Boolean)

  def close()
}

trait ActionsScreenListener {
  def onStartFinishAction()

  def onEndFinishAction()
}

object ScrollType {
  val up = 0
  val down = 1
}

object CollectionsDetailsActivity {
  val startPosition = "start_position"
  val indexColorToolbar = "color_toolbar"
  val iconToolbar = "icon_toolbar"
  val toDoAnimation = "to_do_animation"
  val snapshotName = "snapshot"

  def getContentTransitionName(position: Int) = s"icon_$position"
}