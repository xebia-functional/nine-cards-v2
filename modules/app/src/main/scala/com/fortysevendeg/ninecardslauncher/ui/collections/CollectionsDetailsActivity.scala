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
import com.fortysevendeg.ninecardslauncher.commons.ContentResolverWrapperImpl
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.repository.repositories.{CacheCategoryRepository, CardRepository, CollectionRepository, GeoInfoRepository}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.impl.PersistenceServicesImpl
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection
import com.fortysevendeg.ninecardslauncher.ui.collections.Snails._
import com.fortysevendeg.ninecardslauncher.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.ui.components.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.utils.SystemBarTintManager
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts, Tweak, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

class CollectionsDetailsActivity
  extends ActionBarActivity
  with Contexts[FragmentActivity]
  with Layout
  with ComponentRegistryImpl
  with ScrolledListener {

  override lazy val contextProvider: ContextWrapper = activityContextWrapper

  lazy val systemBarTintManager = new SystemBarTintManager(this)

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val elevation = resGetDimensionPixelSize(R.dimen.elevation_toolbar)

  private lazy val contentResolverWrapper = new ContentResolverWrapperImpl(contextProvider.application.getContentResolver)

  private lazy val persistenceServices = new PersistenceServicesImpl(
    new CacheCategoryRepository(contentResolverWrapper),
    new CardRepository(contentResolverWrapper),
    new CollectionRepository(contentResolverWrapper),
    new GeoInfoRepository(contentResolverWrapper))

  private def getAdapter: Option[CollectionsPagerAdapter] = {
    viewPager flatMap (ad => Option(ad.getAdapter)) flatMap {
      case adapter: CollectionsPagerAdapter => Some(adapter)
      case _ => None
    }
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(layout)
    toolbar map setSupportActionBar
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)

    systemBarTintManager.setStatusBarTintEnabled(true)

    for {
      FetchCollectionsResponse(collections) <- persistenceServices.fetchCollections(FetchCollectionsRequest())
    } yield {
      val adapter = CollectionsPagerAdapter(getSupportFragmentManager, collections)
      runUi(
        (viewPager <~ vpAdapter(adapter)) ~
          Ui(adapter.activateFragment(0)) ~
          (tabs <~
            stlViewPager(viewPager) <~
            stlOnPageChangeListener(new OnPageChangeCollectionsListener(collections, updateToolbarColor, updateCollection))) ~
          (viewPager map (vp => setIconCollection(collections(vp.getCurrentItem))) getOrElse Ui.nop)
      )
    }
  }

  private def setIconCollection(collection: Collection): Ui[_] = icon <~ ivSrc(iconCollectionDetail(collection.icon))

  private def updateCollection(collection: Collection, position: Int, fromLeft: Boolean): Ui[_] = getAdapter map {
    adapter =>
      (icon <~ changeIcon(iconCollectionDetail(collection.icon), fromLeft)) ~ adapter.notifyChanged(position)
  } getOrElse Ui.nop

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
      adapter <- getAdapter
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

class OnPageChangeCollectionsListener(
                                       collections: Seq[Collection],
                                       updateToolbarColor: Int => Ui[_],
                                       updateCollection: (Collection, Int, Boolean) => Ui[_])(implicit context: ContextWrapper)
  extends OnPageChangeListener
  with ComponentRegistryImpl {

  override val contextProvider: ContextWrapper = context

  var lastSelected = -1

  override def onPageScrollStateChanged(state: Int): Unit = {}

  override def onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int): Unit = {
    val selectedCollection: Collection = collections(position)
    val nextCollection: Option[Collection] = collections.lift(position + 1)
    nextCollection map {
      next =>
        val startColor = resGetColor(persistentServices.getIndexColor(selectedCollection.themedColorIndex))
        val endColor = resGetColor(persistentServices.getIndexColor(next.themedColorIndex))
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