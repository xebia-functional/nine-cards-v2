package cards.nine.app.ui.collections.jobs

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import cards.nine.app.ui.collections.{CollectionAdapter, CollectionsPagerAdapter}
import cards.nine.app.ui.commons.ActivityFindViews
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.models.{Card, CardData, Collection}
import com.fortysevendeg.ninecardslauncher.TR
import macroid.{ActivityContextWrapper, Ui}

class GroupCollectionsDOM(activity: Activity) {

  import ActivityFindViews._

  val fabButtonItem = "fab_button"

  val opened = "opened"

  val autoHideKey = "autoHide"

  lazy val toolbar = findView(TR.collections_toolbar).run(activity)

  lazy val toolbarTitle = findView(TR.collections_toolbar_title).run(activity)

  lazy val titleContent = findView(TR.collections_title_content).run(activity)

  lazy val titleName = findView(TR.collections_title_name).run(activity)

  lazy val titleIcon = findView(TR.collections_title_icon).run(activity)

  lazy val selector = findView(TR.collections_selector).run(activity)

  lazy val root = findView(TR.collections_root).run(activity)

  lazy val viewPager = findView(TR.collections_view_pager).run(activity)

  lazy val tabs = findView(TR.collections_tabs).run(activity)

  lazy val iconContent = findView(TR.collections_icon_content).run(activity)

  lazy val icon = findView(TR.collections_icon).run(activity)

  lazy val fabButton = findView(TR.fab_button).run(activity)

  lazy val fabMenuContent = findView(TR.fab_menu_content).run(activity)

  lazy val fabMenu = findView(TR.fab_menu).run(activity)

  lazy val fragmentContent = findView(TR.action_fragment_content).run(activity)

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

}

trait GroupCollectionsUiListener {

  def closeEditingMode(): Unit

  def updateScroll(dy: Int): Unit

  def isNormalMode: Boolean

  def isEditingMode: Boolean

  def showPublicCollectionDialog(collection: Collection): Unit

  def showEditCollectionDialog(cardName: String, onChangeName: (Option[String]) => Unit): Unit

  def addCards(cards: Seq[CardData]): Unit

  def bindAnimatedAdapter(): Unit

  def reloadCards(cards: Seq[Card]): Unit

  def saveEditedCard(collectionId: Int, cardId: Int, cardName: Option[String]): Unit

  def showDataInPosition(position: Int): Unit

  def showAppsDialog(args: Bundle): Ui[Any]

  def showContactsDialog(args: Bundle): Ui[Any]

  def showShortcutsDialog(args: Bundle): Ui[Any]

  def showRecommendationsDialog(args: Bundle): Ui[Any]

}
