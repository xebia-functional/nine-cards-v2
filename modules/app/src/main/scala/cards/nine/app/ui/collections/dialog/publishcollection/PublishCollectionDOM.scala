package cards.nine.app.ui.collections.dialog.publishcollection

import cards.nine.models.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}

trait PublishCollectionDOM {

  self: TypedFindView =>

  lazy val rootLayout = findView(TR.publish_collection_wizard_root)

  lazy val startLayout = findView(TR.publish_collection_wizard_start)

  lazy val informationLayout = findView(TR.publish_collection_wizard_information)

  lazy val publishingLayout = findView(TR.publish_collection_wizard_publishing)

  lazy val endLayout = findView(TR.publish_collection_wizard_end)

  lazy val startHeader = findView(TR.publish_collection_wizard_start_header)

  lazy val startMessage = findView(TR.publish_collection_wizard_start_message)

  lazy val startArrow = findView(TR.publish_collection_wizard_arrow)

  lazy val informationHeader = findView(TR.publish_collection_wizard_information_header)

  lazy val informationMessage = findView(TR.publish_collection_information_message)

  lazy val collectionTag = findView(TR.collection_name_tag)

  lazy val collectionInput = findView(TR.collection_name)

  lazy val collectionName = findView(TR.collection_name)

  lazy val collectionNameLine = findView(TR.collection_name_line)

  lazy val categoryTag = findView(TR.category_tag)

  lazy val categorySelect = findView(TR.category_select)

  lazy val categorySpinner = findView(TR.category)

  lazy val categoryIndicator = findView(TR.category_indicator)

  lazy val categoryLine = findView(TR.category_line)

  lazy val publishButton = findView(TR.publish_collection_wizard_information_button)

  lazy val publishingHeader = findView(TR.publish_collection_wizard_publishing_header)

  lazy val publishingMessage = findView(TR.publish_collection_wizard_publishing_message)

  lazy val loading = findView(TR.action_loading)

  lazy val endHeader = findView(TR.publish_collection_wizard_end_header)

  lazy val endMessage = findView(TR.publish_collection_wizard_end_message)

  lazy val endLine = findView(TR.end_line)

  lazy val endButton = findView(TR.publish_collection_wizard_end_button)

  lazy val paginationPanel = findView(TR.publish_collection_wizard_steps_pagination_panel)

  def getName: Option[String] =
    Option(collectionName.getText) flatMap {
      case text if text.toString.nonEmpty => Option(text.toString)
      case _ => None
    }

  def getCategory: Option[NineCardCategory] =
    categorySpinner.getTag match {
      case category: NineCardCategory => Option(category)
      case _ => None
    }

}

trait PublishCollectionUiListener {

  def showCollectionInformation(): Unit

  def launchShareCollection(sharedCollectionId: String): Unit

  def reloadSharedCollectionId(): Unit

  def publishCollection(name: Option[String], category: Option[NineCardCategory]): Unit

  def dismiss(): Unit

}