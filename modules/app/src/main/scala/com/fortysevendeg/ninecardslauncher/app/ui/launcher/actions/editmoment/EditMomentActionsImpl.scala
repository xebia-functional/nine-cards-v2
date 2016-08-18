package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.editmoment

import android.widget.ArrayAdapter
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.SpinnerTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, Moment}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait EditMomentActionsImpl
  extends EditMomentActions
  with Styles{

  self: TypedFindView with BaseActionFragment =>

  implicit val editPresenter: EditMomentPresenter

  lazy val momentCollection = findView(TR.edit_moment_collection)

  override def initialize(moment: Moment, collections: Seq[Collection]): Ui[Any] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.editMoment) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (fab <~
        fabButtonMenuStyle(colorPrimary) <~
        On.click(Ui(editPresenter.saveMoment()))) ~
      loadCategories(moment, collections)

  override def momentNoFound(): Ui[Any] = unreveal()

  override def success(): Ui[Any] = unreveal()

  override def showSavingMomentErrorMessage(): Ui[Any] = uiShortToast(R.string.contactUsError)

  private[this] def loadCategories(moment: Moment, collections: Seq[Collection]): Ui[Any] = {
    val collectionIds = 0 +: (collections map (_.id))
    val collectionNames = resGetString(R.string.noLinkCollectionToMoment) +: (collections map (_.name))
    val sa = new ArrayAdapter[String](fragmentContextWrapper.getOriginal, android.R.layout.simple_spinner_dropdown_item, collectionNames.toArray)

    val spinnerPosition = moment.collectionId map collectionIds.indexOf getOrElse -1

    momentCollection <~
      sAdapter(sa) <~
      sItemSelectedListener((position) => editPresenter.setCollectionId(collectionIds.lift(position))) <~
      (if (spinnerPosition > 0) sSelection(spinnerPosition) else Tweak.blank)
  }

}
