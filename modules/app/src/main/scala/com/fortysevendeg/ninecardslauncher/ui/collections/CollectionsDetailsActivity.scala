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
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.GetAppsRequest
import com.fortysevendeg.ninecardslauncher.modules.repository.{Collection, GetCollectionsRequest, GetCollectionsResponse}
import com.fortysevendeg.ninecardslauncher.ui.collections.Snails._
import com.fortysevendeg.ninecardslauncher.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.ui.components.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.utils.SystemBarTintManager
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{AppContext, Contexts, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

class CollectionsDetailsActivity
  extends ActionBarActivity
  with Contexts[FragmentActivity]
  with Layout
  with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = AppContext(getApplicationContext)

  lazy val systemBarTintManager = new SystemBarTintManager(this)

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
      val pageChangeListener = new OnPageChangeListener {
        override def onPageScrollStateChanged(state: Int): Unit = {}

        override def onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int): Unit = {
          val selectedCollection: Collection = collections(position)
          val nextCollection: Option[Collection] = collections.lift(position + 1)
          nextCollection map {
            toCollection =>
              val startColor = resGetColor(persistentServices.getIndexColor(selectedCollection.themedColorIndex))
              val endColor = resGetColor(persistentServices.getIndexColor(toCollection.themedColorIndex))
              val color = interpolateColors(positionOffset, startColor, endColor)
              runUi(updateToolbarColor(color))
          }
        }

        override def onPageSelected(position: Int): Unit = runUi(updateCollection(collections(position)))
      }
      runUi(
        (viewPager <~ vpAdapter(new CollectionsPagerAdapter(getSupportFragmentManager, collections))) ~
          (tabs <~
            stlViewPager(viewPager) <~
            stlOnPageChangeListener(pageChangeListener)) ~
            (viewPager map (vp => updateCollection(collections(vp.getCurrentItem), false)) getOrElse Ui.nop)
      )
    }

  }

  private def updateCollection(collection: Collection, anim: Boolean = true): Ui[_] = resGetDrawableIdentifier(collection.icon + "_detail") map {
    res => if (anim) icon <~ changeIcon(res) else icon <~ ivSrc(res)
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

}
