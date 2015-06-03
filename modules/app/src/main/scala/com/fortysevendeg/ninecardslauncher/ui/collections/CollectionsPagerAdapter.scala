package com.fortysevendeg.ninecardslauncher.ui.collections

import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager, FragmentStatePagerAdapter}
import android.view.ViewGroup
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection
import macroid.{ContextWrapper, Ui}

import scala.collection.mutable.WeakHashMap

case class CollectionsPagerAdapter(fragmentManager: FragmentManager, collections: Seq[Collection])(implicit context: ContextWrapper)
  extends FragmentStatePagerAdapter(fragmentManager) {

  val fragments : WeakHashMap[Int, CollectionFragment] = WeakHashMap.empty

  var scrollType = ScrollType.Down

  override def getItem(position: Int): Fragment = {
    val fragment = new CollectionFragment()
    val bundle = new Bundle()
    bundle.putInt(CollectionFragment.KeyPosition, position)
    bundle.putSerializable(CollectionFragment.KeyCollection, collections(position))
    bundle.putInt(CollectionFragment.KeyScrollType, scrollType)
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

  def activateFragment(pos: Int) = {
    for (f <- fragments) {
      if (f._1 == pos) {
        f._2.activeFragment = true
      }
    }
  }

  def setScrollType(sType: Int) = scrollType = sType

  def notifyChanged(currentPosition: Int): Ui[_] = {
    var uis: Ui[_] = Ui.nop
    for (f <- fragments) {
      if (f._1 == currentPosition) {
        f._2.activeFragment = true
      } else {
        f._2.activeFragment = false
        uis = uis ~ f._2.scrollType(scrollType)
      }
    }
    uis
  }
}
