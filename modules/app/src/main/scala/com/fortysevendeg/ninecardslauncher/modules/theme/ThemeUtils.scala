package com.fortysevendeg.ninecardslauncher.modules.theme

import com.fortysevendeg.ninecardslauncher2.R

trait ThemeUtils {

  def getIndexColor(index: Int): Int = index match {
    case 0 => R.color.collection_group_1
    case 1 => R.color.collection_group_2
    case 2 => R.color.collection_group_3
    case 3 => R.color.collection_group_4
    case 4 => R.color.collection_group_5
    case 5 => R.color.collection_group_6
    case 6 => R.color.collection_group_7
    case 7 => R.color.collection_group_8
    case _ => R.color.collection_group_9
  }

}

object ThemeUtils extends ThemeUtils