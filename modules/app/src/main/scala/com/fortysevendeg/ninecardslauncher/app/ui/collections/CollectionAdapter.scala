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
import com.fortysevendeg.ninecardslauncher.app.ui.commons.LauncherExecutor
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardsIntentExtras._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper
import macroid.FullDsl._

import scala.util.{Failure, Success, Try}

class CollectionAdapter(collection: Collection, heightCard: Int)
  (implicit activityContext: ActivityContextWrapper, fragment: Fragment, theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderCollectionAdapter]
  with LauncherExecutor {

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
      })) ~
      (viewHolder.name <~ tvText(card.term)) ~
      (viewHolder.content <~ vTag(position.toString)))
  }

}


