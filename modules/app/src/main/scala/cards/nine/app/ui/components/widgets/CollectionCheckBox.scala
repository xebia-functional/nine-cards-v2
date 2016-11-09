package cards.nine.app.ui.components.widgets

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.commons._
import cards.nine.models._
import cards.nine.models.types.theme.{CardLayoutBackgroundColor, ThemeDark, ThemeLight, ThemeType}
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid._

class CollectionCheckBox(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends LinearLayout(context, attr, defStyleAttr)
    with Contexts[View]
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val checkKey = "collection-check"

  val collectionKey = "collection-moment"

  val paddingIcon = resGetDimensionPixelSize(R.dimen.padding_medium)

  val paddingCheckbox = resGetDimensionPixelSize(R.dimen.card_padding_small)

  val selectedColor = resGetColor(R.color.checkbox_selected)

  val unselectedLightColor = resGetColor(R.color.checkbox_light_unselected)

  val unselectedDarkColor = resGetColor(R.color.checkbox_dark_unselected)

  def selectedDrawable(color: Int) = {
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.getPaint.setColor(color)
    drawable
  }

  def unselectedDrawable(themeType: ThemeType) = {
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.getPaint.setColor(
      themeType match {
        case ThemeLight => unselectedLightColor
        case ThemeDark => unselectedDarkColor
      })
    drawable
  }

  LayoutInflater.from(context).inflate(R.layout.collection_checkbox, this)

  val collectionIcon = findView(TR.collection_icon)

  val checkboxIconContent = findView(TR.subscriptions_item_content)

  val checkboxIcon = findView(TR.collection_checkbox_icon)

  (this <~ vAddField(checkKey, true)).run

  val iconSelectedDrawable = PathMorphDrawable(
    defaultIcon = IconTypes.CHECK,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_thin),
    padding = resGetDimensionPixelSize(R.dimen.card_padding_small))

  def initialize(icon: Int, color: Int, theme: NineCardsTheme, defaultCheck: Boolean = true): Ui[Any] = {
    (this <~ vAddField(collectionKey, icon)) ~
      (collectionIcon <~
        vBackground(selectedDrawable(color)) <~
        vPaddings(paddingIcon) <~
        ivSrc(icon)) ~
      (checkboxIconContent <~
        vBackground(selectedDrawable(theme.get(CardLayoutBackgroundColor)))) ~
      (checkboxIcon <~
        vBackground(selectedDrawable(selectedColor)) <~
        ivSrc(iconSelectedDrawable)) ~
      (if (defaultCheck) check(color) else uncheck(theme.parent))
  }

  def check(color: Int): Ui[Any] =
    (this <~ vAddField(checkKey, true)) ~
      (collectionIcon <~ vBackground(selectedDrawable(color))) ~
      (checkboxIcon <~ vBackground(selectedDrawable(selectedColor)))

  def uncheck(themeType: ThemeType): Ui[Any] =
    (this <~ vAddField(checkKey, false)) ~
      (collectionIcon <~ vBackground(unselectedDrawable(themeType))) ~
      (checkboxIcon <~ vBackground(unselectedDrawable(themeType)))

  def isCheck: Boolean = this.getField[Boolean](checkKey) exists (c => c)

}
