package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.{FrameLayout, ImageView, TextView}
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak}

class FabItemMenu(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(context, attr, defStyleAttr)
  with FabItemMenuStyles {

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  val content = LayoutInflater.from(context).inflate(R.layout.fab_item, null)

  val title = Option(content.findViewById(R.id.fab_title).asInstanceOf[TextView])

  val icon = Option(content.findViewById(R.id.fab_icon).asInstanceOf[ImageView])

  addView(content)

  runUi(icon <~ fabStyle)

}

trait FabItemMenuStyles {

  def fabStyle(implicit context: ContextWrapper): Tweak[ImageView] = Lollipop ifSupportedThen {
    vElevation(resGetDimension(R.dimen.elevation_fab_button)) + vCircleOutlineProvider()
  } getOrElse Tweak.blank

}

