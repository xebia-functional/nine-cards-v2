package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.CircleDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.{ContactView, AppsView, ContentView}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.theme.models.{SearchIconsColor, NineCardsTheme, SearchBackgroundColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails.SwipeAnimatedDrawerViewSnails._
import macroid.{Tweak, Ui, ActivityContextWrapper}
import macroid.FullDsl._

class SwipeAnimatedDrawerView (context: Context, attrs: AttributeSet, defStyle: Int)
  (implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme)
  extends FrameLayout(context, attrs, defStyle)
  with TypedFindView { self =>

  def this(context: Context)(implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme) =
    this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet)(implicit contextWrapper: ActivityContextWrapper, theme: NineCardsTheme) =
    this(context, attrs, 0)

  val colorForeground = getColorDark(theme.get(SearchBackgroundColor), 0.05f)

  val colorBackground = theme.get(SearchBackgroundColor)

  val circle = new CircleDrawable(colorBackground)

  LayoutInflater.from(context).inflate(R.layout.swipe_animation_drawer_layout, self)

  lazy val root = Option(findView(TR.swipe_animation_root))

  lazy val rippleView = Option(findView(TR.swipe_animation_content))

  lazy val icon = Option(findView(TR.swipe_animation_icon))

  runUi(
    (root <~ vBackgroundColor(colorForeground)) ~
      (rippleView <~ vBackgroundColor(colorBackground) <~ vInvisible) ~
      (icon <~ vBackground(circle)))

  def initAnimation(contentView: ContentView, widthContainer: Int): Ui[_] = {
    val sizeIcon = icon map (ic => ic.getWidth + ic.getPaddingLeft + ic.getPaddingRight) getOrElse 0
    val (translationContent, translationIcon, resIcon) = contentView match {
      case AppsView => (widthContainer, 0, R.drawable.icon_collection_contacts_detail)
      case ContactView => (-widthContainer, widthContainer - sizeIcon, R.drawable.icon_collection_default_detail)
    }
    (self <~
      vVisible <~
      vTranslationX(translationContent)) ~
      (root <~ vBackgroundColor(colorForeground)) ~
      (icon <~
        vVisible <~
        vTranslationX(translationIcon) <~
        ivSrc(resIcon) <~
        tivDefaultColor(theme.get(SearchIconsColor))) ~
      Ui(circle.setPercentage(0))
  }

  def moveAnimation(
    contentView: ContentView,
    widthContainer: Int,
    displacement: Float): Ui[_] = {
    val sizeIcon = icon map (ic => ic.getWidth + ic.getPaddingLeft + ic.getPaddingRight) getOrElse 0
    val distance = (widthContainer / 2) - (sizeIcon / 2)
    val percentage: Float = math.abs(displacement) / widthContainer.toFloat
    val iconX = (distance * displacement) / widthContainer
    val (translationContent, translationIcon) = contentView match {
      case AppsView => (widthContainer - displacement, iconX)
      case ContactView => (-widthContainer - displacement, widthContainer - sizeIcon + iconX)
    }
    (self <~ vTranslationX(translationContent)) ~
      (icon <~ vTranslationX(translationIcon)) ~
      Ui(circle.setPercentage(percentage))
  }

  def endAnimation(duration: Int): Ui[_] =
    (self <~ animatedClose(duration)) ~
      (icon <~ iconFadeOut(duration))

}
