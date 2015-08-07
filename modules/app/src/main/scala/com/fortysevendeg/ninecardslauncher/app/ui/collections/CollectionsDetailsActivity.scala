package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.annotation.TargetApi
import android.os.{Build, Bundle}
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.transition.{Transition, ChangeBounds, Explode, Slide}
import android.view.ViewTreeObserver.OnPreDrawListener
import android.view.{ViewTreeObserver, Menu, MenuItem}
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiExtensions
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.Contexts
import macroid.FullDsl._
import rapture.core.Answer

import scalaz.concurrent.Task

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

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)

    val position = getInt(
      Seq(getIntent.getExtras, bundle),
      startPosition,
      defaultPosition)

    setContentView(R.layout.collections_detail_activity)

//    icon foreach (_.setTransitionName(getContentTransitionName(position)))

//    runUi(initUi)

    configureEnterTransition(position)

    toolbar foreach setSupportActionBar
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    systemBarTintManager.setStatusBarTintEnabled(true)

    Task.fork(di.collectionProcess.getCollections.run).resolveAsyncUi(
      onResult = (collections: Seq[Collection]) => drawCollections(collections, position),
      onPreTask = () => initUi
    )
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private[this] def configureEnterTransition(position: Int) = {

//    val changeBounds = new ChangeBounds()
//    changeBounds.addTarget(R.id.collections_icon)
//
//    getWindow.setSharedElementEnterTransition(changeBounds)

    postponeEnterTransition()

    icon foreach (_.setTransitionName(getContentTransitionName(position)))

    root.get.getViewTreeObserver.addOnPreDrawListener(new OnPreDrawListener {
      override def onPreDraw(): Boolean = {
        root.get.getViewTreeObserver.removeOnPreDrawListener(this)
        startPostponedEnterTransition()
        true
      }
    })

    getWindow.getSharedElementEnterTransition.addListener(new Transition.TransitionListener {
      override def onTransitionStart(transition: Transition): Unit = {}

      override def onTransitionCancel(transition: Transition): Unit = {}

      override def onTransitionEnd(transition: Transition): Unit = {
        runUi(showViews(position))
      }

      override def onTransitionPause(transition: Transition): Unit = {}

      override def onTransitionResume(transition: Transition): Unit = {}
    })
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

object CollectionsDetailsActivity {
  val startPosition = "start_position"

  def getContentTransitionName(position: Int) = s"icon_$position"
}