package com.fortysevendeg.ninecardslauncher.app.ui.components.commons

import android.os

trait LongClickHandler {

  private[this] val longClickMillis = 1000

  private[this] val handler = new os.Handler()

  private[this] val runnable = new Runnable {
    override def run(): Unit = onLongClick()
  }

  def onLongClick(): Unit

  def startLongClick() = handler.postDelayed(runnable, longClickMillis)

  def resetLongClick() = handler.removeCallbacks(runnable)

}
