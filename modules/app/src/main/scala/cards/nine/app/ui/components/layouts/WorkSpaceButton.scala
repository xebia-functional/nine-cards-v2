package cards.nine.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.commons.{GenericUiContext, UiContext}
import cards.nine.commons._
import cards.nine.models.types.AppCardType
import cards.nine.models.types.theme.DrawerTextColor
import cards.nine.models.{Card, Collection, NineCardsTheme}
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid._

class WorkSpaceButton(context: Context, attr: AttributeSet, defStyleAttr: Int)
    extends LinearLayout(context, attr, defStyleAttr)
    with Contexts[View]
    with TypedFindView {

  implicit val uiContext: UiContext[Context] = GenericUiContext(context)

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  LayoutInflater.from(context).inflate(R.layout.workspace_button, this)

  val padding = resGetDimensionPixelSize(R.dimen.padding_small)

  private[this] lazy val content = findView(TR.workspace_moment_icon_content)

  private[this] lazy val title = findView(TR.workspace_moment_title)

  private[this] lazy val icon = findView(TR.workspace_moment_icon)

  def init(t: WorkSpaceButtonType)(implicit theme: NineCardsTheme): Ui[Any] =
    t match {
      case WorkSpaceAppMomentButton =>
        title <~ tvColor(theme.get(DrawerTextColor))
      case WorkSpaceActionWidgetButton =>
        (this <~ vBlankBackground) ~
          (title <~ tvColorResource(R.color.widgets_text))
    }

  def populateCollection(collection: Collection)(implicit theme: NineCardsTheme): Ui[Any] = {
    val resIcon = collection.getIconDetail
    (title <~ tvText(collection.name)) ~
      (content <~ vPaddings(padding)) ~
      (icon <~
        ivScaleType(ScaleType.CENTER_INSIDE) <~
        vBackgroundCollection(collection.themedColorIndex) <~
        ivSrc(resIcon))
  }

  def populateCard(card: Card): Ui[Any] =
    (title <~ tvText(card.term)) ~
      (icon <~
        (card.cardType match {
          case cardType if cardType.isContact =>
            ivUriContactFromLookup(card.intent.extractLookup(), card.term, circular = true)
          case AppCardType => ivSrcByPackageName(card.packageName, card.term)
          case _           => ivCardUri(card.imagePath, card.term, circular = true)
        }))

  def populateIcon(resIcon: Int, resTitle: Int, resColor: Int): Ui[Any] = {
    (title <~ tvText(resTitle)) ~
      (content <~ vPaddings(padding)) ~
      (icon <~
        ivScaleType(ScaleType.CENTER_INSIDE) <~
        vBackgroundCircle(resGetColor(resColor)) <~
        ivSrc(resIcon))
  }

}

sealed trait WorkSpaceButtonType

case object WorkSpaceAppMomentButton extends WorkSpaceButtonType

case object WorkSpaceActionWidgetButton extends WorkSpaceButtonType
