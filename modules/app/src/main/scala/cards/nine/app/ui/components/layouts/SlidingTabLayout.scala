/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.components.layouts

import android.content.Context
import android.graphics.{Canvas, Color, Paint}
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, LayoutInflater, View}
import android.widget.{FrameLayout, HorizontalScrollView, LinearLayout, TextView}
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.R

/**
 * This file is part of Google Examples, you can see the code here
 * https://developer.android.com/samples/SlidingTabsBasic/index.html
 * We have translated the code to Scala using a functional approach
 */
class SlidingTabLayout(context: Context, attr: AttributeSet, defStyleAttr: Int)
    extends HorizontalScrollView(context, attr, defStyleAttr) {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  private var viewPager: Option[ViewPager] = None

  private var viewPagerPageChangeListener: Option[ViewPager.OnPageChangeListener] = None

  val tabStrip: SlidingTabStrip = new SlidingTabStrip(context)

  var lastScrollTo: Int = 0

  var defaultTextColor: Int =
    context.getResources.getColor(R.color.text_tab_color_default)

  var selectedTextColor: Int =
    context.getResources.getColor(R.color.text_tab_color_selected)

  val params = new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
  params.gravity = Gravity.BOTTOM

  setHorizontalScrollBarEnabled(false)
  setFillViewport(true)

  addView(tabStrip, params)

  def setTabStripColor(color: Int) = tabStrip.setColor(color)

  def setDefaultTextColor(color: Int) = defaultTextColor = color

  def setSelectedTextColor(color: Int) = selectedTextColor = color

  def setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) =
    viewPagerPageChangeListener = Some(listener)

  def setViewPager(viewPager: ViewPager) = {
    tabStrip.removeAllViews()
    this.viewPager = Option(viewPager)

    viewPager.addOnPageChangeListener(new InternalViewPagerListener())
    val adapter = viewPager.getAdapter
    (0 until adapter.getCount) foreach { i =>
      val tabTitleView = createDefaultTabView(i)
      tabTitleView.setText(adapter.getPageTitle(i))
      tabTitleView.setOnClickListener(new TabClickListener(i))
      tabStrip.addView(tabTitleView)
    }
    updateTabsColors(viewPager.getCurrentItem)
  }

  def createDefaultTabView(position: Int): TextView = {
    val textView = LayoutInflater
      .from(context)
      .inflate(R.layout.collections_detail_tab, javaNull)
      .asInstanceOf[TextView]
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
      if (selectedChild != javaNull && selectedChild.getMeasuredWidth != 0) {
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
        case text: TextView
            if Option(text.getTag).isDefined && text.getTag.equals(position.toString) =>
          text.setTextColor(selectedTextColor)
        case text: TextView => text.setTextColor(defaultTextColor)
        case _              =>
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
        val selectedOffset: Int =
          if (selectedTitle == javaNull) 0 else selectedTitle.getWidth
        val nextTitlePosition: Int = position + 1
        val nextTitle: View        = tabStrip.getChildAt(nextTitlePosition)
        val nextOffset: Int =
          if (nextTitle == javaNull) 0 else nextTitle.getWidth
        val extraOffset: Int =
          (0.5F * (positionOffset * (selectedOffset + nextOffset).toFloat)).toInt
        scrollToTab(position, extraOffset)
        viewPagerPageChangeListener foreach (_.onPageScrolled(
          position,
          positionOffset,
          positionOffsetPixels))
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

class SlidingTabStrip(context: Context, attr: AttributeSet, defStyleAttr: Int)
    extends LinearLayout(context, attr, defStyleAttr) {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  private var selectedPosition: Int  = 0
  private var selectionOffset: Float = .0f

  setWillNotDraw(false)

  val selectedIndicatorThickness =
    context.getResources.getDimensionPixelOffset(R.dimen.height_selected_tab)
  val selectedIndicatorPaint = new Paint
  setColor(Color.WHITE)

  def setColor(color: Int) = selectedIndicatorPaint.setColor(color)

  def onViewPagerPageChanged(position: Int, positionOffset: Float) {
    selectedPosition = position
    selectionOffset = positionOffset
    invalidate()
  }

  protected override def onDraw(canvas: Canvas) {
    val height: Int     = getHeight
    val childCount: Int = getChildCount

    if (childCount > 0) {
      val selectedTitle: View = getChildAt(selectedPosition)

      val initialLeft: Int  = selectedTitle.getLeft
      val initialRight: Int = selectedTitle.getRight

      val (left, right) =
        if (selectionOffset > 0f && selectedPosition < (getChildCount - 1)) {
          val nextTitle = getChildAt(selectedPosition + 1)
          val l =
            (selectionOffset * nextTitle.getLeft + (1.0f - selectionOffset) * initialLeft).toInt
          val r =
            (selectionOffset * nextTitle.getRight + (1.0f - selectionOffset) * initialRight).toInt
          (l, r)
        } else {
          (initialLeft, initialRight)
        }
      canvas
        .drawRect(left, height - selectedIndicatorThickness, right, height, selectedIndicatorPaint)
    }

  }

}
