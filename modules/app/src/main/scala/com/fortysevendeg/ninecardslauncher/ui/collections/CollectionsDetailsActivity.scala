package com.fortysevendeg.ninecardslauncher.ui.collections

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.ActionBarActivity
import android.view.{Menu, MenuItem}
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.macroid.extras.ViewPagerTweaks._
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.GetAppsRequest
import com.fortysevendeg.ninecardslauncher.modules.repository.GetCollectionsRequest
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{AppContext, Contexts}
import macroid.FullDsl._

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
      collectionResponse <- repositoryServices.getCollections(GetCollectionsRequest(appsResponse.apps))
    } yield {
      runUi(viewPager <~ vpAdapter(new CollectionsPagerAdapter(getSupportFragmentManager, collectionResponse.collections)))
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
