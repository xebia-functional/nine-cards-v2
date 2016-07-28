package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog

import android.widget.ArrayAdapter
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.SpinnerTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.LauncherExecutor
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.Styles
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait PublishCollectionActionsImpl
  extends PublishCollectionActions
  with LauncherExecutor
  with Styles {

  self: TypedFindView with PublishCollectionFragment =>

  implicit val publishCollectionPresenter: PublishCollectionPresenter

  lazy val startLayout = findView(TR.publish_collection_wizard_start)

  lazy val informationLayout = findView(TR.publish_collection_wizard_information)

  lazy val publishingLayout = findView(TR.publish_collection_wizard_publishing)

  lazy val endLayout = findView(TR.publish_collection_wizard_end)

  lazy val startArrow = findView(TR.publish_collection_wizard_arrow)

  lazy val collectionName = findView(TR.collection_name)

  lazy val descriptionText = findView(TR.description)

  lazy val categorySpinner = findView(TR.category)

  lazy val publishButton = findView(TR.publish_collection_wizard_information_button)

  lazy val endButton = findView(TR.publish_collection_wizard_end_button)

  lazy val loading = findView(TR.action_loading)

  lazy val (categoryNamesMenu, categories) = {
    val categoriesSorted = NineCardCategory.appsCategories map { category =>
      (resGetString(category.getStringResource) getOrElse category.name, category)
    } sortBy(_._1)
    (categoriesSorted map (_._1), categoriesSorted map (_._2))
  }

  override def initialize(): Ui[Any] =
    (startLayout <~ vVisible) ~
      (informationLayout <~ vInvisible) ~
      (publishingLayout <~ vInvisible) ~
      (endLayout <~ vInvisible) ~
      (startArrow <~ On.click(Ui(publishCollectionPresenter.showCollectionInformation())))

  override def goToPublishCollectionInformation(collection: Collection): Ui[Any] =
    (startLayout <~ applyFadeOut()) ~
      (informationLayout <~ applyFadeIn()) ~
      (publishingLayout <~ vInvisible) ~
      (endLayout <~ vInvisible) ~
      (collectionName <~ tvText(collection.name)) ~
      addCategoriesToSpinner(collection) ~
      (publishButton <~ On.click(Ui(publishCollectionPresenter.publishCollection(getName, getDescription, getCategory))))

  override def goBackToPublishCollectionInformation(name: String, description: String, category: NineCardCategory): Ui[Any] =
    (startLayout <~ vInvisible) ~
      (informationLayout <~ applyFadeIn()) ~
      (publishingLayout <~ applyFadeOut()) ~
      (endLayout <~ vInvisible) ~
      (collectionName <~ tvText(name)) ~
      (descriptionText <~ tvText(description)) ~
      addCategoriesToSpinner(collection) ~
      (publishButton <~ On.click(Ui(publishCollectionPresenter.publishCollection(getName, getDescription, getCategory))))

  override def goToPublishCollectionPublishing(): Ui[Any] =
    (startLayout <~ vInvisible) ~
      (informationLayout <~ applyFadeOut()) ~
      (publishingLayout <~ applyFadeIn()) ~
      (endLayout <~ vInvisible)

  override def goToPublishCollectionEnd(shareLink: String): Ui[Any] =
    (startLayout <~ vInvisible) ~
      (informationLayout <~ vInvisible) ~
      (publishingLayout <~ applyFadeOut()) ~
      (endLayout <~ applyFadeIn()) ~
      (endButton <~ On.click(Ui(launchShare(shareLink)) ~ Ui(dismiss())))

  override def showMessageCollectionError: Ui[Any] = showMessage(R.string.collectionError)

  override def showMessageFormFieldError: Ui[Any] = showMessage(R.string.formFieldError)

  override def showMessagePublishingError: Ui[Any] = showMessage(R.string.publishingError)

  private[this] def showMessage(message: Int): Ui[Any] = uiShortToast(message)

  private[this] def addCategoriesToSpinner(collection: Collection): Ui[Any] = {
    val selectCategory = Seq(resGetString(R.string.addInformationCategory))
    val categoryNames = (selectCategory ++ categoryNamesMenu).toArray
    val collectionCategoryName = collection.appsCategory map getCategoryName

    val sa = new ArrayAdapter[String](fragmentContextWrapper.getOriginal, android.R.layout.simple_spinner_dropdown_item, categoryNames)
    val spinnerPosition = collectionCategoryName map sa.getPosition getOrElse 0

    categorySpinner <~ sAdapter(sa) <~ sSelection(spinnerPosition)
  }

  private[this] def getCategoryName(category: NineCardCategory) =
    resGetString(category.getStringResource) getOrElse category.name

  private[this] def getName: Option[String] = (for {
    text <- Option(collectionName.getText)
  } yield if (text.toString.isEmpty) None else Some(text.toString)).flatten

  private[this] def getDescription: Option[String] = (for {
    text <- Option(descriptionText.getText)
  } yield if (text.toString.isEmpty) None else Some(text.toString)).flatten

  private[this] def getCategory: Option[NineCardCategory] = categorySpinner.getSelectedItemPosition match {
    case pos if pos == 0 => None
    case pos => categories.lift(pos - 1)
  }

}

