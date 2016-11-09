package cards.nine.app.ui.launcher.actions.privatecollections

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.commons.styles.{CollectionCardsStyles, CommonStyles}
import cards.nine.models.{CardData, CollectionData, NineCardsTheme}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import cards.nine.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class PrivateCollectionsAdapter(
  privateCollections: Seq[CollectionData],
  onClick: (CollectionData => Unit))
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderPrivateCollectionsLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPrivateCollectionsLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.private_collections_item, parent, false)
    ViewHolderPrivateCollectionsLayoutAdapter(view)
  }

  override def getItemCount: Int = privateCollections.size

  override def onBindViewHolder(viewHolder: ViewHolderPrivateCollectionsLayoutAdapter, position: Int): Unit = {
    val privateCollection = privateCollections(position)
    viewHolder.bind(privateCollection, onClick).run
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}

case class ViewHolderPrivateCollectionsLayoutAdapter(
  content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView
  with CollectionCardsStyles
  with CommonStyles {

  val appsByRow = 5

  lazy val root = findView(TR.private_collections_item_layout)

  lazy val iconContent = findView(TR.private_collections_item_content)

  lazy val icon = findView(TR.private_collections_item_icon)

  lazy val name = findView(TR.private_collections_item_name)

  lazy val appsRow = findView(TR.private_collections_item_row)

  lazy val addCollection = findView(TR.private_collections_item_add_collection)

  lazy val line = findView(TR.private_collections_item_line)

  ((root <~ cardRootStyle) ~
    (name <~ titleTextStyle) ~
    (line <~ vBackgroundColor(theme.getLineColor)) ~
    (addCollection <~ buttonStyle)).run

  def bind(collection: CollectionData, onClick: (CollectionData => Unit)): Ui[_] = {
    val background = new ShapeDrawable(new OvalShape)
    background.getPaint.setColor(theme.getIndexColor(collection.themedColorIndex))
    (iconContent <~ vBackground(background)) ~
      (icon <~ ivSrc(collection.getIconCollectionDetail)) ~
      (appsRow <~
        vgRemoveAllViews <~
        fblAddItems(collection.cards, (item: CardData) => {
          ivSrcByPackageName(item.packageName, item.term)
        })) ~
      (name <~ tvText(collection.name)) ~
      (addCollection <~ On.click(Ui(onClick(collection))))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}