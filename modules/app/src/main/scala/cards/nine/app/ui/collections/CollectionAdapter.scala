package cards.nine.app.ui.collections

import android.graphics.drawable._
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.{CardView, RecyclerView}
import android.view.ViewGroup.LayoutParams._
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.ImageView.ScaleType
import android.widget.{FrameLayout, TextView}
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.collections.jobs.EditingCollectionMode
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.styles.CommonStyles
import cards.nine.app.ui.components.commons.ReorderItemTouchListener
import cards.nine.app.ui.components.drawables.{BackgroundSelectedDrawable, IconTypes, PathMorphDrawable}
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.app.ui.preferences.commons.{FontSize, IconsSize, ShowPositionInCards}
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.ops.SeqOps._
import cards.nine.models.types._
import cards.nine.models.{Card, Collection}
import cards.nine.process.theme.models.{CardBackgroundColor, CardTextColor, NineCardsTheme}
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui, _}

case class CollectionAdapter(
  var collection: Collection,
  heightCard: Int,
  onClick: (Card, Int) => Unit,
  onLongClick: (ViewHolder) => Unit)
  (implicit activityContext: ActivityContextWrapper,
    uiContext: UiContext[_],
    theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderCollectionAdapter]
  with ReorderItemTouchListener { self =>

  val showPositions = ShowPositionInCards.readValue

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCollectionAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.card_item, parent, false)
    ViewHolderCollectionAdapter(
      content = view,
      heightCard = heightCard,
      showPositions = showPositions,
      onLongClick = onLongClick)
  }

  override def getItemCount: Int = collection.cards.size

  override def onBindViewHolder(viewHolder: ViewHolderCollectionAdapter, position: Int): Unit =
    viewHolder.bind(collection.cards(position), onClick).run

  def addCards(cards: Seq[Card]): Unit = {
    collection = collection.copy(cards = collection.cards ++ cards)
    val count = cards.length
    notifyItemRangeInserted(collection.cards.length - count, count)
  }

  def removeCards(cards: Seq[Card]): Unit = {
    val cardIds = cards map (_.id)
    collection = collection.copy(cards = collection.cards.filterNot(c => cardIds.contains(c.id)))
    notifyDataSetChanged()
  }

  def updateCard(card: Card): Unit = {
    val position = card.position
    collection = collection.copy(cards = collection.cards.updated(position, card))
    notifyItemChanged(position, card)
  }

  def updateCards(cards: Seq[Card]): Unit = {
    collection = collection.copy(cards = cards)
    notifyItemRangeChanged(0, cards.length)
  }

  override def onItemMove(from: Int, to: Int): Unit = {
    collection = collection.copy(cards = collection.cards.reorder(from, to))
    notifyItemMoved(from, to)
  }
}

case class ViewHolderCollectionAdapter(
  content: CardView,
  heightCard: Int,
  showPositions: Boolean,
  onLongClick: (ViewHolder) => Unit)
  (implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with CollectionAdapterStyles
  with TypedFindView {

  lazy val iconContent = findView(TR.card_icon_content)

  lazy val icon = findView(TR.card_icon)

  lazy val name = findView(TR.card_text)

  lazy val badge = findView(TR.card_badge)

  lazy val selectedIcon = findView(TR.card_selected)

  val iconSelectedDrawable = PathMorphDrawable(
    defaultIcon = IconTypes.CHECK,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_thin),
    padding = resGetDimensionPixelSize(R.dimen.padding_small))

  val selectedBackground = new BackgroundSelectedDrawable

  ((content <~
    rootStyle(heightCard) <~
    On.longClick {
      Ui {
        onLongClick(this)
        true
      }
    }) ~
    (selectedIcon <~ vBackground(selectedBackground)) ~
    (iconContent <~ iconContentStyle(heightCard))).run

  def bind(card: Card, onClick: (Card, Int) => Unit)(implicit uiContext: UiContext[_]): Ui[_] = {
    val selectedViewUi = statuses.collectionMode match {
      case EditingCollectionMode => selectCard(statuses.positionsEditing.contains(getAdapterPosition))
      case _ => if (selectedIcon.getVisibility == View.VISIBLE) clearSelectedCard() else Ui.nop
    }
    val text = if (showPositions) s"${card.position} - ${card.term}" else card.term
    (content <~ On.click {
      Ui(onClick(card, getAdapterPosition))
    }) ~
      (icon <~ vResize(IconsSize.getIconApp) <~ iconCardTransform(card)) ~
      (name <~ tvText(text) <~ tvSizeResource(FontSize.getSizeResource) <~ nameStyle(card.cardType)) ~
      (badge <~ (getBadge(card.cardType) map {
        ivSrc(_) + vVisible
      } getOrElse vGone)) ~
      selectedViewUi
  }

  def selectCard(select: Boolean) = {
    selectedBackground.selected(select)
    selectedIcon <~ vVisible <~ (if (select) ivSrc(iconSelectedDrawable) else ivBlank)
  }

  def clearSelectedCard() = selectedIcon <~ vGone

  private[this] def getBadge(cardType: CardType): Option[Int] = cardType match {
    case PhoneCardType => Option(R.drawable.badge_phone)
    case SmsCardType => Option(R.drawable.badge_sms)
    case EmailCardType => Option(R.drawable.badge_email)
    case _ => None
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}

trait CollectionAdapterStyles extends CommonStyles {

  val iconContentHeightRatio = .6f

  def rootStyle(heightCard: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[CardView] =
    Tweak[CardView] { view =>
      view.getLayoutParams.height = heightCard
    } +
      cvCardBackgroundColor(theme.get(CardBackgroundColor)) +
      flForeground(createBackground) +
      vDisableHapticFeedback

  def iconContentStyle(heightCard: Int)(implicit context: ContextWrapper): Tweak[FrameLayout] =
    Tweak[FrameLayout] { view =>
      view.getLayoutParams.height = (heightCard * iconContentHeightRatio).toInt
    }

  def nameStyle(cardType: CardType)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    cardType match {
      case NoInstalledAppCardType =>
        tvColor(theme.get(CardTextColor).alpha(.4f))
      case _ =>
        tvColor(theme.get(CardTextColor))
    }

  def iconCardTransform(card: Card)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme) =
    card.cardType match {
      case cardType if cardType.isContact =>
        ivUriContactFromLookup(card.intent.extractLookup(), card.term) +
          vBackground(javaNull) +
          expandLayout +
          ivScaleType(ScaleType.CENTER_CROP)
      case AppCardType => ivSrcByPackageName(card.packageName, card.term)
      case NoInstalledAppCardType =>
        val shape = new ShapeDrawable(new OvalShape)
        shape.getPaint.setColor(theme.get(CardTextColor).alpha(.4f))
        val iconColor = theme.get(CardBackgroundColor)
        ivSrc(R.drawable.icon_card_not_installed) +
          tivDefaultColor(iconColor) +
          tivPressedColor(iconColor) +
          vBackground(shape) +
          reduceLayout +
          ivScaleType(ScaleType.CENTER_INSIDE)
      case _ =>
        ivCardUri(card.imagePath, card.term, circular = true) +
          vBackground(javaNull) +
          reduceLayout +
          ivScaleType(ScaleType.FIT_CENTER)
    }

  private[this] def expandLayout(implicit context: ContextWrapper): Tweak[View] = Tweak[View] {
    view =>
      val params = view.getLayoutParams
      params.height = MATCH_PARENT
      params.width = MATCH_PARENT
      view.requestLayout()
  }

  private[this] def reduceLayout(implicit context: ContextWrapper): Tweak[View] =
    vResize(IconsSize.getIconApp)

}
