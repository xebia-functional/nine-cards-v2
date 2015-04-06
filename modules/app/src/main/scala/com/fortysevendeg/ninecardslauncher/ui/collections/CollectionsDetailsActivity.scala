package com.fortysevendeg.ninecardslauncher.ui.collections

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v7.app.ActionBarActivity
import android.view.{Menu, MenuItem}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewPagerTweaks._
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.GetAppsRequest
import com.fortysevendeg.ninecardslauncher.modules.repository.{Collection, GetCollectionsRequest, GetCollectionsResponse}
import com.fortysevendeg.ninecardslauncher.ui.components.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{Ui, AppContext, Contexts}

import scala.concurrent.ExecutionContext.Implicits.global

class CollectionsDetailsActivity
  extends ActionBarActivity
  with Contexts[FragmentActivity]
  with Layout
  with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = AppContext(getApplicationContext)

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(layout)
    toolbar map setSupportActionBar
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)

    for {
      appsResponse <- appManagerServices.getApps(GetAppsRequest())
      GetCollectionsResponse(collections) <- repositoryServices.getCollections(GetCollectionsRequest(appsResponse.apps))
    } yield {
      runUi(
        (viewPager <~ vpAdapter(new CollectionsPagerAdapter(getSupportFragmentManager, collections))) ~
          (tabs <~
            stlViewPager(viewPager)<~
            stlOnPageChangeListener(new OnPageChangeListener {
              override def onPageScrollStateChanged(i: Int): Unit = {}
              override def onPageScrolled(i: Int, v: Float, i1: Int): Unit = {}
              override def onPageSelected(position: Int): Unit = {
                updateCollection(collections(position))
              }
            })) ~
          Ui { updateCollection(collections(viewPager.get.getCurrentItem)) }
      )
    }

  }

  def updateCollection(collection: Collection) = resGetDrawableIdentifier(collection.icon + "_detail") map {
      res => runUi(icon <~ ivSrc(res))
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
