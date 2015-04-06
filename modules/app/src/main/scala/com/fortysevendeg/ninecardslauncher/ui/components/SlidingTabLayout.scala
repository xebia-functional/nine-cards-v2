package com.fortysevendeg.ninecardslauncher.ui.components

import android.content.Context
import android.graphics.Color
import android.support.v4.view.ViewPager
import android.util.{AttributeSet, TypedValue}
import android.view.View.OnClickListener
import android.view.{Gravity, View, ViewGroup}
import android.widget.{HorizontalScrollView, TextView}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContext, Tweak, AppContext}
import macroid.FullDsl._

/**
 * Inspired in https://developer.android.com/samples/SlidingTabsBasic/index.html
 */
class SlidingTabLayout(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext, activityContext: ActivityContext)
  extends HorizontalScrollView(context, attr, defStyleAttr) {

  def this(context: Context)(implicit appContext: AppContext, activityContext: ActivityContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext, activityContext: ActivityContext) = this(context, attr, 0)

  private val paddingDefault: Int = resGetDimensionPixelSize(R.dimen.padding_default)

  private val paddingLarge: Int = resGetDimensionPixelSize(R.dimen.padding_large)

  private var viewPager: Option[ViewPager] = None

  private var viewPagerPageChangeListener: Option[ViewPager.OnPageChangeListener] = None

  val tabStrip: SlidingTabStrip = new SlidingTabStrip(context)

  var lastScrollTo: Int = 0

  setHorizontalScrollBarEnabled(false)
  setFillViewport(true)

  addView(tabStrip, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

  def setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) = viewPagerPageChangeListener = Some(listener)

  def setViewPager(viewPager: Option[ViewPager]) {
    tabStrip.removeAllViews()
    this.viewPager = viewPager
    viewPager map {
      vp =>
        vp.setOnPageChangeListener(new InternalViewPagerListener())
        val adapter = vp.getAdapter
        (0 until adapter.getCount) map {
          i =>
            val tabTitleView = createDefaultTabView()
            tabTitleView.setText(adapter.getPageTitle(i))
            tabTitleView.setOnClickListener(new TabClickListener(i))
            tabStrip.addView(tabTitleView)
        }
    }
  }

  def createDefaultTabView(): TextView = getUi(
    w[TextView] <~
      tvColor(Color.WHITE) <~
      tvGravity(Gravity.CENTER) <~
      tvSizeResource(R.dimen.text_tabs) <~
      tvAllCaps <~
      vPaddings(paddingLeftRight = paddingLarge, paddingTopBottom = paddingDefault) <~
      Tweak[TextView] {
        view =>
          val outValue = new TypedValue()
          getContext.getTheme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
          view.setBackgroundResource(outValue.resourceId)
      }
  )

  override def onAttachedToWindow() = {
    super.onAttachedToWindow()
    viewPager map (vp => scrollToTab(vp.getCurrentItem(), 0))
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

  private class InternalViewPagerListener extends ViewPager.OnPageChangeListener {
    private var mScrollState: Int = 0

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
        viewPagerPageChangeListener map (_.onPageScrolled(position, positionOffset, positionOffsetPixels))
      }
    }

    def onPageScrollStateChanged(state: Int) {
      mScrollState = state
      viewPagerPageChangeListener map (_.onPageScrollStateChanged(state))
    }

    def onPageSelected(position: Int) {
      if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
        tabStrip.onViewPagerPageChanged(position, 0f)
        scrollToTab(position, 0)
      }
      viewPagerPageChangeListener map (_.onPageSelected(position))
    }
  }

  private class TabClickListener(position: Int) extends OnClickListener {
    def onClick(v: View) = viewPager map (_.setCurrentItem(position))
  }

}


