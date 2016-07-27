package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog

import android.support.v4.app.Fragment
import android.widget.ArrayAdapter
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.SpinnerTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.Styles
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait PublishCollectionActionsImpl
  extends PublishCollectionActions
  with Styles {

  self: TypedFindView with Contexts[Fragment] =>

  implicit val publishCollectionPresenter: PublishCollectionPresenter

  lazy val publishCollectionStartLayout = Option(findView(TR.publish_collection_wizard_start))

  lazy val publishCollectionInformationLayout = Option(findView(TR.publish_collection_wizard_information))

  lazy val publishCollectionEndLayout = Option(findView(TR.publish_collection_wizard_end))

  lazy val publishCollectionStartArrow = Option(findView(TR.publish_collection_wizard_arrow))

  lazy val publishCollectionInformationCollectionName = Option(findView(TR.collection_name))

  lazy val publishCollectionInformationDescription = Option(findView(TR.description))

  lazy val publishCollectionInformationCategorySpinner = Option(findView(TR.category))

  lazy val publishCollectionInformationPublish = Option(findView(TR.publish_collection_wizard_information_button))

  override def initialize(collection: Collection): Ui[Any] =
    (publishCollectionStartLayout <~ vVisible) ~
      (publishCollectionInformationLayout <~ vInvisible) ~
      (publishCollectionEndLayout <~ vInvisible) ~
      (publishCollectionStartArrow <~ On.click(goToPublishCollectionInformation(collection)))

  private[this] def goToPublishCollectionInformation(collection: Collection): Ui[Any] =
    (publishCollectionStartLayout <~ applyFadeOut()) ~
      (publishCollectionInformationLayout <~ applyFadeIn()) ~
      (publishCollectionEndLayout <~ applyFadeOut()) ~
      (publishCollectionInformationCollectionName <~ tvText(collection.name)) ~
      addCategoriesToSpinner(collection) ~
      (publishCollectionInformationPublish <~ On.click(goToPublishCollectionEnd()))

  private[this] def goToPublishCollectionEnd(): Ui[Any] =
    (publishCollectionStartLayout <~ applyFadeOut()) ~
      (publishCollectionInformationLayout <~ applyFadeOut()) ~
      (publishCollectionEndLayout <~ applyFadeIn())

  private[this] def addCategoriesToSpinner(collection: Collection): Ui[Any] = {
    val selectCategory = Seq(resGetString(R.string.addInformationCategory))
    val categoryNames = (selectCategory ++ (appsCategories map getCategoryName).sorted).toArray
    val collectionCategoryName = collection.appsCategory map getCategoryName

    val sa = new ArrayAdapter[String](fragmentContextWrapper.getOriginal, android.R.layout.simple_spinner_dropdown_item, categoryNames)
    val spinnerPosition = collectionCategoryName map sa.getPosition getOrElse 0

    publishCollectionInformationCategorySpinner <~ sAdapter(sa) <~ sSelection(spinnerPosition)
  }

  private[this] def getCategoryName(category: NineCardCategory) =
    resGetString(category.getStringResource) getOrElse category.name

}

