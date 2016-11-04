package cards.nine.app.ui.commons.adapters.apps

import java.io.Closeable

import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.components.layouts.FastScrollerListener
import cards.nine.app.ui.components.widgets.ScrollingLinearLayoutManager
import cards.nine.app.ui.preferences.commons.{FontSize, IconsSize}
import cards.nine.models.{ApplicationData, NineCardsTheme}
import cards.nine.models.types.theme.DrawerTextColor
import cards.nine.process.device.models.{EmptyIterableApps, IterableApps}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import cards.nine.app.ui.collections.actions.apps.AppsFragment._

case class AppsSelectionAdapter(
  var apps: IterableApps,
  clickListener: (ApplicationData) => Unit)
  (implicit val activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[AppsSelectedIterableHolder]
  with FastScrollerListener
  with Closeable {

  val columnsLists = 4

  val heightItem = resGetDimensionPixelSize(R.dimen.height_app_item)

  override def getItemCount: Int = apps.count()

  override def onBindViewHolder(vh: AppsSelectedIterableHolder, position: Int): Unit =
    vh.bind(apps.moveToPosition(position)).run

  override def onCreateViewHolder(parent: ViewGroup, i: Int): AppsSelectedIterableHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.app_item, parent, false)
    AppsSelectedIterableHolder(view, clickListener)
  }

  def getLayoutManager: GridLayoutManager = new ScrollingLinearLayoutManager(columnsLists)

  def swapIterator(iter: IterableApps) = {
    apps.close()
    apps = iter
    notifyDataSetChanged()
  }

  def clear() = {
    apps.close()
    apps = new EmptyIterableApps()
    notifyDataSetChanged()
  }

  override def close() = apps.close()

  override def getHeightAllRows = apps.count() / columnsLists * getHeightItem

  override def getHeightItem: Int = heightItem

  override def getColumns: Int = columnsLists
}

case class AppsSelectedIterableHolder(
  content: ViewGroup,
  clickListener: (ApplicationData) => Unit)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  def bind(app: ApplicationData): Ui[_] =
    (icon <~ vResize(IconsSize.getIconApp) <~ ivSrcByPackageName(Some(app.packageName), app.name)) ~
      (name <~ tvSizeResource(FontSize.getSizeResource) <~ tvText(if (appStatuses.selectedPackages.contains(app.packageName)) "Selected" else app.name) + tvColor(theme.get(DrawerTextColor))) ~
      (content <~
        On.click {
          Ui(clickListener(app))
        })

  override def findViewById(id: Int): View = content.findViewById(id)
}