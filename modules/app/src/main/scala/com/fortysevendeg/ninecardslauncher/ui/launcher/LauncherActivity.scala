package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.{AppItem, GetAppsRequest}
import com.fortysevendeg.ninecardslauncher.modules.repository.GetCollectionsRequest
import com.fortysevendeg.ninecardslauncher.ui.commons.AsyncImageActivityTweaks._
import com.fortysevendeg.ninecardslauncher.ui.launcher.LauncherWorkSpacesTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{Ui, AppContext, Contexts, Transformer}
import Snails._

import scala.concurrent.ExecutionContext.Implicits.global

class LauncherActivity
  extends Activity
  with Contexts[Activity]
  with Layout
  with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = AppContext(getApplicationContext)

  val SelectedPageDefault = 1

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(content)

    for {
      appsResponse <- appManagerServices.getApps(GetAppsRequest())
      collectionResponse <- repositoryServices.getCollections(GetCollectionsRequest(appsResponse.apps))
    } yield {
      runUi(
        (workspaces <~
          lwsData(collectionResponse.collections, SelectedPageDefault) <~
          lwsAddPageChangedObserver(currentPage => runUi(pager <~ reloadPager(currentPage)))) ~
          (appDrawerBar <~ fillAppDrawer(appsResponse.apps)) ~
          Ui(createPager(SelectedPageDefault)))
    }

  }

  def createPager(posActivated: Int) = workspaces map {
    ws =>
      val pagerViews = 0 until ws.getWorksSpacesCount map {
        position =>
          val view = pagination(position)
          view.setActivated(posActivated == position)
          view
      }
      runUi(pager <~ vgAddViews(pagerViews))
  }

  def fillAppDrawer(appItems: Seq[AppItem]) = Transformer {
    case i: ImageView if Option(i.getTag(R.id.`type`)).isDefined && i.getTag(R.id.`type`).equals(AppDrawer.app) => {
      val r = scala.util.Random
      val app = appItems(r.nextInt(appItems.length))
      i <~ ivUri(app.imagePath)
    }
  }

  def reloadPager(currentPage: Int) = Transformer {
    case i: ImageView if Option(i.getTag).isDefined && i.getTag.equals(currentPage.toString) => i <~ vActivated(true) <~~ pagerAppear()
    case i: ImageView => i <~ vActivated(false)
  }

}
