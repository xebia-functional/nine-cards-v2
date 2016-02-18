package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps

import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.view.View.{OnClickListener, OnLongClickListener}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FastScrollerListener
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.ScrollingLinearLayoutManager
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, IterableApps}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
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

  override def onBindViewHolder(vh: AppsIterableHolder, position: Int): Unit =
    runUi(vh.bind(apps.moveToPosition(position), position))

  override def onCreateViewHolder(parent: ViewGroup, i: Int): AppsIterableHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.app_item, parent, false)
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        v.getPosition foreach (tag => clickListener(apps.moveToPosition(tag)))
      }
    })
    longClickListener foreach { listener =>
      view.setOnLongClickListener(new OnLongClickListener {
        override def onLongClick(v: View): Boolean = {
          v.getPosition foreach (tag => listener(apps.moveToPosition(tag)))
          true
        }
      })
    }
    AppsIterableHolder(view)
  }

  def getLayoutManager: GridLayoutManager =
    new GridLayoutManager(activityContext.application, columnsLists) with ScrollingLinearLayoutManager

  def swapIterator(iter: IterableApps) = {
    apps.close()
    apps = iter
    notifyDataSetChanged()
  }

  def close() = apps.close()

  override def getHeightAllRows = apps.count() / columnsLists * getHeightItem

  override def getHeightItem: Int = heightItem

  override def getColumns: Int = columnsLists
}

case class AppsIterableHolder(content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  def bind(app: App, position: Int): Ui[_] =
    (icon <~ ivCardUri(app.imagePath, app.name)) ~
      (name <~ tvText(app.name)) ~
      (content <~ vSetPosition(position))

  override def findViewById(id: Int): View = content.findViewById(id)
}