package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.view.{LayoutInflater, ViewGroup}
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{NineCardsTheme, SearchIconsColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ContextWrapper, Transformer, Tweak, Ui}

class PullToTabsView(context: Context)(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
  extends PullToDownView(context) {

  val heightTabs = resGetDimensionPixelSize(R.dimen.pulltotabs_height)

  val primaryColor = resGetColor(R.color.primary)

  val defaultColor = theme.get(SearchIconsColor)

  var tabs = slot[LinearLayout]

  var selectedItem = 0

  def linkTabsView(tabsView: Option[LinearLayout], start: Ui[_], end: Ui[_]): Ui[PullToTabsView] = {
    tabs = tabsView
    this <~
      pdvListener(PullToDownListener(
        startPulling = () => runUi((tabs <~ vVisible <~ vY(-heightTabs)) ~ start),
        endPulling = () => runUi((tabs <~ vGone) ~ end),
        scroll = (scroll: Int, close: Boolean) => runUi(tabs <~ vY(-heightTabs + scroll)))
      )
  }

  def activate(item: Int): Transformer = Transformer {
    case tab: TabView if tab.isPosition(item) => tab.activate()
    case tab: TabView => tab.deactivate()
  }

  def clear: Unit = runUi(tabs <~ vgRemoveAllViews)

  def addTabs(items: Seq[TabInfo], index: Option[Int] = None): Unit = {
    val views = items.zipWithIndex map {
      case (item, pos) => new TabView(item, pos, index contains pos)
    }
    val params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1)
    runUi(tabs <~ vgAddViews(views, params))
  }

  class TabView(item: TabInfo, pos: Int, selected: Boolean)
    extends LinearLayout(context)
    with TypedFindView {

    LayoutInflater.from(context).inflate(R.layout.tab_item, this)

    lazy val icon = findView(TR.tab_item_icon)

    lazy val name = findView(TR.tab_item_name)

    runUi(
      (icon <~ ivSrc(item.drawable)) ~
        (name <~ tvText(item.name)) ~
        (if (selected) {
          activate()
        } else {
          deactivate()
        }) ~
        (this <~ vSetPosition(pos)))

    def activate() =
      (icon <~ tivDefaultColor(primaryColor)) ~
        (name <~ tvColor(primaryColor))

    def deactivate() =
      (icon <~ tivDefaultColor(defaultColor)) ~
        (name <~ tvColor(defaultColor))

  }

}

trait PullToTabsViewStyles {

  def tabContentStyles(implicit context: ContextWrapper): Tweak[LinearLayout] = {
    val heightTabs = resGetDimensionPixelSize(R.dimen.pulltotabs_height)
    vContentSizeMatchWidth(heightTabs) +
      vGone
  }

}

case class TabInfo(drawable: Int, name: String)
