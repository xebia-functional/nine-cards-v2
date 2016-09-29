package cards.nine.app.ui.collections.jobs

import android.os.Bundle
import android.support.v4.app.{DialogFragment, Fragment, FragmentActivity, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import cards.nine.app.ui.collections.{CollectionAdapter, CollectionsPagerAdapter}
import cards.nine.app.ui.commons.FabButtonTags._
import cards.nine.app.ui.commons.actions.{ActionsBehaviours, BaseActionFragment}
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.commons._
import cards.nine.process.collection.AddCardRequest
import cards.nine.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.{ActivityContextWrapper, FragmentBuilder, FragmentManagerContext, Ui}

trait GroupCollectionsDOM {

  finder: TypedFindView =>

  lazy val toolbar = findView(TR.collections_toolbar)

  lazy val toolbarTitle = findView(TR.collections_toolbar_title)

  lazy val titleContent = findView(TR.collections_title_content)

  lazy val titleName = findView(TR.collections_title_name)

  lazy val titleIcon = findView(TR.collections_title_icon)

  lazy val selector = findView(TR.collections_selector)

  lazy val root = findView(TR.collections_root)

  lazy val viewPager = findView(TR.collections_view_pager)

  lazy val tabs = findView(TR.collections_tabs)

  lazy val iconContent = findView(TR.collections_icon_content)

  lazy val icon = findView(TR.collections_icon)

  lazy val fabButton = findView(TR.fab_button)

  lazy val fabMenuContent = findView(TR.fab_menu_content)

  lazy val fabMenu = findView(TR.fab_menu)

  lazy val fragmentContent = findView(TR.action_fragment_content)

  val tagDialog = "dialog"

  def isFabButtonVisible: Boolean = fabButton.getVisibility == View.VISIBLE

  def isAutoHide: Boolean = fabButton.getField[Boolean](autoHideKey) getOrElse false

  def isMenuOpened: Boolean = fabButton.getField[Boolean](opened) getOrElse false

  def getCurrentPosition: Option[Int] = getAdapter flatMap ( _.getCurrentFragmentPosition )

  def getCurrentCollection: Option[Collection] = getAdapter flatMap { adapter =>
    adapter.getCurrentFragmentPosition flatMap adapter.collections.lift
  }

  def getCollection(position: Int): Option[Collection] = getAdapter flatMap (_.collections.lift(position))

  def getAdapter: Option[CollectionsPagerAdapter] = viewPager.getAdapter match {
    case adapter: CollectionsPagerAdapter => Some(adapter)
    case _ => None
  }

  def getScrollType: Option[ScrollType] = getAdapter map (_.statuses.scrollType)

  def getActiveCollectionAdapter: Option[CollectionAdapter] = for {
    adapter <- getAdapter
    fragment <- adapter.getActiveFragment
    collectionAdapter <- fragment.getAdapter
  } yield collectionAdapter

  def notifyItemChangedCollectionAdapter(position: Int): Unit =
    getActiveCollectionAdapter foreach(_.notifyItemChanged(position))

  def notifyDataSetChangedCollectionAdapter(): Unit =
    getActiveCollectionAdapter foreach(_.notifyDataSetChanged())

  def invalidateOptionMenu(implicit activityContextWrapper: ActivityContextWrapper): Unit = {
    activityContextWrapper.original.get match {
      case Some(activity: FragmentActivity) => activity.supportInvalidateOptionsMenu()
      case _ =>
    }
  }

  // TODO We should move this call to NavigationProcess #826
  def showDialog(dialog: DialogFragment)(implicit activityContextWrapper: ActivityContextWrapper): Unit = {
    activityContextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        dialog.show(ft, tagDialog)
      case _ =>
    }
  }

  // TODO We should move this call to NavigationProcess #826
  def launchDialog[F <: BaseActionFragment]
  (fragmentBuilder: FragmentBuilder[F], args: Bundle)(implicit fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager]): Ui[Any] = {
    fragmentBuilder.pass(args).framed(R.id.action_fragment_content, ActionsBehaviours.nameActionFragment)
  }

}

trait GroupCollectionsUiListener {

  def closeEditingMode(): Unit

  def updateScroll(dy: Int): Unit

  def isNormalMode: Boolean

  def isEditingMode: Boolean

  def showPublicCollectionDialog(collection: Collection): Unit

  def addCards(cards: Seq[AddCardRequest]): Unit

  def bindAnimatedAdapter(): Unit

  def reloadCards(cards: Seq[Card]): Unit

  def saveEditedCard(collectionId: Int, cardId: Int, cardName: Option[String]): Unit

  def showDataInPosition(position: Int): Unit

  def showAppsDialog(args: Bundle): Ui[Any]

  def showContactsDialog(args: Bundle): Ui[Any]

  def showShortcutsDialog(args: Bundle): Ui[Any]

  def showRecommendationsDialog(args: Bundle): Ui[Any]

}
