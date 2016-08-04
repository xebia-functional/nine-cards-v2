package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{GenericUiContext, UiContext}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppCardType
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
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

  private[this] val content = Option(findView(TR.workspace_moment_icon_content))

  private[this] val title = Option(findView(TR.workspace_moment_title))

  private[this] val icon = Option(findView(TR.workspace_moment_icon))

  def init(t: WorkSpaceButtonType): Ui[Any] = title <~ tvColorResource(t match {
    case WorkSpaceAppMomentButton => R.color.menu_apps_moment_item_title
    case WorkSpaceActionWidgetButton => R.color.widgets_text
  })

  def populateCollection(collection: Collection): Ui[Any] = {
    val resIcon = iconCollectionDetail(collection.icon)
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
          case cardType if cardType.isContact => ivUriContact(card.imagePath, card.term, circular = true)
          case AppCardType => ivSrcByPackageName(card.packageName, card.term)
          case _ => ivCardUri(card.imagePath, card.term, circular = true)
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
