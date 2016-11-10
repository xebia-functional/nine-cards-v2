package cards.nine.app.ui.commons.adapters.sharedcollections

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.{View, ViewGroup}
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.SharedCollectionOps._
import cards.nine.app.ui.commons.styles.{CollectionCardsStyles, CommonStyles}
import cards.nine.models.types.{NotPublished, PublishedByMe, PublishedByOther, Subscribed}
import cards.nine.models.{NineCardsTheme, SharedCollection, SharedCollectionPackage}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
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

  lazy val line = findView(TR.public_collections_item_line)

  def initialize()(implicit theme: NineCardsTheme): Ui[Any] = {
    (root <~ cardRootStyle) ~
      (name <~ titleTextStyle) ~
      (line <~ vBackgroundColor(theme.getLineColor)) ~
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
      case Subscribed | PublishedByOther =>
        tvText(R.string.alreadyAddedCollection) +
          tvAllCaps2(false) + tvItalicLight + vEnabled(false)
      case PublishedByMe =>
        tvText(R.string.ownedCollection) +
          tvAllCaps2(false) + tvItalicLight + vEnabled(false)
    }

    val background = new ShapeDrawable(new OvalShape)
    background.getPaint.setColor(theme.getRandomIndexColor)
    val apps = collection.resolvedPackages
    (iconContent <~ vBackground(background)) ~
      (icon <~ ivSrc(collection.getIconCollectionDetail)) ~
      (appsIcons <~
        vgRemoveAllViews <~
        fblAddItems(apps, (item: SharedCollectionPackage) => {
          ivUri(item.icon)
        })) ~
      (name <~ tvText(resGetString(collection.name) getOrElse collection.name)) ~
      (author <~ tvText(collection.author)) ~
      (subscriptions <~
        (if (collection.subscriptions.isDefined) vVisible +
          tvText(resGetString(R.string.subscriptions_number,
            (collection.subscriptions getOrElse 0).toString)) else vGone)) ~
      (downloads <~ tvText(s"${collection.views}")) ~
      (addCollection <~ addCollectionTweak()) ~
      (shareCollection <~ On.click(Ui(onShareCollection)))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}