package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.app.Activity
import android.os.Bundle
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.GetAppsRequest
import com.fortysevendeg.ninecardslauncher.modules.repository.GetCollectionsRequest
import macroid.{AppContext, Contexts}
import macroid.FullDsl._
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
      runUi(workspaces <~ lwsData(collectionResponse.collections))
    }

  }

}
