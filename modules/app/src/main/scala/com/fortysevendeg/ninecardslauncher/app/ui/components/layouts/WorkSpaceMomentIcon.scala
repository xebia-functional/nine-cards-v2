package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
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

class WorkSpaceMomentIcon(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends LinearLayout(context, attr, defStyleAttr)
  with Contexts[View]
  with TypedFindView {

  implicit val uiContext: UiContext[Context] = GenericUiContext(context)

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  LayoutInflater.from(context).inflate(R.layout.workspace_moment_menu, this)

  val displacement = resGetDimensionPixelSize(R.dimen.shadow_displacement_default)

  val padding = resGetDimensionPixelSize(R.dimen.padding_small)

  val radius = resGetDimensionPixelSize(R.dimen.shadow_radius_default)

  private[this] val content = Option(findView(TR.workspace_moment_icon_content))

  private[this] val title = Option(findView(TR.workspace_moment_title))

  private[this] val icon = Option(findView(TR.workspace_moment_icon))

  (title <~ tvShadowLayer(radius, displacement, displacement, resGetColor(R.color.shadow_default))).run

  def populateCollection(collection: Collection): Ui[Any] = {
    val resIcon = iconCollectionDetail(collection.icon)
    (title <~ tvText(collection.name)) ~
      (content <~ vPaddings(padding)) ~
      (icon <~
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

}

