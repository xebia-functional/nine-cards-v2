package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper

case class ShortcutAdapter(shortcuts: Seq[Shortcut], clickListener: (Shortcut) => Unit)
  (implicit activityContext: ActivityContextWrapper)
  extends RecyclerView.Adapter[ViewHolderShortcutLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShortcutLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.shortcut_item, parent, false).asInstanceOf[ViewGroup]
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = Option(v.getTag) foreach (tag => clickListener(shortcuts(Int.unbox(tag))))
    })
    new ViewHolderShortcutLayoutAdapter(view)
  }

  override def getItemCount: Int = shortcuts.size

  override def onBindViewHolder(viewHolder: ViewHolderShortcutLayoutAdapter, position: Int): Unit = {
    val shortcut = shortcuts(position)
    viewHolder.bind(shortcut, position).run
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}
