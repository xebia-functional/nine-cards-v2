package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper
import macroid.FullDsl._

case class AppsAdapter(apps: Seq[AppCategorized])
  (implicit activityContext: ActivityContextWrapper, fragment: Fragment)
  extends RecyclerView.Adapter[ViewHolderAppLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAppLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.simple_item, parent, false).asInstanceOf[ViewGroup]
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        Option(v.getTag) foreach (tag => tag)
      }
    })
    new ViewHolderAppLayoutAdapter(view)
  }

  override def getItemCount: Int = apps.size

  override def onBindViewHolder(viewHolder: ViewHolderAppLayoutAdapter, position: Int): Unit = {
    val app = apps(position)
    runUi(viewHolder.bind(app, position))
  }

}
