package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import macroid.ContextWrapper

class PullToCloseView(context: Context)(implicit contextWrapper: ContextWrapper)
  extends PullToDownView(context) {

  var closeListeners = PullToCloseListener()

  override def drop(): Unit = if (pullToDownStatuses.isValidAction) {
    closeListeners.close()
  } else {
    super.drop()
  }

}

case class PullToCloseListener(close: () => Unit = () => ())
