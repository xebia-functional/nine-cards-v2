package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.{FrameLayout, ImageView}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

class WorkspaceItemMenu(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends FrameLayout(context, attr, defStyleAttr)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  LayoutInflater.from(context).inflate(R.layout.workspace_item_menu, this)

  private[this] val title = Option(findView(TR.workspace_title))

  val icon = Option(findView(TR.workspace_icon))

  (icon <~ fabStyle).run

  def populate(backgroundColor: Int, res: Int, text: Int): Ui[Any] =
    (title <~ tvText(text)) ~
      (icon <~
        ivSrc(res) <~
        (Lollipop ifSupportedThen {
          vBackgroundColor(backgroundColor)
        } getOrElse {
          val drawable = new ShapeDrawable(new OvalShape)
          drawable.getPaint.setColor(backgroundColor)
          vBackground(drawable)
        }))

  private[this] def fabStyle: Tweak[ImageView] = Lollipop ifSupportedThen {
    vElevation(resGetDimension(R.dimen.elevation_fab_button)) + vCircleOutlineProvider()
  } getOrElse Tweak.blank

}

