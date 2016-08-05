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
import com.fortysevendeg.ninecardslauncher.process.theme.models.{DrawerTextColor, NineCardsTheme}
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

  val padding = resGetDimensionPixelSize(R.dimen.padding_small)

  private[this] lazy val title = findView(TR.workspace_moment_title)

  private[this] lazy val icon = findView(TR.workspace_moment_icon)

  def populateCard(card: Card)(implicit theme: NineCardsTheme): Ui[Any] =
    (title <~ tvColor(theme.get(DrawerTextColor)) <~ tvText(card.term)) ~
      (icon <~
        (card.cardType match {
          case cardType if cardType.isContact => ivUriContact(card.imagePath, card.term, circular = true)
          case AppCardType => ivSrcByPackageName(card.packageName, card.term)
          case _ => ivCardUri(card.imagePath, card.term, circular = true)
        }))

}

