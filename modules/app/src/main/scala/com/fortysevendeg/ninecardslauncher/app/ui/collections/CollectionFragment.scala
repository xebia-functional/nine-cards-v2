package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionFragment._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FragmentUiContext, UiContext, UiExtensions}
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{TR, _}
import macroid.Contexts

class CollectionFragment(implicit collectionsPagerPresenter: CollectionsPagerPresenter, nineCardsTheme: NineCardsTheme)
  extends Fragment
  with Contexts[Fragment]
  with ContextSupportProvider
  with UiExtensions
  with TypedFindView
  with CollectionUiActionsImpl { self =>

  override lazy val presenter = new CollectionPresenter(
    animateCards = getBoolean(Seq(getArguments), keyAnimateCards, default = false),
    maybeCollection = Option(getSerialize[Collection](Seq(getArguments), keyCollection, javaNull)),
    actions = self)

  override val collectionsPresenter: CollectionsPagerPresenter = collectionsPagerPresenter

  override val theme = nineCardsTheme

  override lazy val uiContext: UiContext[Fragment] = FragmentUiContext(self)

  protected var rootView: Option[View] = None

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull

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

}

object CollectionFragment {
  val keyPosition = "tab_position"
  val keyCollection = "collection"
  val keyCollectionId = "collection_id"
  val keyScrollType = "scroll_type"
  val keyAnimateCards = "animate_cards"
}

