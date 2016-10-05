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
import cards.nine.commons._
import cards.nine.models.types.NineCardsMoment
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

class WizardWifiCheckBox(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends LinearLayout(context, attr, defStyleAttr)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val checkKey = "widget-check"

  val paddingIcon = resGetDimensionPixelSize(R.dimen.padding_default)

  val selectedColor = resGetColor(R.color.wizard_background_new_conf_step_2)

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

  LayoutInflater.from(context).inflate(R.layout.wizard_wifi_checkbox, this)

  val icon = findView(TR.wizard_wifi_check_icon)

  val name = findView(TR.wizard_wifi_check_name)

  val textConnected = findView(TR.wizard_wifi_check_connected)

  val wifiAction = findView(TR.wizard_wifi_check_wifi_action)

  (this <~ vAddField(checkKey, true)).run

  def initialize(moment: NineCardsMoment, defaultCheck: Boolean = true): Ui[Any] =
    (icon <~
      ivSrc(moment.getIconCollectionDetail)) ~
      (name <~
        tvText(moment.getName)) ~
      (textConnected <~
        tvText(R.string.wizard_new_conf_wifi_no_connected_step_3)) ~
      (if (defaultCheck) check() else uncheck())

  def check(): Ui[Any] =
    (this <~ vAddField(checkKey, true)) ~
      (icon <~ vBackground(selectedDrawable)) ~
      (name <~ tvColorResource(R.color.wizard_text_title)) ~
      (textConnected <~ tvColorResource(R.color.wizard_text_message)) ~
      (wifiAction <~ vClickable(true) <~ tivColor(resGetColor(R.color.wizard_text_title)))

  def uncheck(): Ui[Any] =
    (this <~ vAddField(checkKey, false)) ~
      (icon <~ vBackground(unselectedDrawable)) ~
      (name <~ tvColorResource(R.color.wizard_checkbox_unselected)) ~
      (textConnected <~ tvColorResource(R.color.wizard_checkbox_unselected)) ~
      (wifiAction <~ vClickable(true) <~ tivColor(resGetColor(R.color.wizard_checkbox_unselected)))

  def swap(): Ui[Any] = this.getField[Boolean](checkKey) match {
    case Some(true) => uncheck()
    case Some(false) => check()
    case _ => Ui.nop
  }

  def isCheck: Boolean = this.getField[Boolean](checkKey) exists(c => c)

}
