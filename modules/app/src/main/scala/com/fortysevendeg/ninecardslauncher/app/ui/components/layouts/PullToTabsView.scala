package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.view.{LayoutInflater, MotionEvent, ViewGroup}
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{NineCardsTheme, PrimaryColor, SearchBackgroundColor, SearchIconsColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class PullToTabsView(context: Context)(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
  extends PullToDownView(context) {

  val heightTabs = resGetDimensionPixelSize(R.dimen.pulltotabs_max_height)

  val distanceChangeTabs = resGetDimensionPixelSize(R.dimen.pulltotabs_distance_change_tabs)

  val primaryColor = theme.get(PrimaryColor)

  val defaultColor = theme.get(SearchIconsColor)

  var tabs = slot[LinearLayout]

  var pullToTabsStatuses = PullToTabsStatuses()

  var tabsListener = PullToTabsListener()

  override def dispatchTouchEvent(event: MotionEvent): Boolean = {
    if (pullToDownStatuses.action == Pulling) {
      val displacedX = MotionEventCompat.getX(event, 0) + (distanceChangeTabs / 2)
      val pos = math.floor((displacedX - pullToDownStatuses.startX) / distanceChangeTabs).toInt
      val newPos = pullToTabsStatuses.calculatePosition(pos, getTabsCount)
      if (newPos != pullToTabsStatuses.selectedItem) {
        pullToTabsStatuses = pullToTabsStatuses.copy(selectedItem = newPos)
        (tabs <~ activateItem(newPos)).run
      }
    }
    super.dispatchTouchEvent(event)
  }

  override def drop(): Unit = {
    if (pullToTabsStatuses.wasTabChanged()) {
      tabsListener.changeItem(pullToTabsStatuses.selectedItem)
    }
    super.drop()
  }

  def getTabsCount = tabs map (_.getChildCount) getOrElse 0

  def linkTabsView(tabsView: Option[LinearLayout], start: Ui[_], end: Ui[_]): Ui[PullToTabsView] = {
    tabs = tabsView
    this <~
      pdvPullingListener(PullingListener(
        start = () => {
          pullToTabsStatuses = pullToTabsStatuses.start()
          ((tabs <~ vVisible <~ vY(-heightTabs)) ~ start).run
        },
        end = () => ((tabs <~ vGone) ~ end).run,
        scroll = (scroll: Int, close: Boolean) => (tabs <~ vY(-heightTabs + scroll)).run)
      )
  }

  def activateItem(item: Int): Transformer = Transformer {
    case tab: TabView if tab.isPosition(item) => tab.activate()
    case tab: TabView => tab.deactivate()
  }

  def clear(): Unit = (tabs <~ vgRemoveAllViews).run

  def addTabs(items: Seq[TabInfo], index: Option[Int] = None): Unit = {
    index foreach (i => pullToTabsStatuses = pullToTabsStatuses.copy(selectedItem = i))
    val views = items.zipWithIndex map {
      case (item, pos) => new TabView(item, pos, index contains pos)
    }
    val params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1)
    (tabs <~ vgAddViews(views, params)).run
  }

  class TabView(item: TabInfo, pos: Int, selected: Boolean)
    extends LinearLayout(context)
    with TypedFindView {

    LayoutInflater.from(context).inflate(R.layout.tab_item, this)

    lazy val line = findView(TR.tab_item_line)

    lazy val icon = findView(TR.tab_item_icon)

    lazy val name = findView(TR.tab_item_name)

    val backgroundColor = getColorDark(theme.get(SearchBackgroundColor), 0.05f)

    ((this <~ vBackgroundColor(backgroundColor)) ~
      (icon <~ ivSrc(item.drawable)) ~
      (name <~ tvText(item.name)) ~
      (if (selected) {
        activate()
      } else {
        deactivate()
      }) ~
      (this <~
        vSetPosition(pos) <~
        On.click {
          Ui {
            pullToTabsStatuses = pullToTabsStatuses.copy(selectedItem = pos)
            tabsListener.changeItem(pullToTabsStatuses.selectedItem)
          } ~ (tabs <~ activateItem(pos))
        })).run

    def activate(): Ui[_] =
      (icon <~ tivDefaultColor(primaryColor)) ~
        (name <~ tvColor(primaryColor)) ~
        (line <~ vBackgroundColor(primaryColor))

    def deactivate(): Ui[_] =
      (icon <~ tivDefaultColor(defaultColor)) ~
        (name <~ tvColor(defaultColor)) ~
        (line <~ vBlankBackground)

  }

}

case class PullToTabsListener(changeItem: (Int) => Unit = (_) => ())

case class PullToTabsStatuses(
  selectedItem: Int = 0,
  selectedItemWhenStartPulling: Int = 0) {

  def start() = copy(selectedItemWhenStartPulling = selectedItem)

  def wasTabChanged() = selectedItemWhenStartPulling != selectedItem

  def calculatePosition(pos: Int, max: Int) = {
    val min = math.max(pos + selectedItemWhenStartPulling, 0)
    math.min(min, max - 1)
  }
}

trait PullToTabsViewStyles {

  def tabContentStyles(paddingRight: Int = 0)(implicit context: ContextWrapper): Tweak[LinearLayout] = {
    val heightTabs = resGetDimensionPixelSize(R.dimen.pulltotabs_max_height)
    vContentSizeMatchWidth(heightTabs) +
      vPadding(paddingRight = paddingRight) +
      vGone
  }

}

case class TabInfo(drawable: Int, name: String)
