package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.{ImageView, LinearLayout}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.CharDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.commons.models.MomentTimeSlot
import com.fortysevendeg.ninecardslauncher.process.theme.models.{DrawerIconColor, DrawerTextColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class EditHourMomentLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
    with Contexts[View]
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  val margin = resGetDimensionPixelSize(R.dimen.padding_default)

  val sizeDay = resGetDimensionPixelSize(R.dimen.edit_moment_size_day)

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

  def populate(time: MomentTimeSlot, position: Int)(implicit theme: NineCardsTheme): Ui[Any] = {
    val iconColor = theme.get(DrawerIconColor)
    val textColor = theme.get(DrawerTextColor)
    (this <~ vSetPosition(position)) ~
      (startText <~ tvText(time.from) <~ tvColor(textColor)) ~
      (endText <~ tvText(time.to) <~ tvColor(textColor)) ~
      (deleteAction <~ tivDefaultColor(iconColor)) ~
      fillDays(time.days)
  }

  private[this] def fillDays(days: Seq[Int]) = {
    val views = days.zipWithIndex map {
      case (day, index) =>
        val letter = daysWeek.lift(index) getOrElse ""
        val color = if (day == 0) dayUnselectedColor else daySelectedColor
        (w[ImageView] <~
          ivSrc(CharDrawable(letter, circle = true, Some(color)))).get
    }
    val params = new LinearLayout.LayoutParams(sizeDay, sizeDay)
    params.setMargins(margin, margin, margin, margin)
    daysContent <~
      vgRemoveAllViews <~
      vgAddViews(views, params)
  }

}
