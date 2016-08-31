package com.fortysevendeg.ninecardslauncher.app.ui.commons.ops

import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

object NineCardsCategoryOps {

  implicit class NineCardCategoryOp(nineCardCategory: NineCardCategory) {

    def getIconCollectionWorkspace(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${nineCardCategory.getIconResource}") getOrElse R.drawable.icon_collection_default

    def getIconCollectionDetail(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${nineCardCategory.getIconResource}_detail") getOrElse R.drawable.icon_collection_default_detail

    def getName(implicit contextWrapper: ContextWrapper): String =
      resGetString(nineCardCategory.getStringResource) getOrElse nineCardCategory.name

  }

}
