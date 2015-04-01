package com.fortysevendeg.ninecardslauncher.ui.collections

import android.os.{Parcelable, Bundle}
import android.support.v4.app.{Fragment, FragmentManager, FragmentPagerAdapter}
import com.fortysevendeg.ninecardslauncher.modules.repository.Collection

class CollectionsPagerAdapter(fragmentManager: FragmentManager, collections: Seq[Collection])
  extends FragmentPagerAdapter(fragmentManager) {

  override def getItem(position: Int): Fragment = {
    val fragment = new CollectionFragment()
    val bundle = new Bundle()
    bundle.putInt(CollectionFragment.KeyPosition, position)
    bundle.putParcelable(CollectionFragment.KeyCollection, collections(position).asInstanceOf[Parcelable])
    fragment.setArguments(bundle)
    fragment
  }

  override def getCount: Int = collections.length

}
