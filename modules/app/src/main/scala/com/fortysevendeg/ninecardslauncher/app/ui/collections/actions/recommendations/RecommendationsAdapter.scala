package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Ui, ActivityContextWrapper}
import macroid.FullDsl._

case class RecommendationsAdapter(recommendations: Seq[RecommendedApp], clickListener: (String) => Ui[_])
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.Adapter[ViewHolderRecommendationsLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRecommendationsLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.recommendations_item, parent, false).asInstanceOf[ViewGroup]
    new ViewHolderRecommendationsLayoutAdapter(view, clickListener)
  }

  override def getItemCount: Int = recommendations.size

  override def onBindViewHolder(viewHolder: ViewHolderRecommendationsLayoutAdapter, position: Int): Unit = {
    val recommendation = recommendations(position)
    runUi(viewHolder.bind(recommendation, position))
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}
