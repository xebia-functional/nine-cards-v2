package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionFragment._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FragmentUiContext, UiContext, UiExtensions}
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{TR, _}
import macroid.Contexts

import scala.language.postfixOps

class CollectionFragment
  extends Fragment
  with Contexts[Fragment]
  with ContextSupportProvider
  with UiExtensions
  with TypedFindView
  with CollectionUiActionsImpl { self =>

  val badActivityMessage = "CollectionFragment only can be loaded in CollectionsDetailsActivity"

  override lazy val presenter = CollectionPresenter(
    animateCards = getBoolean(Seq(getArguments), keyAnimateCards, default = false),
    maybeCollection = Option(getSerialize[Collection](Seq(getArguments), keyCollection, javaNull)),
    actions = self)

  override lazy val groupCollectionsJobs: GroupCollectionsJobs = getActivity match {
    case activity: CollectionsDetailsActivity => activity.groupCollectionsJobs
    case _ => throw new IllegalArgumentException(badActivityMessage)
  }

  override lazy val toolbarJobs: ToolbarJobs = getActivity match {
    case activity: CollectionsDetailsActivity => activity.toolbarJobs
    case _ => throw new IllegalArgumentException(badActivityMessage)
  }

  override lazy val theme: NineCardsTheme = presenter.getTheme

  override lazy val uiContext: UiContext[Fragment] = FragmentUiContext(self)

  protected var rootView: Option[View] = None

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val baseView = LayoutInflater.from(getActivity).inflate(TR.layout.collection_detail_fragment, container, false)
    rootView = Some(baseView)
    baseView
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    val sType = ScrollType(getArguments.getString(keyScrollType, ScrollDown.toString))
    presenter.initialize(sType)
    presenter.showData()
    super.onViewCreated(view, savedInstanceState)
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    inflater.inflate(R.menu.collection_edit_menu, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override def onPrepareOptionsMenu(menu: Menu): Unit = {
    super.onPrepareOptionsMenu(menu)
    (groupCollectionsJobs.statuses.collectionMode, groupCollectionsJobs.statuses.positionsEditing.toSeq.length) match {
      case (NormalCollectionMode, _) =>
        groupCollectionsJobs.statuses.publishStatus match {
          case PublishedByMe =>
            menu.findItem(R.id.action_make_public).setEnabled(false).setTitle(resGetString(R.string.alreadyPublishedCollection))
            menu.findItem(R.id.action_share).setVisible(true)
          case _ =>
            menu.findItem(R.id.action_make_public).setVisible(true)
            menu.findItem(R.id.action_share).setVisible(false)
        }
        menu.findItem(R.id.action_edit).setVisible(false)
        menu.findItem(R.id.action_move_to_collection).setVisible(false)
        menu.findItem(R.id.action_delete).setVisible(false)
      case (EditingCollectionMode, 1) =>
        menu.findItem(R.id.action_make_public).setVisible(false)
        menu.findItem(R.id.action_share).setVisible(false)
        menu.findItem(R.id.action_edit).setVisible(true)
        menu.findItem(R.id.action_move_to_collection).setVisible(true)
        menu.findItem(R.id.action_delete).setVisible(true)
      case (EditingCollectionMode, _) =>
        menu.findItem(R.id.action_make_public).setVisible(false)
        menu.findItem(R.id.action_share).setVisible(false)
        menu.findItem(R.id.action_edit).setVisible(false)
        menu.findItem(R.id.action_move_to_collection).setVisible(true)
        menu.findItem(R.id.action_delete).setVisible(true)
    }
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case R.id.action_edit =>
      groupCollectionsJobs.editCard().resolveAsync()
      true
    case R.id.action_move_to_collection =>
      presenter.moveToCollection()
      true
    case R.id.action_delete =>
      groupCollectionsJobs.removeCards().resolveAsync()
      true
    case _ => super.onOptionsItemSelected(item)
  }

}

object CollectionFragment {
  val keyPosition = "tab_position"
  val keyCollection = "collection"
  val keyCollectionId = "collection_id"
  val keyScrollType = "scroll_type"
  val keyAnimateCards = "animate_cards"
}

