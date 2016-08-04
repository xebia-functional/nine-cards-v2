package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps

import java.io.Closeable

import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FastScrollerListener
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.ScrollingLinearLayoutManager
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, IterableApps}
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class AppsAdapter(
  var apps: IterableApps,
  clickListener: (App) => Unit,
  longClickListener: Option[(View, App) => Unit])
  (implicit val activityContext: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.Adapter[AppsIterableHolder]
  with FastScrollerListener
  with Closeable {

  val columnsLists = 4

  val heightItem = resGetDimensionPixelSize(R.dimen.height_app_item)

  override def getItemCount: Int = apps.count()

  override def onBindViewHolder(vh: AppsIterableHolder, position: Int): Unit =
    vh.bind(apps.moveToPosition(position)).run

  override def onCreateViewHolder(parent: ViewGroup, i: Int): AppsIterableHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.app_item, parent, false)
    AppsIterableHolder(view, clickListener, longClickListener)
  }

  def getLayoutManager: GridLayoutManager = new ScrollingLinearLayoutManager(columnsLists)

  def swapIterator(iter: IterableApps) = {
    apps.close()
    apps = iter
    notifyDataSetChanged()
  }

  override def close() = apps.close()

  override def getHeightAllRows = apps.count() / columnsLists * getHeightItem

  override def getHeightItem: Int = heightItem

  override def getColumns: Int = columnsLists
}

case class AppsIterableHolder(
  content: ViewGroup,
  clickListener: (App) => Unit,
  longClickListener: Option[(View, App) => Unit])(implicit context: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  def bind(app: App): Ui[_] =
    (icon <~ ivSrcByPackageName(Some(app.packageName), app.name)) ~
      (name <~ tvText(app.name)) ~
      (content <~
        On.click {
          Ui(clickListener(app))
        } <~
        (longClickListener map { listener =>
          FuncOn.longClick { view: View =>
            icon foreach (listener(_, app))
            Ui(true)
          }
        } getOrElse Tweak.blank))

  override def findViewById(id: Int): View = content.findViewById(id)
}