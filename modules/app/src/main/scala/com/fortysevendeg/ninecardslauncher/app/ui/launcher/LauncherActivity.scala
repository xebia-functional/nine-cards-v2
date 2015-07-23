package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.modules.persistent.impl.PersistentServicesComponentImpl
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ActivityResult._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageActivityTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Snails._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{Contexts, Transformer, Ui}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.concurrent.Task

class LauncherActivity
  extends ActionBarActivity
  with Contexts[ActionBarActivity]
  with ContextSupportProvider
  with PersistentServicesComponentImpl
  with Layout {

  lazy val di = new Injector

  // TODO We select the page in ViewPager with collections. In the future this will be a user preference
  val selectedPageDefault = 1

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    Task.fork(di.userProcess.register).resolveAsync()
    setContentView(content)
    generateCollections()
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    (requestCode, resultCode) match {
      case (request, result) if result == Activity.RESULT_OK && request == wizard =>
        generateCollections()
      case _ =>
    }
  }

  private def generateCollections() = Task.fork(di.collectionProcess.getCollections).resolveAsyncUi(
    (collections: Seq[Collection]) => {
      // Check if there are collections in DB, if there aren't we go to wizard
      if (collections.isEmpty) {
        goToWizard()
      } else {
        (workspaces <~
          lwsData(collections, selectedPageDefault) <~
          lwsAddPageChangedObserver(currentPage => runUi(pager <~ reloadPager(currentPage)))) ~
          (appDrawerBar <~ fillAppDrawer(collections)) ~
          Ui(createPager(selectedPageDefault))
      }
    },
    (ex: Throwable) => goToWizard()
  )

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

  private[this] def goToWizard(): Ui[_] = Ui {
    val wizardIntent = new Intent(this, classOf[WizardActivity])
    startActivityForResult(wizardIntent, wizard)
  }

}
