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
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{NineCardsTheme, SearchIconsColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak}

class PullToTabsView(context: Context)(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
  extends PullToDownView(context) {

  val heightTabs = resGetDimensionPixelSize(R.dimen.pulltotabs_height)

  var tabs = slot[LinearLayout]

  runUi(
    this <~ vGlobalLayoutListener(view => {
      this <~
        pdvListener(PullToDownListener(
          startPulling = () => runUi(tabs <~ vVisible <~ vY(-heightTabs)),
          endPulling = () => runUi(tabs <~ vGone),
          scroll = (scroll: Int, close: Boolean) => runUi(tabs <~ vY(-heightTabs + scroll)))
        )
    }))

  def clear = runUi(tabs <~ vgRemoveAllViews)

  def addTabs(items: Seq[TabInfo]) = {
    val views = items map { item =>
      new TabView(item)
    }
    val params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1)
    runUi(tabs <~ vgAddViews(views, params))
  }

  class TabView(item: TabInfo)
    extends LinearLayout(context)
    with TypedFindView {

    LayoutInflater.from(context).inflate(R.layout.tab_item, this)

    lazy val icon = findView(TR.tab_item_icon)

    lazy val name = findView(TR.tab_item_name)

    runUi(
      (icon <~
        tivDefaultColor(theme.get(SearchIconsColor)) <~
        ivSrc(item.drawable)) ~
        (name <~ tvText(item.name)))
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
