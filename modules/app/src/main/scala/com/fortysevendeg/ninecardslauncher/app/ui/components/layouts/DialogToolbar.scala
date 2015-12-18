package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ContextWrapper, Ui}

class DialogToolbar(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends FrameLayout(context, attr, defStyleAttr)
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  LayoutInflater.from(context).inflate(R.layout.toolbar_dialog, this)

  lazy val toolbar = Option(findView(TR.actions_toolbar_widget))

  lazy val title = Option(findView(TR.actions_toolbar_title))

  lazy val extendedContent = Option(findView(TR.actions_toolbar_extended_content))

  def init(color: Int)(implicit contextWrapper: ContextWrapper) = {
    val closeDrawable = new PathMorphDrawable(
      defaultIcon = IconTypes.CLOSE,
      defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
      padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))
    (toolbar <~
      tbNavigationIcon(closeDrawable)) ~
    (this <~
      vBackgroundColor(color))
  }

  def changeToolbarHeight(height: Int): Ui[_] = toolbar <~ tbChangeHeightLayout(height)

  def addExtendedView(view: View): Ui[_] = extendedContent <~ vgAddView(view)

  def changeText(res: Int): Ui[_] = title <~ tvText(res)

  def navigationClickListener(click: (View) => Ui[_]): Ui[_] = toolbar <~ tbNavigationOnClickListener(click)

}
