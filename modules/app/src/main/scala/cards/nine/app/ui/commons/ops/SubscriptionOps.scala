package cards.nine.app.ui.commons.ops

import cards.nine.models.Subscription
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

object SubscriptionOps {

  implicit class SubscriptionOp(subscription: Subscription) {

    def getIconSubscriptionDetail(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${subscription.icon.toLowerCase}_detail") getOrElse R.drawable.icon_collection_default_detail

  }

}
