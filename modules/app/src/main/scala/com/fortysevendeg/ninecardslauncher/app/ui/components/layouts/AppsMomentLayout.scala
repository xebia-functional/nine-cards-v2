package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.WorkSpaceMomentMenuTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.LauncherMoment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.TypedFindView
import macroid.FullDsl._
import macroid._

class AppsMomentLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  setOrientation(LinearLayout.VERTICAL)

  def populate(moment: LauncherMoment)(implicit theme: NineCardsTheme, presenter: LauncherPresenter): Ui[Any] = {
    (for {
      collection <- moment.collection
    } yield {
      this <~
        vgRemoveAllViews <~
        vgAddViews(createCollection(collection) +: (collection.cards map (createIconCard(_, moment.momentType))))
    }) getOrElse
      Ui.nop
  }

  private[this] def createCollection(collection: Collection)(implicit presenter: LauncherPresenter): WorkSpaceMomentIcon = {
    (w[WorkSpaceMomentIcon] <~
      vWrapContent <~
      wmmPopulateCollection(collection) <~
      On.click {
        Ui(presenter.goToMomentWorkspace())
      }).get
  }

  private[this] def createIconCard(card: Card, moment: Option[NineCardsMoment])(implicit presenter: LauncherPresenter): WorkSpaceMomentIcon =
    (w[WorkSpaceMomentIcon] <~
      vWrapContent <~
      wmmPopulateCard(card) <~
      On.click {
        Ui(presenter.openMomentIntent(card, moment))
      }).get

}
