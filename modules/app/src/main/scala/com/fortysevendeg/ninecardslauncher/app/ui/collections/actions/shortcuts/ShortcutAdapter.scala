package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView, R}
import macroid._

case class ShortcutAdapter(shortcuts: Seq[Shortcut])
  (implicit activityContext: ActivityContextWrapper, presenter: ShortcutPresenter)
  extends RecyclerView.Adapter[ViewHolderShortcutLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShortcutLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.shortcut_item, parent, false).asInstanceOf[ViewGroup]
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = Option(v.getTag) foreach (tag => presenter.configureShortcut(shortcuts(Int.unbox(tag))))
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

case class ViewHolderShortcutLayoutAdapter(content: ViewGroup)(implicit context: ActivityContextWrapper)
  extends RecyclerView.ViewHolder(content)
    with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  def bind(shortcut: Shortcut, position: Int): Ui[_] =
    (icon <~ (shortcut.icon map ivSrc getOrElse Tweak.blank)) ~
      (name <~ tvText(shortcut.title)) ~
      (content <~ vTag(position))

  override def findViewById(id: Int): View = content.findViewById(id)

}