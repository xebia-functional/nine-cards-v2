package cards.nine.app.ui.components.widgets

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ops.NineCardsMomentOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.commons._
import cards.nine.models.types.NineCardsMoment
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid._

class WizardMomentCheckBox(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends LinearLayout(context, attr, defStyleAttr)
    with Contexts[View]
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val checkKey = "widget-check"

  val momentKey = "widget-moment"

  val paddingIcon = resGetDimensionPixelSize(R.dimen.padding_default)

  val selectedColor = resGetColor(R.color.wizard_new_conf_accent_3)

  val tagSelectedColor = resGetColor(R.color.checkbox_selected)

  val tagBackgroundColor = resGetColor(R.color.background_app)

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

  val tagSelectedDrawable = {
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.getPaint.setColor(tagSelectedColor)
    drawable
  }

  val tagUnselectedDrawable = {
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.getPaint.setColor(unselectedColor)
    drawable
  }

  val tagBackgroundDrawable = {
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.getPaint.setColor(tagBackgroundColor)
    drawable
  }

  LayoutInflater.from(context).inflate(R.layout.wizard_moment_checkbox, this)

  val iconContent = findView(TR.wizard_moment_check_content)

  val icon = findView(TR.wizard_moment_check_icon)

  val text = findView(TR.wizard_moment_check_name)

  val tag = findView(TR.wizard_moment_check_tag)

  val tagContent = findView(TR.wizard_moment_check_tag_content)

  (this <~ vAddField(checkKey, true)).run

  val iconSelectedDrawable = PathMorphDrawable(
    defaultIcon = IconTypes.CHECK,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_thin),
    padding = resGetDimensionPixelSize(R.dimen.padding_small))

  def initialize(moment: NineCardsMoment, defaultCheck: Boolean = true): Ui[Any] = {
    (this <~ vAddField(momentKey, moment)) ~
      (icon <~
        vPaddings(paddingIcon) <~
        ivSrc(moment.getIconCollectionDetail)) ~
      (tag <~
        ivSrc(iconSelectedDrawable)) ~
      (tagContent <~
        vBackground(tagBackgroundDrawable)) ~
      (text <~
        tvText(moment.getName) <~
        tvSizeResource(R.dimen.text_xlarge)) ~
      (if (defaultCheck) check() else uncheck())
  }

  def check(): Ui[Any] =
    (this <~ vAddField(checkKey, true)) ~
      (iconContent <~ vBackground(selectedDrawable)) ~
      (tag <~ vBackground(tagSelectedDrawable)) ~
      (text <~ tvColorResource(R.color.wizard_text_title))

  def uncheck(): Ui[Any] =
    (this <~ vAddField(checkKey, false)) ~
      (iconContent <~ vBackground(unselectedDrawable)) ~
      (tag <~ vBackground(tagUnselectedDrawable)) ~
      (text <~ tvColorResource(R.color.wizard_checkbox_unselected))

  def swap(): Ui[Any] = this.getField[Boolean](checkKey) match {
    case Some(true) => uncheck()
    case Some(false) => check()
    case _ => Ui.nop
  }

  def isCheck: Boolean = this.getField[Boolean](checkKey) exists (c => c)

  def getMomentIfSelected: Option[NineCardsMoment] =
    if (isCheck) this.getField[NineCardsMoment](momentKey) else None

}
