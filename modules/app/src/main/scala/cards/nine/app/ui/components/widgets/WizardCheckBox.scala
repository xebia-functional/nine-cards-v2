package cards.nine.app.ui.components.widgets

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import cards.nine.app.ui.commons.CommonsTweak._
import macroid.extras.UIActionsExtras._
import cards.nine.app.ui.commons.ops.NineCardsCategoryOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.commons._
import cards.nine.models.PackagesByCategory
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid._

class WizardCheckBox(context: Context, attr: AttributeSet, defStyleAttr: Int)
    extends LinearLayout(context, attr, defStyleAttr)
    with Contexts[View]
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val checkKey = "widget-check"

  val dataKey = "widget-data"

  val sizeIconTitle = resGetDimensionPixelSize(R.dimen.wizard_size_checkbox_title)

  val sizeIconCollection = resGetDimensionPixelSize(R.dimen.wizard_size_checkbox_collection)

  val paddingIcon = resGetDimensionPixelSize(R.dimen.padding_default)

  val selectedColor = resGetColor(R.color.wizard_new_conf_accent_1)

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

  (this <~ vAddField(checkKey, true)).run

  val iconSelectedDrawable = PathMorphDrawable(
    defaultIcon = IconTypes.CHECK,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_thin),
    padding = resGetDimensionPixelSize(R.dimen.padding_small))

  def initialize(resText: Int, defaultCheck: Boolean = true): Ui[Any] =
    (icon <~
      vResize(sizeIconTitle, sizeIconTitle) <~
      ivSrc(iconSelectedDrawable)) ~
      (text <~
        tvText(resText) <~
        tvSizeResource(R.dimen.text_large)) ~
      (if (defaultCheck) check() else uncheck())

  def initializeCollection(
      packagesByCategory: PackagesByCategory,
      defaultCheck: Boolean = true): Ui[Any] = {
    val nineCardCategory = packagesByCategory.category
    val title = resGetString(
      R.string.wizard_new_conf_collection_name_step_1,
      nineCardCategory.getName,
      packagesByCategory.packages.length.toString)
    (this <~ vAddField(dataKey, packagesByCategory)) ~
      (icon <~
        vResize(sizeIconCollection, sizeIconCollection) <~
        vPaddings(paddingIcon) <~
        ivSrc(nineCardCategory.getIconCollectionDetail)) ~
      (text <~
        tvText(title) <~
        tvSizeResource(R.dimen.text_xlarge)) ~
      (if (defaultCheck) check() else uncheck())
  }

  def check(): Ui[Any] =
    (this <~ vAddField(checkKey, true)) ~
      (icon <~ vBackground(selectedDrawable)) ~
      (text <~ tvColorResource(R.color.wizard_text_title))

  def uncheck(): Ui[Any] =
    (this <~ vAddField(checkKey, false)) ~
      (icon <~ vBackground(unselectedDrawable)) ~
      (text <~ tvColorResource(R.color.wizard_checkbox_unselected))

  def swap(): Ui[Any] = this.getField[Boolean](checkKey) match {
    case Some(true)  => uncheck()
    case Some(false) => check()
    case _           => Ui.nop
  }

  def isCheck: Boolean = this.getField[Boolean](checkKey) exists (c => c)

  def getData: Option[PackagesByCategory] =
    this.getField[PackagesByCategory](dataKey)

  def getDataIfSelected: Option[PackagesByCategory] =
    if (isCheck) this.getField[PackagesByCategory](dataKey) else None

}
