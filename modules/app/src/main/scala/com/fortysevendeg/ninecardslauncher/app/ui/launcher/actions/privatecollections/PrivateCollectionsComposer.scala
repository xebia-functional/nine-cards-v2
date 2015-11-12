package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, NineCardIntentConversions, UiContext}
import com.fortysevendeg.ninecardslauncher.process.collection.PrivateCollection
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

trait PrivateCollectionsComposer
  extends Styles
  with LauncherExecutor
  with NineCardIntentConversions {

  self: TypedFindView with BaseActionFragment =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  def initUi: Ui[_] =
    (toolbar <~
      tbTitle(R.string.myCollections) <~
      toolbarStyle(colorPrimary) <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)

  def showLoading: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

//  def addRecommendations(recommendations: Seq[RecommendedApp], clickListener: (RecommendedApp) => Ui[_])(implicit uiContext: UiContext[_]) = {
//    val adapter = new RecommendationsAdapter(recommendations, clickListener)
//    (recycler <~
//      vVisible <~
//      rvLayoutManager(adapter.getLayoutManager) <~
//      rvAdapter(adapter)) ~
//      (loading <~ vGone)
//  }
//
//  def onInstallNowClick(app: RecommendedApp): Ui[_] =
//    Ui {
//      launchGooglePlay(app.packageName)
//      val card = AddCardRequest(
//        term = app.title,
//        packageName = Option(app.packageName),
//        cardType = CardType.noInstalledApp,
//        intent = toNineCardIntent(app),
//        imagePath = "")
//      activity[CollectionsDetailsActivity] foreach (_.addCards(Seq(card)))
//    } ~
//      unreveal()

}

case class ViewHolderPrivateCollectionsLayoutAdapter(content: ViewGroup, clickListener: (PrivateCollection) => Ui[_])(implicit context: ActivityContextWrapper)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.private_collections_item_icon))

  lazy val name = Option(findView(TR.private_collections_item_name))

  lazy val appsRow1 = Option(findView(TR.private_collections_item_row1))

  lazy val appsRow2 = Option(findView(TR.private_collections_item_row2))

  lazy val addCollection = Option(findView(TR.private_collections_item_add_collection))

  def bind(privateCollection: PrivateCollection, position: Int)(implicit uiContext: UiContext[_]): Ui[_] = {
    (icon <~ ivUri(privateCollection.icon)) ~
      (name <~ tvText(privateCollection.name)) ~
      (content <~ vTag2(position)) ~
      (addCollection <~ On.click(clickListener(privateCollection)))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}