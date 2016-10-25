package cards.nine.app.ui.components.layouts

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.{ImageView, LinearLayout, TimePicker}
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.app.ui.components.drawables.CharDrawable
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.commons.javaNull
import cards.nine.models.types.theme.{DrawerTextColor, DrawerIconColor}
import cards.nine.models.{MomentTimeSlot, NineCardsTheme}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.util.Try

class EditHourMomentLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
    with Contexts[View]
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  val margin = resGetDimensionPixelSize(R.dimen.padding_default)

  val paddingLarge = resGetDimensionPixelSize(R.dimen.padding_large)

  val daySelectedColor = resGetColor(R.color.collection_fab_button_item_edit_moment)

  val dayUnselectedColor = resGetColor(R.color.edit_moment_unselected_day)

  val daysWeek = getResources.getStringArray(R.array.days_letters).toList

  lazy val startContent = findView(TR.edit_hour_start_content)

  lazy val startText = findView(TR.edit_hour_start_text)

  lazy val endContent = findView(TR.edit_hour_end_content)

  lazy val endText = findView(TR.edit_hour_end_text)

  lazy val deleteAction = findView(TR.edit_hour_action_delete)

  lazy val daysContent = findView(TR.edit_hour_days_content)

  LayoutInflater.from(context).inflate(R.layout.edit_moment_hour_layout, this)

  def populate(
    time: MomentTimeSlot,
    position: Int,
    onRemoveHour: (Int) => Unit,
    onChangeFromHour: (Int, String) => Unit,
    onChangeToHour: (Int, String) => Unit,
    onSwapDays: (Int, Int) => Unit)
    (implicit theme: NineCardsTheme): Ui[Any] = {
    val iconColor = theme.get(DrawerIconColor)
    val arrow = resGetDrawable(R.drawable.icon_edit_moment_arrow).colorize(iconColor)
    val textColor = theme.get(DrawerTextColor)
    (this <~ vSetPosition(position) <~ vGlobalLayoutListener(_ => fillDays(position, time.days, onSwapDays))) ~
      (startContent <~ On.click(showTime(position, time.from, from = true, onChangeFromHour))) ~
      (endContent <~ On.click(showTime(position, time.to, from = false, onChangeToHour))) ~
      (startText <~
        tvText(time.from) <~
        tvColor(textColor) <~
        tvCompoundDrawablesWithIntrinsicBounds(right = Some(arrow))) ~
      (endText <~
        tvText(time.to) <~
        tvColor(textColor) <~
        tvCompoundDrawablesWithIntrinsicBounds(right = Some(arrow))) ~
      (deleteAction <~
        tivDefaultColor(iconColor) <~
        On.click(Ui(onRemoveHour(position))))
  }

  private[this] def showTime(position: Int, time: String, from: Boolean, onChangeToHour: (Int, String) => Unit): Ui[Any] = Try {
    val timeArray = time.split(":")
    (timeArray(0).toInt, timeArray(1).toInt)
  }.toOption match {
    case Some((hour, min)) =>
      Ui {
        val dialog = new TimePickerDialog(getContext, new OnTimeSetListener {
          def timeToString(time: Int) = if (time < 10) s"0$time" else time.toString
          override def onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int): Unit = {
            val hour = s"${timeToString(hourOfDay)}:${timeToString(minute)}"
            if (from)
              onChangeToHour(position, hour)
            else
              onChangeToHour(position, hour)
          }
        }, hour, min, true)
        dialog.show()
      }
    case _ => Ui.nop
  }

  private[this] def fillDays(position: Int, days: Seq[Int], onSwapDays: (Int, Int) => Unit) = {
    val views = days.zipWithIndex map {
      case (day, index) =>
        val letter = daysWeek.lift(index) getOrElse ""
        val color = if (day == 0) dayUnselectedColor else daySelectedColor
        (w[ImageView] <~
          On.click(Ui(onSwapDays(position, index))) <~
          ivSrc(CharDrawable(letter, circle = true, Some(color)))).get
    }
    val sizeDay = ((getWidth - (paddingLarge * 2)) / days.length) - (margin * 2)
    val params = new LinearLayout.LayoutParams(sizeDay, sizeDay)
    params.setMargins(margin, margin, margin, margin)
    daysContent <~
      vgRemoveAllViews <~
      vgAddViews(views, params)
  }

}
