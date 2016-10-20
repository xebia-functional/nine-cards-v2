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
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
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

  val paddingIcon = resGetDimensionPixelSize(R.dimen.padding_default)

  val unselectedColor = resGetColor(R.color.checkbox_unselected)

  def selectedDrawable(color: Int) = {
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.getPaint.setColor(color)
    drawable
  }

  val unselectedDrawable = {
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.getPaint.setColor(unselectedColor)
    drawable
  }

  LayoutInflater.from(context).inflate(R.layout.collection_checkbox, this)

  val iconContent = findView(TR.collection_check_content)

  val collectionIcon = findView(TR.collection_icon)

  val checkboxIcon = findView(TR.collection_checkbox_icon)

  (this <~ vAddField(checkKey, true)).run

  val iconSelectedDrawable = PathMorphDrawable(
    defaultIcon = IconTypes.CHECK,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_thin),
    padding = resGetDimensionPixelSize(R.dimen.padding_small))

  def initialize(icon: Int, color: Int, defaultCheck: Boolean = true): Ui[Any] = {
    val background = new ShapeDrawable(new OvalShape)
    background.getPaint.setColor(color)
    (this <~ vAddField(collectionKey, icon)) ~
      (collectionIcon <~
        vBackground(background) <~
        vPaddings(paddingIcon) <~
        ivSrc(icon)) ~
      (if (defaultCheck) check(color) else uncheck())
  }

  def check(color: Int): Ui[Any] =
    (this <~ vAddField(checkKey, true)) ~
      (iconContent <~ vBackground(selectedDrawable(color)))

  def uncheck(): Ui[Any] =
    (this <~ vAddField(checkKey, false)) ~
      (iconContent <~ vBackground(unselectedDrawable))

  def isCheck: Boolean = this.getField[Boolean](checkKey) exists (c => c)

}
