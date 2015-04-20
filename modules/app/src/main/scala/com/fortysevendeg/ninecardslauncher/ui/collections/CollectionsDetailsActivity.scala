package com.fortysevendeg.ninecardslauncher.ui.collections

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v7.app.ActionBarActivity
import android.view.{Menu, MenuItem}
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewPagerTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.di.ActivityInjectorProvider
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.GetAppsRequest
import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistentServices
import com.fortysevendeg.ninecardslauncher.modules.repository.{Collection, GetCollectionsRequest, GetCollectionsResponse}
import com.fortysevendeg.ninecardslauncher.ui.collections.Snails._
import com.fortysevendeg.ninecardslauncher.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.ui.components.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.utils.SystemBarTintManager
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{AppContext, Contexts, Tweak, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

class CollectionsDetailsActivity
  extends ActionBarActivity
  with Contexts[FragmentActivity]
  with Layout
  with ComponentRegistryImpl
  with ScrolledListener
  with ActivityInjectorProvider {

  override implicit lazy val appContextProvider: AppContext = AppContext(getApplicationContext)

  lazy val systemBarTintManager = new SystemBarTintManager(this)

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val elevation = resGetDimensionPixelSize(R.dimen.elevation_toolbar)

  private var collectionsAdapter: Option[CollectionsPagerAdapter] = None

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(layout)
    toolbar map setSupportActionBar
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)

    systemBarTintManager.setStatusBarTintEnabled(true)

    for {
      appsResponse <- appManagerServices.getApps(GetAppsRequest())
      GetCollectionsResponse(collections) <- repositoryServices.getCollections(GetCollectionsRequest(appsResponse.apps))
    } yield {
      val adapter = new CollectionsPagerAdapter(getSupportFragmentManager, collections)
      collectionsAdapter = Some(adapter)
      runUi(
        (viewPager <~ vpAdapter(adapter)) ~
          Ui(adapter.activateFragment(0)) ~
          (tabs <~
            stlViewPager(viewPager) <~
            stlOnPageChangeListener(new OnPageChangeCollectionsListener(di map (_.persistentServices), collections, updateToolbarColor, updateCollection))) ~
          (viewPager map (vp => setIconCollection(collections(vp.getCurrentItem))) getOrElse Ui.nop)
      )
    }
  }

  private def setIconCollection(collection: Collection): Ui[_] =
    resGetDrawableIdentifier(s"${collection.icon}_detail") map (r => icon <~ ivSrc(r)) getOrElse Ui.nop

  private def updateCollection(collection: Collection, position: Int, fromLeft: Boolean): Ui[_] =
    (for {
      res <- resGetDrawableIdentifier(s"${collection.icon}_detail")
      adapter <- collectionsAdapter
    } yield {
        (icon <~ changeIcon(res, fromLeft)) ~ adapter.notifyChanged(position)
      }).getOrElse(Ui.nop)

  private def updateToolbarColor(color: Int): Ui[_] =
    (toolbar <~ vBackgroundColor(color)) ~
      Ui {
        Lollipop ifSupportedThen {
          getWindow.setStatusBarColor(color)
        } getOrElse {
          systemBarTintManager.setStatusBarTintColor(color)
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

  override def scrollY(scroll: Int, dy: Int): Unit = {
    runUi(scrolled(scroll))
  }

  private def scrolled(scroll: Int): Ui[_] = {
    val move = math.min(scroll, spaceMove)
    val ratio: Float = move.toFloat / spaceMove.toFloat
    val newElevation = elevation + (if (ratio >= 1) 1 else 0)
    val scale = 1 - (ratio / 2)
    (tabs <~ vTranslationY(-move) <~ uiElevation(newElevation)) ~
      (toolbar <~ vTranslationY(-move * 2) <~ uiElevation(newElevation)) ~
      (iconContent <~ uiElevation(newElevation) <~ vScaleX(scale) <~ vScaleY(scale) <~ vAlpha(1 - ratio))
  }

  private def uiElevation(elevation: Float) = Lollipop.ifSupportedThen {
    vElevation(elevation)
  }.getOrElse(Tweak.blank)

  override def scrollType(sType: Int): Unit = {
    for {
      vp <- viewPager
      adapter <- collectionsAdapter
    } yield {
      adapter.setScrollType(sType)
      runUi(adapter.notifyChanged(vp.getCurrentItem))
    }
  }
}

trait ScrolledListener {
  def scrollY(scroll: Int, dy: Int)

  def scrollType(sType: Int)
}

object ScrollType {
  val Up = 0
  val Down = 1
}

class OnPageChangeCollectionsListener(persistentServices: Option[PersistentServices],
                                       collections: Seq[Collection],
                                       updateToolbarColor: Int => Ui[_],
                                       updateCollection: (Collection, Int, Boolean) => Ui[_])(implicit appContext: AppContext)
  extends OnPageChangeListener
  with ComponentRegistryImpl {

  override val appContextProvider: AppContext = appContext

  var lastSelected = -1

  override def onPageScrollStateChanged(state: Int): Unit = {}

  override def onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int): Unit = {
    val selectedCollection: Collection = collections(position)
    val nextCollection: Option[Collection] = collections.lift(position + 1)
    nextCollection map {
      next =>
        val startColor = persistentServices map (ps => resGetColor(ps.getIndexColor(selectedCollection.themedColorIndex))) getOrElse 0
        val endColor = persistentServices map (ps => resGetColor(ps.getIndexColor(next.themedColorIndex))) getOrElse 0
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