package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.Intent
import android.content.Intent._
import android.graphics.{Bitmap, BitmapFactory}
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ActivityResult._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiExtensions}
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.FullDsl._
import macroid.{Contexts, Ui}
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
  with CollectionDetailsTasks {

  val defaultPosition = 0

  val defaultIcon = ""

  implicit lazy val di = new Injector

  var collections: Seq[Collection] = Seq.empty

  implicit lazy val theme: NineCardsTheme = di.themeProcess.getSelectedTheme.run.run match {
    case Answer(t) => t
    case _ => getDefaultTheme
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)

    val position = getInt(
      Seq(getIntent.getExtras, bundle),
      startPosition,
      defaultPosition)

    val indexColor = getInt(
      Seq(getIntent.getExtras, bundle),
      indexColorToolbar,
      defaultPosition)

    val icon = getString(
      Seq(getIntent.getExtras, bundle),
      iconToolbar,
      defaultIcon)

    setContentView(R.layout.collections_detail_activity)

    runUi(initUi(indexColor, icon))

    configureEnterTransition(position, () => runUi(ensureDrawCollection(position)))

    toolbar foreach setSupportActionBar
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    getSupportActionBar.setHomeAsUpIndicator(iconIndicatorDrawable)

    initSystemStatusBarTint

    Task.fork(di.collectionProcess.getCollections.run).resolveAsync(
      onResult = (c: Seq[Collection]) => collections = c
    )

  }

  override def finishAfterTransition(): Unit = {
    super.finishAfterTransition()
    runUi(toolbar <~ vVisible)
  }

  def ensureDrawCollection(position: Int): Ui[_] = if (collections.isEmpty) {
    uiHandlerDelayed(ensureDrawCollection(position), 200)
  } else {
    drawCollections(collections, position)
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
      finish()
      true
    case _ => super.onOptionsItemSelected(item)
  }

  override def scrollY(scroll: Int, dy: Int): Unit = runUi(translationScrollY(scroll))

  override def scrollType(sType: Int): Unit = runUi(notifyScroll(sType))

  override def onBackPressed(): Unit = (fabMenuOpened, isActionShowed) match {
    case (true, _) => runUi(swapFabButton())
    case (_, true) => runUi(unrevealActionFragment())
    case _ => finish()
  }

  override def pullToClose(scroll: Int, scrollType: Int, close: Boolean): Unit =
    runUi(pullCloseScrollY(scroll, scrollType, close))

  override def close(): Unit = finish()

  override def startScroll(): Unit = getCurrentCollection foreach { collection =>
    val color = getIndexColor(collection.themedColorIndex)
    runUi(showFabButton(color))
  }

  override def onStartFinishAction(): Unit = runUi(turnOffFragmentContent)

  override def onEndFinishAction(): Unit = removeActionFragment()

  override def addCards(cards: Seq[AddCardRequest]): Unit =
    getCurrentCollection foreach { collection =>
      Task.fork(createCards(collection.id, cards).run).resolveAsync(
        onResult = addCardsToCurrentFragment(_)
      )
    }

  private[this] def getBitmapFromShortcutIntent(bundle: Bundle): Option[Bitmap] = bundle match {
    case b if b.containsKey(EXTRA_SHORTCUT_ICON) =>
      Try(b.getParcelable[Bitmap](EXTRA_SHORTCUT_ICON)).toOption
    case b if b.containsKey(EXTRA_SHORTCUT_ICON_RESOURCE) =>
      val extra = Try(b.getParcelable[ShortcutIconResource](EXTRA_SHORTCUT_ICON_RESOURCE)).toOption
      extra flatMap { e =>
        val resources = getPackageManager.getResourcesForApplication(e.packageName)
        val id = resources.getIdentifier(e.resourceName, null, null)
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

  def addCards(cards: Seq[AddCardRequest])
}

object ScrollType {
  val up = 0
  val down = 1
}

object CollectionsDetailsActivity {
  val startPosition = "start_position"
  val indexColorToolbar = "color_toolbar"
  val iconToolbar = "icon_toolbar"
  val snapshotName = "snapshot"

  def getContentTransitionName(position: Int) = s"icon_$position"
}