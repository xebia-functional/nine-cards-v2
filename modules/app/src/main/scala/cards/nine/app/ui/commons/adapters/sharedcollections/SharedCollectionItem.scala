package cards.nine.app.ui.commons.adapters.sharedcollections

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.ViewGroup.LayoutParams._
import android.view.{View, ViewGroup}
import android.widget._
import cards.nine.app.ui.commons.AppUtils._
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.SharedCollectionOps._
import cards.nine.app.ui.commons.styles.{CollectionCardsStyles, CommonStyles}
import cards.nine.models.types
import cards.nine.models.types.{NotPublished, PublishedByMe, PublishedByOther}
import cards.nine.process.sharedcollections.models._
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import com.google.android.flexbox.FlexboxLayout
import macroid.FullDsl._
import macroid._

trait SharedCollectionItem
  extends CollectionCardsStyles
  with CommonStyles
  with TypedFindView {

  implicit val context: ActivityContextWrapper

  implicit val uiContext: UiContext[_]

  def content: ViewGroup

  val appsByRow = 5

  lazy val root = findView(TR.public_collections_item_layout)

  lazy val iconContent = findView(TR.public_collections_item_content)

  lazy val icon = findView(TR.public_collections_item_icon)

  lazy val name = findView(TR.public_collections_item_name)

  lazy val author = findView(TR.public_collections_item_author)

  lazy val downloads = findView(TR.public_collections_item_downloads)

  lazy val subscriptions = findView(TR.public_collections_item_subscriptions)

  lazy val appsIcons = findView(TR.public_collections_item_apps)

  lazy val addCollection = findView(TR.public_collections_item_add_collection)

  lazy val shareCollection = findView(TR.public_collections_item_share_collection)

  def initialize()(implicit theme: NineCardsTheme): Ui[Any] = {
    (root <~ cardRootStyle) ~
      (name <~ titleTextStyle) ~
      (author <~ subtitleTextStyle) ~
      (downloads <~ leftDrawableTextStyle(R.drawable.icon_collection_downloads) <~ subtitleTextStyle) ~
      (subscriptions <~ leftDrawableTextStyle(R.drawable.icon_collection_subscriptions) <~ subtitleTextStyle) ~
      (addCollection <~ buttonStyle) ~
      (shareCollection <~ ivSrc(tintDrawable(R.drawable.icon_dialog_collection_share)))
  }

  def bind(
    collection: SharedCollection,
    onAddCollection: => Unit,
    onShareCollection: => Unit)(implicit theme: NineCardsTheme): Ui[Any] = {

    def addCollectionTweak() = collection.publicCollectionStatus match {
      case NotPublished =>
        tvText(R.string.addMyCollection) +
          tvAllCaps2(true) + tvNormalMedium + On.click(Ui(onAddCollection)) + vEnabled(true)
      case types.Subscribed | PublishedByOther =>
        tvText(R.string.alreadyAddedCollection) +
          tvAllCaps2(false) + tvItalicLight + vEnabled(false)
      case PublishedByMe =>
        tvText(R.string.ownedCollection) +
          tvAllCaps2(false) + tvItalicLight + vEnabled(false)
    }

    val background = new ShapeDrawable(new OvalShape)
    background.getPaint.setColor(resGetColor(getRandomIndexColor))
    val apps = collection.resolvedPackages
    (iconContent <~ vBackground(background)) ~
      (icon <~ ivSrc(collection.getIconCollectionDetail)) ~
      (appsIcons <~
        vgRemoveAllViews <~
        automaticAlignment(apps)) ~
      (name <~ tvText(resGetString(collection.name) getOrElse collection.name)) ~
      (author <~ tvText(collection.author)) ~
      (subscriptions <~
        (if (collection.subscriptions.isDefined) vVisible + tvText(resGetString(R.string.subscriptions_number, collection.views.toString)) else vGone)) ~
      (downloads <~ tvText(s"${collection.views}")) ~
      (addCollection <~ addCollectionTweak()) ~
      (shareCollection <~ On.click(Ui(onShareCollection)))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

  private[this] def automaticAlignment(packages: Seq[SharedCollectionPackage]): Tweak[FlexboxLayout] = {
    val width = appsIcons.getWidth
    if (width > 0) {
      vgAddViews(getViewsByCards(packages, width))
    } else {
      vGlobalLayoutListener { v => {
        appsIcons <~ vgAddViews(getViewsByCards(packages, v.getWidth))
      }}
    }
  }

  private[this] def getViewsByCards(packages: Seq[SharedCollectionPackage], width: Int) = {
    val sizeIcon = resGetDimensionPixelSize(R.dimen.size_icon_item_collections_content)
    val sizeView = width / appsByRow
    val padding = (sizeView - sizeIcon) / 2
    val appsViews = packages map { pkg =>
      (w[ImageView] <~
        lp[FlexboxLayout](sizeView, WRAP_CONTENT) <~
        vPadding(padding, 0, padding, 0) <~
        ivUri(pkg.icon)).get
    }
    appsViews
  }

}