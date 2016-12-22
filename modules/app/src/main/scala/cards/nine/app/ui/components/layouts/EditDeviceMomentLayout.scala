package cards.nine.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.commons._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.{DrawerIconColor, DrawerTextColor}
import macroid.extras.TextViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class EditDeviceMomentLayout(context: Context, attrs: AttributeSet, defStyle: Int)
    extends LinearLayout(context, attrs, defStyle)
    with Contexts[View]
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  lazy val name = findView(TR.edit_wifi_name)

  lazy val deleteAction = findView(TR.edit_wifi_action_delete)

  LayoutInflater.from(context).inflate(R.layout.edit_moment_wifi_layout, this)

  def populate(deviceName: String, position: Int, onRemoveDevice: (Int => Unit))(
      implicit theme: NineCardsTheme): Ui[Any] = {
    val iconColor = theme.get(DrawerIconColor)
    val textColor = theme.get(DrawerTextColor)
    (this <~ vSetPosition(position)) ~
      (name <~
        tvText(deviceName) <~
        tvColor(textColor)) ~
      (deleteAction <~
        tivDefaultColor(iconColor) <~
        On.click(Ui(onRemoveDevice(position))))
  }

}
