package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog

import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.Styles
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.TintableImageView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait PublishCollectionActionsImpl
  extends PublishCollectionActions
  with Styles
  with PublishCollectionStyles {

  self: TypedFindView with PublishCollectionFragment =>

  implicit val publishCollectionPresenter: PublishCollectionPresenter

  implicit lazy val theme: NineCardsTheme = publishCollectionPresenter.getTheme

  val steps = 3

  lazy val colorPrimary = theme.get(PrimaryColor)

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

  lazy val collectionInput = findView(TR.collection_name_information)

  lazy val collectionName = findView(TR.collection_name)

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

  lazy val (categoryNamesMenu, categories) = {
    val categoriesSorted = NineCardCategory.appsCategories map { category =>
      (resGetString(category.getStringResource) getOrElse category.name, category)
    } sortBy(_._1)
    (categoriesSorted map (_._1), categoriesSorted map (_._2))
  }

  override def initialize(): Ui[Any] =
    (rootLayout <~ dialogBackgroundStyle) ~
      (startLayout <~ vVisible) ~
      (informationLayout <~ vInvisible) ~
      (publishingLayout <~ vInvisible) ~
      (endLayout <~ vInvisible) ~
      (startHeader <~ titleTextStyle) ~
      (startMessage <~ subtitleTextStyle) ~
      (informationHeader <~ titleTextStyle) ~
      (informationMessage <~ subtitleTextStyle) ~
      (collectionName <~ titleTextStyle) ~
      (publishButton <~ subtitleTextStyle) ~
      (publishingHeader <~ titleTextStyle) ~
      (publishingMessage <~ subtitleTextStyle) ~
      (endHeader <~ titleTextStyle) ~
      (endMessage <~ subtitleTextStyle) ~
      (startArrow <~ tivColor(theme.get(DrawerIconColor)) <~ On.click(Ui(publishCollectionPresenter.showCollectionInformation()))) ~
      (categoryIndicator <~ tivColor(theme.get(DrawerIconColor))) ~
      (categoryLine <~ vBackgroundColor(theme.get(DrawerIconColor))) ~
      (loading <~ sChangeProgressBarColor(colorPrimary)) ~
      (endLine <~ vBackgroundColor(theme.get(DrawerIconColor))) ~
      (endButton <~ subtitleTextStyle) ~
      createPagers() ~
      (paginationPanel <~ reloadPagers(currentPage = 0))

  override def goToPublishCollectionInformation(collection: Collection): Ui[Any] =
    (startLayout <~ applyFadeOut()) ~
      (informationLayout <~ applyFadeIn()) ~
      (publishingLayout <~ vInvisible) ~
      (endLayout <~ vInvisible) ~
      (collectionName <~ tvText(collection.name)) ~
      (categorySelect <~ categoryOnClick) ~
      (categorySpinner <~ spinnerStyle) ~
      Ui(setCategory(collection.appsCategory)) ~
      (publishButton <~ publishOnClick) ~
      (paginationPanel <~ reloadPagers(currentPage = 1))

  override def goBackToPublishCollectionInformation(name: String, category: NineCardCategory): Ui[Any] =
    (startLayout <~ vInvisible) ~
      (informationLayout <~ applyFadeIn()) ~
      (publishingLayout <~ applyFadeOut()) ~
      (endLayout <~ vInvisible) ~
      (collectionName <~ tvText(name)) ~
      (categorySelect <~ categoryOnClick) ~
      (categorySpinner <~ spinnerStyle) ~
      Ui(setCategory(Some(category))) ~
      (publishButton <~ publishOnClick) ~
      (paginationPanel <~ reloadPagers(currentPage = 1))

  override def goToPublishCollectionPublishing(): Ui[Any] =
    (startLayout <~ vInvisible) ~
      (informationLayout <~ applyFadeOut()) ~
      (publishingLayout <~ applyFadeIn()) ~
      (endLayout <~ vInvisible) ~
      (paginationPanel <~ reloadPagers(currentPage = 1))

  override def goToPublishCollectionEnd(sharedCollectionId: String): Ui[Any] =
    (startLayout <~ vInvisible) ~
      (informationLayout <~ vInvisible) ~
      (publishingLayout <~ applyFadeOut()) ~
      (paginationPanel <~ vInvisible)~
      (endLayout <~ applyFadeIn()) ~
      invalidateOptionMenu() ~
      (endButton <~ On.click(Ui(publishCollectionPresenter.launchShareCollection(sharedCollectionId)) ~ Ui(dismiss())))

  override def showMessageCollectionError: Ui[Any] = showMessage(R.string.collectionError)

  override def showMessageFormFieldError: Ui[Any] = showMessage(R.string.formFieldError)

  override def showMessagePublishingError: Ui[Any] = showMessage(R.string.publishingError)

  override def showContactUsError: Ui[Any] = showMessage(R.string.contactUsError)

  private[this] def showMessage(message: Int): Ui[Any] = uiShortToast2(message)

  private[this] def createPagers() = {
    val pagerViews = (0 until steps) map pagination
    paginationPanel <~ vgAddViews(pagerViews)
  }

  private[this] def reloadPagers(currentPage: Int) = Transformer {
    case i: TintableImageView if Option(i.getTag).isDefined && i.getTag.equals(currentPage.toString) =>
      i <~ tivColor(theme.get(DrawerIconColor).alpha(0.5f))
    case i: TintableImageView => i <~ tivColor(theme.get(DrawerIconColor).alpha(0.2f))
  }

  private[this] def pagination(position: Int) =
    (w[TintableImageView] <~ paginationItemStyle <~ vTag(position.toString)).get

  private[this] def getName: Option[String] = (for {
    text <- Option(collectionName.getText)
  } yield if (text.toString.isEmpty) None else Some(text.toString)).flatten

  private[this] def setCategory(maybeCategory: Option[NineCardCategory]): Unit = {
    maybeCategory foreach { category =>
      categorySpinner.setTag(category)
      categorySpinner.setText(categoryNamesMenu(categories.indexOf(category)))
    }
  }

  private[this] def getCategory: Option[NineCardCategory] =
    categorySpinner.getTag match {
      case category: NineCardCategory => Some(category)
      case _ => None
    }

  private[this] def categoryOnClick: Tweak[TextView] =
    On.click {
      categorySpinner <~ vListThemedPopupWindowShow(
        values = categoryNamesMenu,
        onItemClickListener = (position: Int) => setCategory(Some(categories(position))),
        width = Some(resGetDimensionPixelSize(R.dimen.width_list_popup_menu)),
        height = Some(resGetDimensionPixelSize(R.dimen.height_list_popup_menu)))
    }

  private[this] def publishOnClick: Tweak[TextView] =
    On.click(Ui(publishCollectionPresenter.publishCollection(getName, getCategory)))

  private[this] def invalidateOptionMenu(): Ui[Any] = Ui {
    fragmentContextWrapper.original.get match {
      case Some(activity: AppCompatActivity) => activity.supportInvalidateOptionsMenu()
      case _ =>
    }
  }

}

