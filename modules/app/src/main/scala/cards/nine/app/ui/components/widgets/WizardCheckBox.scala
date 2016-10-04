package cards.nine.app.ui.components.widgets

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import cards.nine.app.ui.commons.ops.NineCardsCategoryOps._
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.commons._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.models.types.NineCardCategory
import cards.nine.process.collection.models.PackagesByCategory
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

class WizardCheckBox(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends LinearLayout(context, attr, defStyleAttr)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val sizeIconTitle = resGetDimensionPixelSize(R.dimen.wizard_size_checkbox_title)

  val sizeIconCollection = resGetDimensionPixelSize(R.dimen.wizard_size_checkbox_collection)

  val paddingIcon = resGetDimensionPixelSize(R.dimen.padding_default)

  val selectedColor = resGetColor(R.color.wizard_background_new_conf_step_0)

  val unselectedColor = resGetColor(R.color.wizard_checkbox_unselected)

  val selectedDrawable = {
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.getPaint.setColor(selectedColor)
    drawable
  }

  val unselectedDrawable = {
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.getPaint.setColor(unselectedColor)
    drawable
  }

  LayoutInflater.from(context).inflate(R.layout.wizard_checkbox, this)

  val icon = findView(TR.wizard_check_icon)

  val text = findView(TR.wizard_check_text)

  val iconSelectedDrawable = PathMorphDrawable(
    defaultIcon = IconTypes.CHECK,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_thin),
    padding = resGetDimensionPixelSize(R.dimen.padding_small))

  def initialize(resText: Int): Ui[Any] =
    (icon <~
      vResize(Option(sizeIconTitle), Option(sizeIconTitle)) <~
      ivSrc(iconSelectedDrawable) <~
      vBackground(selectedDrawable)) ~
      (text <~
        tvText(resText) <~
        tvColorResource(R.color.wizard_text_title) <~
        tvSizeResource(R.dimen.text_large))

  def initializeCollection(packagesByCategory: PackagesByCategory): Ui[Any] = {
    val nineCardCategory = NineCardCategory(packagesByCategory.category)
    val title = resGetString(R.string.wizard_new_conf_collection_name_step_1, nineCardCategory.getName, packagesByCategory.packages.length.toString)
    (icon <~
      vResize(Option(sizeIconCollection), Option(sizeIconCollection)) <~
      vPaddings(paddingIcon) <~
      ivSrc(nineCardCategory.getIconCollectionDetail) <~
      vBackground(selectedDrawable)) ~
      (text <~
        tvText(title) <~
        tvColorResource(R.color.wizard_text_title) <~
        tvSizeResource(R.dimen.text_xlarge))
  }

}
