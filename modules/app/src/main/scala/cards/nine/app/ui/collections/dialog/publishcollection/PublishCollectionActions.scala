package cards.nine.app.ui.collections.dialog.publishcollection

import android.view.View
import android.widget.TextView
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.commons.CommonsTweak._
import macroid.extras.UIActionsExtras._
import macroid.extras.EditTextTweaks._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.dialogs.Styles
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.widgets.TintableImageView
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.NineCardsCategory
import cards.nine.models.types.theme.{DrawerIconColor, PrimaryColor}
import cards.nine.models.{Collection, _}
import macroid.extras.ProgressBarTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

class PublishCollectionActions(dom: PublishCollectionDOM with PublishCollectionUiListener)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Styles
  with PublishCollectionStyles {

  val steps = 3

  implicit def theme: NineCardsTheme = statuses.theme

  lazy val (categoryNamesMenu, categories) = {
    val categoriesSorted = NineCardsCategory.appsCategories map { category =>
      (resGetString(category.getStringResource) getOrElse category.name, category)
    } sortBy(_._1)
    (categoriesSorted map (_._1), categoriesSorted map (_._2))
  }

  def initialize(): TaskService[Unit] = {
    val drawerIconColor = statuses.theme.get(DrawerIconColor)
    ((dom.rootLayout <~ dialogBackgroundStyle) ~
      (dom.startLayout <~ vVisible) ~
      (dom.informationLayout <~ vInvisible) ~
      (dom.publishingLayout <~ vInvisible) ~
      (dom.endLayout <~ vInvisible) ~
      (dom.startHeader <~ titleTextStyle) ~
      (dom.startMessage <~ subtitleTextStyle) ~
      (dom.informationHeader <~ titleTextStyle) ~
      (dom.informationMessage <~ subtitleTextStyle) ~
      (dom.collectionTag <~ subtitleTextStyle) ~
      (dom.collectionName <~
        titleTextStyle <~
        etHintColor(drawerIconColor.alpha(0.3f))) ~
      (dom.categoryTag <~ subtitleTextStyle) ~
      (dom.publishButton <~ subtitleTextStyle) ~
      (dom.publishingHeader <~ titleTextStyle) ~
      (dom.publishingMessage <~ subtitleTextStyle) ~
      (dom.endHeader <~ titleTextStyle) ~
      (dom.endMessage <~ subtitleTextStyle) ~
      (dom.startArrow <~
        tivColor(drawerIconColor) <~
        On.click(Ui(dom.showCollectionInformation()))) ~
      (dom.collectionNameLine <~ iconStyle(0.5f)) ~
      (dom.categoryIndicator <~ tivColor(drawerIconColor)) ~
      (dom.categoryLine <~ iconStyle(0.5f)) ~
      (dom.loading <~ pbColor(statuses.theme.get(PrimaryColor))) ~
      (dom.endLine <~ iconStyle()) ~
      (dom.endButton <~ subtitleTextStyle) ~
      createPagers() ~
      (dom.paginationPanel <~ reloadPagers(currentPage = 0))).toService()
  }

  def goToPublishCollectionInformation(collection: Collection): TaskService[Unit] =
    ((dom.startLayout <~ applyFadeOut()) ~
      (dom.informationLayout <~ applyFadeIn()) ~
      (dom.publishingLayout <~ vInvisible) ~
      (dom.endLayout <~ vInvisible) ~
      (dom.collectionName <~ tvText(collection.name)) ~
      (dom.categorySelect <~ categoryOnClick) ~
      (dom.categorySpinner <~ spinnerStyle) ~
      Ui(setCategory(collection.appsCategory)) ~
      (dom.publishButton <~ publishOnClick) ~
      (dom.paginationPanel <~ reloadPagers(currentPage = 1))).toService()

  def goBackToPublishCollectionInformation(name: String, category: NineCardsCategory): TaskService[Unit] =
    ((dom.startLayout <~ vInvisible) ~
      (dom.informationLayout <~ applyFadeIn()) ~
      (dom.publishingLayout <~ applyFadeOut()) ~
      (dom.endLayout <~ vInvisible) ~
      (dom.collectionName <~ tvText(name)) ~
      (dom.categorySelect <~ categoryOnClick) ~
      (dom.categorySpinner <~ spinnerStyle) ~
      Ui(setCategory(Some(category))) ~
      (dom.publishButton <~ publishOnClick) ~
      (dom.paginationPanel <~ reloadPagers(currentPage = 1))).toService()

  def goToPublishCollectionPublishing(): TaskService[Unit] =
    ((dom.startLayout <~ vInvisible) ~
      (dom.informationLayout <~ applyFadeOut()) ~
      (dom.publishingLayout <~ applyFadeIn()) ~
      (dom.endLayout <~ vInvisible) ~
      (dom.paginationPanel <~ reloadPagers(currentPage = 1))).toService()

  def goToPublishCollectionEnd(sharedCollectionId: String): TaskService[Unit] =
    ((dom.startLayout <~ vInvisible) ~
      (dom.informationLayout <~ vInvisible) ~
      (dom.publishingLayout <~ applyFadeOut()) ~
      (dom.paginationPanel <~ vInvisible)~
      (dom.endLayout <~ applyFadeIn()) ~
      Ui(dom.reloadSharedCollectionId()) ~
      (dom.endButton <~ On.click(Ui(dom.launchShareCollection(sharedCollectionId)) ~ Ui(dom.dismiss())))).toService()

  def showMessageCollectionError: TaskService[Unit] = showMessage(R.string.collectionError).toService()

  def showMessageFormFieldError: TaskService[Unit] = showMessage(R.string.formFieldError).toService()

  def showMessagePublishingError: TaskService[Unit]= showMessage(R.string.publishingError).toService()

  def showContactUsError: TaskService[Unit] = showMessage(R.string.contactUsError).toService()

  private[this] def showMessage(message: Int): Ui[Any] = uiShortToast(message)

  private[this] def createPagers() = {
    val pagerViews = (0 until steps) map pagination
    dom.paginationPanel <~ vgAddViews(pagerViews)
  }

  private[this] def reloadPagers(currentPage: Int) = Transformer {
    case i: TintableImageView if Option(i.getTag).isDefined && i.getTag.equals(currentPage.toString) =>
      i <~ tivColor(statuses.theme.get(DrawerIconColor).alpha(0.5f))
    case i: TintableImageView => i <~ tivColor(statuses.theme.get(DrawerIconColor).alpha(0.2f))
  }

  private[this] def pagination(position: Int) =
    (w[TintableImageView] <~ paginationItemStyle <~ vTag(position.toString)).get

  private[this] def setCategory(maybeCategory: Option[NineCardsCategory]): Unit = {
    maybeCategory foreach { category =>
      dom.categorySpinner.setTag(category)
      dom.categorySpinner.setText(categoryNamesMenu(categories.indexOf(category)))
      dom.categorySpinner.setTextColor(statuses.theme.get(DrawerIconColor))
    }
  }

  private[this] def categoryOnClick: Tweak[View] =
    On.click {
      dom.categorySpinner <~ vListThemedPopupWindowShow(
        values = categoryNamesMenu,
        onItemClickListener = (position: Int) => setCategory(Some(categories(position))),
        width = Some(resGetDimensionPixelSize(R.dimen.width_list_popup_menu)),
        height = Some(resGetDimensionPixelSize(R.dimen.height_list_popup_menu)))
    }

  private[this] def publishOnClick: Tweak[TextView] =
    On.click(Ui(dom.publishCollection(dom.getName, dom.getCategory)))

}

