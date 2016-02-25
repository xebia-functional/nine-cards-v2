package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.{ContactView, AppsView, ContentView}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.theme.models.{SearchIconsColor, NineCardsTheme, SearchBackgroundColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails.SwipeAnimatedDrawerViewSnails._
import macroid.{Ui, ActivityContextWrapper}
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

  val shape = new ShapeDrawable(new OvalShape)
  shape.getPaint.setColor(colorBackground)

  LayoutInflater.from(context).inflate(R.layout.swipe_animation_drawer_layout, self)

  lazy val root = Option(findView(TR.swipe_animation_root))

  lazy val rippleView = Option(findView(TR.swipe_animation_content))

  lazy val icon = Option(findView(TR.swipe_animation_icon))

  runUi(
    (root <~ vBackgroundColor(colorForeground)) ~
      (rippleView <~ vBackgroundColor(colorBackground) <~ vInvisible) ~
      (icon <~ vBackground(shape)))

  def initAnimation(contentView: ContentView, widthContainer: Int): Ui[_] = {
    val sizeIcon = icon map (ic => ic.getWidth + ic.getPaddingLeft + ic.getPaddingRight) getOrElse 0
    val (translationContent, translationIcon) = contentView match {
      case AppsView => (widthContainer, 0)
      case ContactView => (-widthContainer, widthContainer - sizeIcon)
    }
    (self <~ vVisible <~ vTranslationX(translationContent)) ~
      (root <~ vBackgroundColor(colorForeground)) ~
      (icon <~
        vVisible <~
        vTranslationX(translationIcon) <~
        ivSrc(contentView match {
          case AppsView => R.drawable.icon_collection_default_detail
          case ContactView => R.drawable.icon_collection_contacts_detail
        }) <~
        tivDefaultColor(theme.get(SearchIconsColor)))
  }

  def endAnimation(duration: Int): Ui[_] =
    (self <~ animatedClose(duration)) ~
      (icon <~ iconFadeOut(duration))
}
