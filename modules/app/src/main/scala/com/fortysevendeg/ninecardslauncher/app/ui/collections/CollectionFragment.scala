package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionFragment._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FragmentUiContext, UiContext, UiExtensions}
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{TR, _}
import macroid.Contexts
import rapture.core.Answer

import scalaz.concurrent.Task

class CollectionFragment
  extends Fragment
  with Contexts[Fragment]
  with ContextSupportProvider
  with UiExtensions
  with TypedFindView
  with CollectionFragmentComposer {

  lazy val di = new Injector

  implicit lazy val theme: NineCardsTheme = di.themeProcess.getSelectedTheme.run.run match {
    case Answer(t) => t
    case _ => getDefaultTheme
  }

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  lazy val animateCards = getBoolean(Seq(getArguments), keyAnimateCards, default = false)

  lazy val position = getInt(Seq(getArguments), keyPosition, 0)

  lazy val collection = Option(getSerialize[Collection](Seq(getArguments), keyCollection, javaNull))

  lazy val collectionId = getInt(Seq(getArguments), keyCollectionId, 0)

  lazy val emptyCollectionLayout = Option(findView(TR.collection_detail_empty))

  lazy val emptyCollectionMessage = Option(findView(TR.collection_empty_message))

  lazy val emptyCollectionImage = Option(findView(TR.collection_empty_image))

  lazy val recyclerView = Option(findView(TR.collection_detail_recycler))

  lazy val pullToCloseView = Option(findView(TR.collection_detail_pull_to_close))

  protected var rootView: Option[View] = None

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val baseView = LayoutInflater.from(getActivity).inflate(TR.layout.collection_detail_fragment, container, false)
    rootView = Some(baseView)
    collection map { col =>
      initUi(
        animateCards = animateCards,
        collection = col,
        onMoveItems = (from: Int, to: Int) => {
          for {
            adapter <- getAdapter
            collection = adapter.collection
            activity <- activity[CollectionsDetailsActivity]
          } yield {
            Task.fork(di.collectionProcess.reorderCard(collection.id, collection.cards(to).id, to).run).resolveAsync(
              onResult = (_) => activity.reloadCards(false)
            )
          }
        },
        onRemoveItem = (position: Int) => {
          for {
            adapter <- getAdapter
            activity <- activity[CollectionsDetailsActivity]
          } yield {
            activity.removeCard(adapter.collection.cards(position))
          }
        }).run
    } getOrElse(throw new RuntimeException("Collection not found")) // TODO We should use an error screen
    baseView
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    val sType = ScrollType(getArguments.getString(keyScrollType, ScrollDown.toString))
    val canScroll = collection exists (_.cards.length > numSpaces)
    statuses = statuses.copy(scrollType = sType, canScroll = canScroll)
    collection foreach (c => showData(c.cards.isEmpty).run)
    super.onViewCreated(view, savedInstanceState)
  }

  override def onAttach(activity: Activity): Unit = {
    super.onAttach(activity)
    activity match {
      case scroll: ScrolledListener => scrolledListener = Some(scroll)
      case _ =>
    }
  }

  override def onDetach(): Unit = {
    super.onDetach()
    scrolledListener = None
  }

  def bindAnimatedAdapter() = if (animateCards) collection foreach (c => setAnimatedAdapter(c).run)

  def addCards(cards: Seq[Card]) = getAdapter foreach { adapter =>
    adapter.addCards(cards)
    updateScroll()
    val emptyCollection = adapter.collection.cards.isEmpty
    if (!emptyCollection) scrolledListener foreach (_.onFirstItemInCollection())
    (resetScroll ~ showData(emptyCollection)).run
  }

  def removeCard(card: Card) = getAdapter foreach { adapter =>
    adapter.removeCard(card)
    updateScroll()
    val emptyCollection = adapter.collection.cards.isEmpty
    if (emptyCollection) scrolledListener foreach (_.onEmptyCollection())
    (resetScroll ~ showData(emptyCollection)).run
  }

  def reloadCards(cards: Seq[Card]) = getAdapter foreach { adapter =>
    adapter.updateCards(cards)
    updateScroll()
    resetScroll.run
  }
}

object CollectionFragment {
  val keyPosition = "tab_position"
  val keyCollection = "collection"
  val keyCollectionId = "collection_id"
  val keyScrollType = "scroll_type"
  val keyAnimateCards = "animate_cards"
}

