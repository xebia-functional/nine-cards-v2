package cards.nine.app.ui.commons.dialogs.createoreditcollection

import cards.nine.models.Collection
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait CreateOrEditCollectionDOM { self: TypedFindView =>

  lazy val name = findView(TR.new_collection_name)

  lazy val collectionName = findView(TR.new_collection_name)

  lazy val colorContent = findView(TR.new_collection_select_color_content)

  lazy val colorImage = findView(TR.new_collection_select_color_image)

  lazy val colorText = findView(TR.new_collection_select_color_text)

  lazy val iconContent = findView(TR.new_collection_select_icon_content)

  lazy val iconImage = findView(TR.new_collection_select_icon_image)

  lazy val iconText = findView(TR.new_collection_select_icon_text)

}

trait CreateOrEditCollectionListener {

  def changeColor(maybeColor: Option[Int])

  def changeIcon(maybeIcon: Option[String])

  def saveCollection(
      maybeName: Option[String],
      maybeIcon: Option[String],
      maybeIndex: Option[Int]): Unit

  def editCollection(
      collection: Collection,
      maybeName: Option[String],
      maybeIcon: Option[String],
      maybeIndex: Option[Int]): Unit

}
