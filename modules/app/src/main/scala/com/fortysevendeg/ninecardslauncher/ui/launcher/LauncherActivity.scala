package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.{AppItem, GetAppsRequest}
import com.fortysevendeg.ninecardslauncher.modules.repository.GetCollectionsRequest
import com.fortysevendeg.ninecardslauncher.ui.commons.AsyncImageActivityTweaks._
import com.fortysevendeg.ninecardslauncher.ui.launcher.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{AppContext, Contexts, Transformer}

import scala.concurrent.ExecutionContext.Implicits.global

class LauncherActivity
  extends Activity
  with Contexts[Activity]
  with Layout
  with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = AppContext(getApplicationContext)

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(content)

    for {
      appsResponse <- appManagerServices.getApps(GetAppsRequest())
      collectionResponse <- repositoryServices.getCollections(GetCollectionsRequest(appsResponse.apps))
    } yield {
      runUi(
        (workspaces <~ lwsData(collectionResponse.collections)) ~
          (appDrawerBar <~ fillAppDrawer(appsResponse.apps)))
    }

  }

  def fillAppDrawer(appItems: Seq[AppItem]) = Transformer {
    case i: ImageView if Option(i.getTag(R.id.`type`)).isDefined && i.getTag(R.id.`type`).equals(AppDrawer.app) => {
      val r = scala.util.Random
      val app = appItems(r.nextInt(appItems.length))
      i <~ ivUri(app.imagePath)
    }
  }

}
