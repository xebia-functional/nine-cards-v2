package com.fortysevendeg.ninecardslauncher.app.ui.collections

import java.io.File

import android.content.{ComponentName, Intent}
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View.OnClickListener
import android.view.{View, ViewGroup}
import android.widget.Toast
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageFragmentTweaks._
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardsIntentExtras._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper
import macroid.FullDsl._

import scala.util.{Failure, Success, Try}

class CollectionAdapter(collection: Collection, heightCard: Int)
    (implicit activityContext: ActivityContextWrapper, fragment: Fragment, theme: NineCardsTheme)
    extends RecyclerView.Adapter[ViewHolderCollectionAdapter] {

  override def onCreateViewHolder(parentViewGroup: ViewGroup, viewType: Int): ViewHolderCollectionAdapter = {
    val adapter = new CollectionLayoutAdapter(heightCard)
    adapter.content.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        Option(v.getTag) foreach (tag => execute(collection.cards(tag.toString.toInt).intent))
      }
    })
    new ViewHolderCollectionAdapter(adapter)
  }

  override def getItemCount: Int = collection.cards.size

  override def onBindViewHolder(viewHolder: ViewHolderCollectionAdapter, position: Int): Unit = {
    val card = collection.cards(position)
    runUi((viewHolder.icon <~ (
      if (new File(card.imagePath).exists()) {
        ivUri(fragment, card.imagePath)
      } else {
        ivSrc(R.drawable.ic_launcher) // TODO Create a new icon when the imagePath don't exist
      }
      )) ~
      (viewHolder.name <~ tvText(card.term)) ~
      (viewHolder.content <~ vTag(position.toString)))
  }

  private[this] def execute(intent: NineCardIntent) = {
    Log.d("9Cards", "action: " + intent.getAction)
    intent.getAction match {
      case `openApp` =>
        createIntentForApp(intent) map {
          i =>
            Try(activityContext.getOriginal.startActivity(i)) match {
              case Success(e) =>
              case Failure(ex) => goToGooglePlay(intent)
            }
        } getOrElse {
          tryLaunchPackage(intent)
        }
      case `openRecommendedApp` | `openSms` | `openPhone` | `openEmail` =>
        // TODO No implemented yet
      case _ => fragment.getActivity.startActivity(intent)
    }
  }

  private[this] def createIntentForApp(intent: NineCardIntent): Option[Intent] = for {
    packageName <- intent.extractPackageName()
    className <- intent.extractClassName()
  } yield {
      val intent = new Intent(Intent.ACTION_MAIN)
      intent.addCategory(Intent.CATEGORY_LAUNCHER)
      intent.setComponent(new ComponentName(packageName, className))
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
      intent
    }

  private[this] def goToGooglePlay(intent: NineCardIntent) =
    intent.extractPackageName() map {
      pn =>
        val intent = new Intent(Intent.ACTION_VIEW, Uri.parse(activityContext.application.getString(R.string.google_play_url, pn)))
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        Try (activityContext.getOriginal.startActivity(intent)) match {
          case Success(e) =>
          case Failure(ex) => Toast.makeText(activityContext.application, R.string.contactUsError, Toast.LENGTH_LONG).show()
        }
    } getOrElse {
      Toast.makeText(activityContext.application, R.string.contactUsError, Toast.LENGTH_LONG).show()
    }

  private[this] def tryLaunchPackage(intent: NineCardIntent) =
    intent.extractPackageName() map {
      pn =>
        Try(activityContext.getOriginal.startActivity(activityContext.application.getPackageManager.getLaunchIntentForPackage(pn))) match {
          case Success(e) =>
          case Failure(ex) => goToGooglePlay(intent)
        }
    } getOrElse {
      Toast.makeText(activityContext.application, R.string.contactUsError, Toast.LENGTH_LONG).show()
    }
}


