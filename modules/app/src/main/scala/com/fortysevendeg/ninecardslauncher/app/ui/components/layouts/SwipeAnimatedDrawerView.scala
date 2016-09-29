package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.DrawerAnimationBackgroundDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails.SwipeAnimatedDrawerViewSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.{AppsView, ContactView, ContentView}
import cards.nine.commons.javaNull
import cards.nine.process.theme.models.{NineCardsTheme, SearchBackgroundColor, SearchIconsColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

class SwipeAnimatedDrawerView (context: Context, attrs: AttributeSet, defStyle: Int)
  extends FrameLayout(context, attrs, defStyle)
  with TypedFindView
  with Contexts[View] { self =>

  def this(context: Context) =
    this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) =
    this(context, attrs, 0)

  lazy val root = Option(findView(TR.swipe_animation_root))

  lazy val icon = Option(findView(TR.swipe_animation_icon))

  LayoutInflater.from(context).inflate(R.layout.swipe_animation_drawer_layout, self)

  def initAnimation(contentView: ContentView, widthContainer: Int)
    (implicit theme: NineCardsTheme): Ui[_] = {
    val colorBackground = theme.get(SearchBackgroundColor)
    val colorForeground = colorBackground.dark(0.05f)
    val background = new DrawerAnimationBackgroundDrawable(colorForeground, colorBackground)

    val sizeIcon = icon map (ic => ic.getWidth + ic.getPaddingLeft + ic.getPaddingRight) getOrElse 0
    val (translationContent, translationIcon, resIcon) = contentView match {
      case AppsView => (widthContainer, 0, R.drawable.icon_collection_contacts_detail)
      case ContactView => (-widthContainer, widthContainer - sizeIcon, R.drawable.icon_collection_default_detail)
    }
    (root <~ vBackground(background)) ~
      (self <~
        vVisible <~
        vTranslationX(translationContent)) ~
      (icon <~
        vVisible <~
        vTranslationX(translationIcon) <~
        ivSrc(resIcon) <~
        tivDefaultColor(theme.get(SearchIconsColor))) ~
      Ui(background.setData(0, 0))
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
    val x = translationIcon + (sizeIcon / 2)
    val backgroundUi = root map {
      _.getBackground match {
        case drawable: DrawerAnimationBackgroundDrawable => Ui(drawable.setData(percentage * percentage, x.toInt))
        case _ => Ui.nop
      }
    } getOrElse Ui.nop
    (self <~ vTranslationX(translationContent)) ~
      (icon <~ vTranslationX(translationIcon)) ~
      backgroundUi
  }

  def endAnimation(duration: Int): Ui[_] =
    icon <~ iconFadeOut(duration)

}
