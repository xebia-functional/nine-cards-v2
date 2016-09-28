package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import com.fortysevendeg.ninecardslauncher.commons._
import macroid.ContextWrapper

class PullToCloseView(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends PullToDownView(context, attr, defStyleAttr) {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  var closeListeners = PullToCloseListener()

  override def drop(): Unit = if (pullToDownStatuses.isValidAction) {
    closeListeners.close()
  } else {
    super.drop()
  }

}

case class PullToCloseListener(close: () => Unit = () => ())
