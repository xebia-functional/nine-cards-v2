package com.fortysevendeg.ninecardslauncher.app.ui.profile.ops

import com.fortysevendeg.macroid.extras.ResourcesExtras._
import cards.nine.process.sharedcollections.models.Subscription
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

object SubscriptionOps {

  implicit class SubscriptionOp(subscription: Subscription) {

    def getIconSubscriptionDetail(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${subscription.icon.toLowerCase}_detail") getOrElse R.drawable.icon_collection_default_detail

  }

}
