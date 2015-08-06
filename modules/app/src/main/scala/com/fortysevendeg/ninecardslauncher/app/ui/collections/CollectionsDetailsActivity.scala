package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.os.Bundle
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v7.app.AppCompatActivity
import android.view.{Menu, MenuItem}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts, Ui}
import rapture.core.Answer

import scalaz.concurrent.Task

class CollectionsDetailsActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with CollectionsDetailsComposer
  with TypedFindView
  with ScrolledListener {

  lazy val di = new Injector

  implicit lazy val theme: NineCardsTheme = di.themeProcess.getSelectedTheme.run.run match {
    case Answer(t) => t
    case _ => getDefaultTheme
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(R.layout.collections_detail_activity)
    toolbar foreach setSupportActionBar
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    systemBarTintManager.setStatusBarTintEnabled(true)

    Task.fork(di.collectionProcess.getCollections.run).resolveAsyncUi(
      onResult = (collections: Seq[Collection]) => fetchCollections(collections),
      onPreTask = () => initUi
    )
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
}

trait ScrolledListener {
  def scrollY(scroll: Int, dy: Int)
  def scrollType(sType: Int)
}

object ScrollType {
  val up = 0
  val down = 1
}

class OnPageChangeCollectionsListener(
  collections: Seq[Collection],
  updateToolbarColor: Int => Ui[_],
  updateCollection: (Collection, Int, Boolean) => Ui[_]
  )(implicit context: ContextWrapper, theme: NineCardsTheme)
  extends OnPageChangeListener {

  var lastSelected = -1

  override def onPageScrollStateChanged(state: Int): Unit = {}

  override def onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int): Unit = {
    val selectedCollection: Collection = collections(position)
    val nextCollection: Option[Collection] = collections.lift(position + 1)
    nextCollection map {
      next =>
        val startColor = resGetColor(getIndexColor(selectedCollection.themedColorIndex))
        val endColor = resGetColor(getIndexColor(next.themedColorIndex))
        val color = interpolateColors(positionOffset, startColor, endColor)
        runUi(updateToolbarColor(color))
    }
  }

  override def onPageSelected(position: Int): Unit = {
    val fromLeft = position < lastSelected
    lastSelected = position
    runUi(updateCollection(collections(position), position, fromLeft))
  }

}