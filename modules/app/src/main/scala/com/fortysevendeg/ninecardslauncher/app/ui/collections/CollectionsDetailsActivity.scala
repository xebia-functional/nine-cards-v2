package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiExtensions}
import com.fortysevendeg.ninecardslauncher.process.collection.{CardException, AddCardRequest}
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.{Ui, Contexts}
import macroid.FullDsl._
import rapture.core.Answer
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.macroid.extras.UIActionsExtras._

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
  with CollectionsDetailDependencies
  with SystemBarsTint {

  val defaultPosition = 0

  val defaultIcon = ""

  lazy val di = new Injector

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

  override def startScroll(): Unit = getAdapter flatMap (_.getCurrentFragmentPosition) map { position =>
    val color = getIndexColor(collections(position).themedColorIndex)
    runUi(showFabButton(color))
  }

  override def onStartFinishAction(): Unit = runUi(turnOffFragmentContent)

  override def onEndFinishAction(): Unit = removeActionFragment()

  override def addCards(cards: Seq[AddCardRequest]): Unit = for {
    adapter <- getAdapter
    fragment <- adapter.getActiveFragment
    currentPosition <- adapter.getCurrentFragmentPosition
  } yield {
      Task.fork(di.collectionProcess.addCards(collections(currentPosition).id, cards).run).resolveAsync(
        onResult = (c: Seq[Card]) => {
          adapter.addCardsToCollection(currentPosition, c)
          fragment.addCards(c)
        }
      )
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