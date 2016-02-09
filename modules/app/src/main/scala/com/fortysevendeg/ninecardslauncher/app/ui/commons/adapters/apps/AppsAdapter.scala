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
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.CounterStatuses
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

  var statuses = CounterStatuses(count = apps.count())

  override def getItemCount: Int = apps.count()

  override def onBindViewHolder(vh: AppsIterableHolder, position: Int): Unit = {
    runUi(vh.bind(apps.moveToPosition(position), position, statuses.isActive(position)))
  }

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
    statuses = statuses.reset(count = getItemCount)
  }

  def close() = apps.close()

  override def getHeightAllRows = apps.count() / columnsLists * getHeightItem

  override def getHeightItem: Int = heightItem

  override def getColumns: Int = columnsLists

  override def activeItems(f: Int, c: Int): Unit = statuses = statuses.copy(from = f, count = c)

  override def inactiveItems(): Unit = statuses = statuses.reset(count = getItemCount)
}

case class AppsIterableHolder(content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  val default = 1f

  val unselected = resGetInteger(R.integer.appdrawer_alpha_unselected_item_percentage).toFloat / 100

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  def bind(app: App, position: Int, active: Boolean): Ui[_] =
    (content <~ (if (active) vAlpha(default) else vAlpha(unselected))) ~
      (icon <~ ivCardUri(app.imagePath, app.name)) ~
      (name <~ tvText(app.name)) ~
      (content <~ vSetPosition(position))

  override def findViewById(id: Int): View = content.findViewById(id)
}