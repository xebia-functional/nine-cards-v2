package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiExtensions
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.Contexts
import macroid.FullDsl._
import rapture.core.Answer

class CollectionsDetailsActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with CollectionsDetailsComposer
  with TypedFindView
  with UiExtensions
  with ScrolledListener {

  val defaultPosition = 0

  lazy val di = new Injector

  implicit lazy val theme: NineCardsTheme = di.themeProcess.getSelectedTheme.run.run match {
    case Answer(t) => t
    case _ => getDefaultTheme
  }

  lazy val collections: Seq[Collection] = di.collectionProcess.getCollections.run.run match {
    case Answer(c) => c
    case _ => Seq.empty
  }

  lazy val position = getInt(
    Seq(getIntent.getExtras),
    startPosition,
    defaultPosition)

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)

    setContentView(R.layout.collections_detail_activity)

    runUi(initUi(collections(position)))

    configureEnterTransition(position, () => runUi(drawCollections(collections, position)))

    toolbar foreach setSupportActionBar
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    systemBarTintManager.setStatusBarTintEnabled(true)

  }

  override def finishAfterTransition(): Unit = {
    super.finishAfterTransition()
    runUi(toolbar <~ vVisible)
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

  override def onBackPressed(): Unit = finish()
}

trait ScrolledListener {
  def scrollY(scroll: Int, dy: Int)

  def scrollType(sType: Int)
}

object ScrollType {
  val up = 0
  val down = 1
}

object CollectionsDetailsActivity {
  val startPosition = "start_position"
  val snapshotName = "snapshot"
  def getContentTransitionName(position: Int) = s"icon_$position"
}