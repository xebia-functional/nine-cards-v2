package com.fortysevendeg.ninecardslauncher.app.ui.collections

import java.io.File

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View.OnClickListener
import android.view.{View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageFragmentTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}
import CollectionAdapter._

class CollectionAdapter(collection: Collection, heightCard: Int, listener: CollectionListener)
    (implicit activityContext: ActivityContextWrapper, fragment: Fragment)
    extends RecyclerView.Adapter[ViewHolderCollectionAdapter] {

  override def onCreateViewHolder(parentViewGroup: ViewGroup, viewType: Int): ViewHolderCollectionAdapter = {
    val adapter = new CollectionLayoutAdapter(heightCard)
    adapter.content.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = Option(v.getTag) map {
        tag =>
          runUi(listener(collection.cards(tag.toString.toInt)))
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

}

object CollectionAdapter {
  type CollectionListener = Card => Ui[_]
}

