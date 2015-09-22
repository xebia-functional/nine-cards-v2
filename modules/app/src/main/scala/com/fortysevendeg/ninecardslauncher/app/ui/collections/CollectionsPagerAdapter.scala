package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager, FragmentStatePagerAdapter}
import android.view.ViewGroup
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Card, Collection}
import macroid.{ContextWrapper, Ui}

import scala.collection.mutable

case class CollectionsPagerAdapter(fragmentManager: FragmentManager, var collections: Seq[Collection], startPosition: Int)
  (implicit context: ContextWrapper)
  extends FragmentStatePagerAdapter(fragmentManager) {

  val fragments: mutable.WeakHashMap[Int, CollectionFragment] = mutable.WeakHashMap.empty

  var scrollType = ScrollType.down

  var firstTime = false

  private[this] def firstTimeInStartPosition(position: Int) = (firstTime, position == startPosition) match {
    case (false, true) =>
      firstTime = true
      true
    case _ => false
  }

  override def getItem(position: Int): Fragment = {
    val fragment = new CollectionFragment()
    val bundle = new Bundle()
    bundle.putInt(CollectionFragment.keyPosition, position)
    bundle.putBoolean(CollectionFragment.keyAnimateCards,firstTimeInStartPosition(position))
    bundle.putSerializable(CollectionFragment.keyCollection, collections(position))
    bundle.putInt(CollectionFragment.keyScrollType, scrollType)
    fragment.setArguments(bundle)
    fragment
  }

  override def getCount: Int = collections.length

  override def getPageTitle(position: Int): CharSequence = collections(position).name

  override def instantiateItem(container: ViewGroup, position: Int): AnyRef = {
    val fragment = super.instantiateItem(container, position)
    fragments.put(position, fragment.asInstanceOf[CollectionFragment])
    fragment
  }

  override def destroyItem(container: ViewGroup, position: Int, `object`: scala.Any): Unit = {
    fragments.remove(position)
    super.destroyItem(container, position, `object`)
  }

  def addCardsInCollection(positionCollection: Int, cards: Seq[Card]) = {
    val currentCollection = collections(positionCollection)
    val newCollection = currentCollection.copy(cards = currentCollection.cards ++ cards)
    collections = collections.patch(positionCollection, Seq(newCollection), 1)
  }

  def removeCardInCollection(positionCollection: Int, card: Card) = {
    val currentCollection = collections(positionCollection)
    val newCollection = currentCollection.copy(cards = currentCollection.cards.filterNot(c => card == c))
    collections = collections.patch(positionCollection, Seq(newCollection), 1)
  }

  def getCurrentFragmentPosition: Option[Int] = fragments find (f => f._2.activeFragment) map (_._1)

  def getActiveFragment: Option[CollectionFragment] = fragments find (f => f._2.activeFragment) map (_._2)

  def activateFragment(pos: Int): Unit = fragments foreach (f => if (f._1 == pos) f._2.activeFragment = true)

  def setScrollType(sType: Int): Unit = scrollType = sType

  def notifyChanged(currentPosition: Int): Ui[_] = {
    val uis = fragments map { f =>
      f._1 match {
        case `currentPosition` =>
          f._2.activeFragment = true
          Ui.nop
        case _ =>
          f._2.activeFragment = false
          f._2.scrollType(scrollType)
      }
    }
    Ui.sequence(uis.toSeq: _*)
  }
}
