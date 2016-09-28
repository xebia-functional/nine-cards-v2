package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.editmoment.EditMomentPresenter
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{DrawerIconColor, DrawerTextColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class EditWifiMomentLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
    with Contexts[View]
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  lazy val name = findView(TR.edit_wifi_name)

  lazy val deleteAction = findView(TR.edit_wifi_action_delete)

  LayoutInflater.from(context).inflate(R.layout.edit_moment_wifi_layout, this)

  def populate(wifi: String, position: Int)(implicit theme: NineCardsTheme, editMomentPresenter: EditMomentPresenter): Ui[Any] = {
    val iconColor = theme.get(DrawerIconColor)
    val textColor = theme.get(DrawerTextColor)
    (this <~ vSetPosition(position)) ~
      (name <~
        tvText(wifi) <~
        tvColor(textColor)) ~
      (deleteAction <~
        tivDefaultColor(iconColor) <~
        On.click(Ui(editMomentPresenter.removeWifi(position))))
  }

}
