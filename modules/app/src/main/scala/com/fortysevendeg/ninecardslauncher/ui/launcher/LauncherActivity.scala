package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.commons.ContentResolverWrapperImpl
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.repository.repositories.{GeoInfoRepository, CollectionRepository, CardRepository, CacheCategoryRepository}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.impl.PersistenceServicesImpl
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection
import com.fortysevendeg.ninecardslauncher.ui.commons.ActivityResult._
import com.fortysevendeg.ninecardslauncher.ui.commons.AsyncImageActivityTweaks._
import com.fortysevendeg.ninecardslauncher.ui.launcher.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.ui.launcher.Snails._
import com.fortysevendeg.ninecardslauncher.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts, Transformer, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

class LauncherActivity
  extends Activity
  with Contexts[Activity]
  with Layout
  with ComponentRegistryImpl {

  override lazy val contextProvider: ContextWrapper = activityContextWrapper

  private lazy val contentResolverWrapper = new ContentResolverWrapperImpl(contextProvider.application.getContentResolver)

  private lazy val persistenceServices = new PersistenceServicesImpl(
    new CacheCategoryRepository(contentResolverWrapper),
    new CardRepository(contentResolverWrapper),
    new CollectionRepository(contentResolverWrapper),
    new GeoInfoRepository(contentResolverWrapper))

  // TODO We select the page in ViewPager with collections. In the future this will be a user preference
  val SelectedPageDefault = 1

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    userServices.register()
    setContentView(content)
    generateCollections
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    (requestCode, resultCode) match {
      case (request, result) if result == Activity.RESULT_OK && request == Wizard =>
        generateCollections
      case _ =>
    }
  }

  private def generateCollections =
    for {
      FetchCollectionsResponse(collections) <- persistenceServices.fetchCollections(FetchCollectionsRequest())
    } yield {
      // Check if there are collections in DB, if there aren't we go to wizard
      if (collections.isEmpty) {
        val wizardIntent = new Intent(this, classOf[WizardActivity])
        startActivityForResult(wizardIntent, Wizard)
      } else {
        runUi(
          (workspaces <~
            lwsData(collections, SelectedPageDefault) <~
            lwsAddPageChangedObserver(currentPage => runUi(pager <~ reloadPager(currentPage)))) ~
            (appDrawerBar <~ fillAppDrawer(collections)) ~
            Ui(createPager(SelectedPageDefault)))
      }
    }

  private def createPager(posActivated: Int) = workspaces map {
    ws =>
      val pagerViews = 0 until ws.getWorksSpacesCount map {
        position =>
          val view = pagination(position)
          view.setActivated(posActivated == position)
          view
      }
      runUi(pager <~ vgAddViews(pagerViews))
  }

  // TODO We add app randomly, in the future we should get the app from repository
  private def fillAppDrawer(collections: Seq[Collection]) = Transformer {
    case i: ImageView if Option(i.getTag(R.id.`type`)).isDefined && i.getTag(R.id.`type`).equals(AppDrawer.app) => {
      val r = scala.util.Random
      val randomCollection = collections(r.nextInt(collections.length))
      val randomCard = randomCollection.cards(r.nextInt(randomCollection.cards.length))
      i <~ ivUri(randomCard.imagePath)
    }
  }

  private def reloadPager(currentPage: Int) = Transformer {
    case i: ImageView if Option(i.getTag).isDefined && i.getTag.equals(currentPage.toString) => i <~ vActivated(true) <~~ pagerAppear
    case i: ImageView => i <~ vActivated(false)
  }

}
