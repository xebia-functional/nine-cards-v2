package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps

import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.view.View.{OnLongClickListener, OnClickListener}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.ScrollableManager
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FastScrollerListener
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, IterableApps}
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

case class AppsAdapter(
  var apps: IterableApps,
  clickListener: (App) => Unit,
  longClickListener: Option[(App) => Unit])
  (implicit val activityContext: ActivityContextWrapper, implicit val uiContext: UiContext[_])
  extends RecyclerView.Adapter[AppsIterableHolder]
    with FastScrollerListener {

  val heightItem = resGetDimensionPixelSize(R.dimen.height_app_item)

  override def getItemCount: Int = apps.count()

  override def onBindViewHolder(vh: AppsIterableHolder, position: Int): Unit = {
    runUi(vh.bind(apps.moveToPosition(position), position))
  }

  override def onCreateViewHolder(parent: ViewGroup, i: Int): AppsIterableHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.app_item, parent, false)
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        Option(v.getTag) foreach (tag => clickListener(apps.moveToPosition(Int.unbox(tag))))
      }
    })
    longClickListener foreach { listener =>
      view.setOnLongClickListener(new OnLongClickListener {
        override def onLongClick(v: View): Boolean = {
          Option(v.getTag) foreach (tag => listener(apps.moveToPosition(Int.unbox(tag))))
          true
        }
      })
    }
    AppsIterableHolder(view)
  }

  def getLayoutManager: GridLayoutManager = {
    val manager = new GridLayoutManager(activityContext.application, columnsLists) with ScrollableManager {
      override def canScrollVertically: Boolean = if (blockScroll) false else super.canScrollVertically
    }
    manager
  }

  def swapIterator(iter: IterableApps) = {
    apps = iter
    notifyDataSetChanged()
  }

  override def getHeight = (apps.count() / columnsLists) * heightItem

  override def getElement(position: Int): Option[String] = Option(apps.moveToPosition(position).name.substring(0, 1))
}

case class AppsIterableHolder(content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  def bind(app: App, position: Int)(implicit uiContext: UiContext[_]): Ui[_] =
    (icon <~ ivCardUri(app.imagePath, app.name)) ~
      (name <~ tvText(app.name)) ~
      (content <~ vTag2(position))

  override def findViewById(id: Int): View = content.findViewById(id)
}