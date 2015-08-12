package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.graphics.Color
import android.support.v4.view.ViewPager
import android.util.{AttributeSet, TypedValue}
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams._
import android.view.{LayoutInflater, Gravity, View}
import android.widget.{FrameLayout, HorizontalScrollView, TextView}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Transformer, ActivityContextWrapper, Tweak}
import macroid.FullDsl._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils

/**
 * Inspired in https://developer.android.com/samples/SlidingTabsBasic/index.html
 */
class SlidingTabLayout(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends HorizontalScrollView(context, attr, defStyleAttr) {

  def this(context: Context) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  private var viewPager: Option[ViewPager] = None

  private var viewPagerPageChangeListener: Option[ViewPager.OnPageChangeListener] = None

  val tabStrip: SlidingTabStrip = new SlidingTabStrip(context)

  var lastScrollTo: Int = 0

  var defaultTextColor: Int = context.getResources.getColor(R.color.text_tab_color_default)

  var selectedTextColor: Int = context.getResources.getColor(R.color.text_tab_color_selected)

  val params = new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
  params.gravity = Gravity.BOTTOM

  setHorizontalScrollBarEnabled(false)
  setFillViewport(true)

  addView(tabStrip, params)

  def setTabStripColor(color: Int) = tabStrip.setColor(color)

  def setDefaultTextColor(color: Int) = defaultTextColor = color

  def setSelectedTextColor(color: Int) = selectedTextColor = color

  def setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) = viewPagerPageChangeListener = Some(listener)

  def setViewPager(viewPager: ViewPager) = {
    tabStrip.removeAllViews()
    this.viewPager = Option(viewPager)

    viewPager.addOnPageChangeListener(new InternalViewPagerListener())
    val adapter = viewPager.getAdapter
    (0 until adapter.getCount) foreach {
      i =>
        val tabTitleView = createDefaultTabView(i)
        tabTitleView.setText(adapter.getPageTitle(i))
        tabTitleView.setOnClickListener(new TabClickListener(i))
        tabStrip.addView(tabTitleView)
    }
    updateTabsColors(viewPager.getCurrentItem)
  }

  def createDefaultTabView(position: Int): TextView = {
    val textView = LayoutInflater.from(context).inflate(R.layout.collections_detail_tab, null).asInstanceOf[TextView]
    textView.setTextColor(defaultTextColor)
    textView.setTag(position.toString)
    textView
  }

  override def onAttachedToWindow() = {
    super.onAttachedToWindow()
    viewPager foreach (vp => scrollToTab(vp.getCurrentItem, 0))
  }

  private def scrollToTab(tabIndex: Int, positionOffset: Int) = {
    val tabStripChildCount = tabStrip.getChildCount
    if (tabStripChildCount != 0 && tabIndex > 0 || tabIndex < tabStripChildCount) {
      val selectedChild = tabStrip.getChildAt(tabIndex)
      if (selectedChild != null && selectedChild.getMeasuredWidth != 0) {
        val targetScrollX = ((positionOffset + selectedChild.getLeft) - getWidth / 2) + selectedChild.getWidth / 2
        if (targetScrollX != lastScrollTo) {
          scrollTo(targetScrollX, 0)
          lastScrollTo = targetScrollX
        }
      }
    }
  }

  private def updateTabsColors(position: Int) = {
    (0 until tabStrip.getChildCount) foreach {
      tabStrip.getChildAt(_) match {
        case text: TextView if Option(text.getTag).isDefined && text.getTag.equals(position.toString) =>
          text.setTextColor(selectedTextColor)
        case text: TextView => text.setTextColor(defaultTextColor)
        case _ =>
      }
    }
  }

  private class InternalViewPagerListener extends ViewPager.OnPageChangeListener {
    private var scrollState: Int = 0

    def onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
      val tabStripChildCount: Int = tabStrip.getChildCount
      if (tabStripChildCount != 0 && position > 0 || position < tabStripChildCount) {
        tabStrip.onViewPagerPageChanged(position, positionOffset)
        val selectedTitle: View = tabStrip.getChildAt(position)
        val selectedOffset: Int = if (selectedTitle == null) 0 else selectedTitle.getWidth
        val nextTitlePosition: Int = position + 1
        val nextTitle: View = tabStrip.getChildAt(nextTitlePosition)
        val nextOffset: Int = if (nextTitle == null) 0 else nextTitle.getWidth
        val extraOffset: Int = (0.5F * (positionOffset * (selectedOffset + nextOffset).toFloat)).toInt
        scrollToTab(position, extraOffset)
        viewPagerPageChangeListener foreach (_.onPageScrolled(position, positionOffset, positionOffsetPixels))
      }
    }

    def onPageScrollStateChanged(state: Int) = {
      scrollState = state
      viewPagerPageChangeListener foreach (_.onPageScrollStateChanged(state))
    }

    def onPageSelected(position: Int) = {
      updateTabsColors(position)
      if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
        tabStrip.onViewPagerPageChanged(position, 0f)
        scrollToTab(position, 0)
      }
      viewPagerPageChangeListener foreach (_.onPageSelected(position))
    }
  }

  private class TabClickListener(position: Int) extends OnClickListener {
    def onClick(v: View) = viewPager foreach (_.setCurrentItem(position))
  }

}

object SlidingTabLayoutTweaks {
  type W = SlidingTabLayout

  def stlViewPager(viewPager: Option[ViewPager]): Tweak[W] = Tweak[W](viewPager foreach _.setViewPager)

  def stlDefaultTextColor(color: Int): Tweak[W] = Tweak[W](_.setDefaultTextColor(color))

  def stlSelectedTextColor(color: Int): Tweak[W] = Tweak[W](_.setSelectedTextColor(color))

  def stlTabStripColor(color: Int): Tweak[W] = Tweak[W](_.setTabStripColor(color))

  def stlOnPageChangeListener(listener: ViewPager.OnPageChangeListener): Tweak[W] = Tweak[W](_.setOnPageChangeListener(listener))
}
