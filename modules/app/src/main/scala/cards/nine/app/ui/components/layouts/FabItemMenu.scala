package cards.nine.app.ui.components.layouts

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.{FrameLayout, ImageView, TextView}
import macroid.extras.DeviceVersion._
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid._

class FabItemMenu(context: Context, attr: AttributeSet, defStyleAttr: Int)
    extends FrameLayout(context, attr, defStyleAttr)
    with Contexts[View]
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  LayoutInflater.from(context).inflate(R.layout.fab_item, this)

  val title = Option(findView(TR.fab_title))

  val icon = Option(findView(TR.fab_icon))

  (icon <~ fabStyle).run

  def populate(backgroundColor: Int, res: Int, text: Int): Ui[Any] =
    (title <~ tvText(text)) ~
      (icon <~ ivSrc(res)) ~
      changeBackground(backgroundColor)

  def changeBackground(backgroundColor: Int): Ui[Any] =
    icon <~
      (Lollipop ifSupportedThen {
        vBackgroundColor(backgroundColor)
      } getOrElse {
        val drawable = new ShapeDrawable(new OvalShape)
        drawable.getPaint.setColor(backgroundColor)
        vBackground(drawable)
      })

  private[this] def fabStyle: Tweak[ImageView] =
    Lollipop ifSupportedThen {
      vElevation(resGetDimension(R.dimen.elevation_fab_button)) + vCircleOutlineProvider()
    } getOrElse Tweak.blank

}
