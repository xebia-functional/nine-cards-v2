package com.fortysevendeg.ninecardslauncher.app.ui.components.commons

import android.os

trait ClicksHandler {

  private[this] val clickMillis = 300

  var pressStart: Long = 0

  private[this] val longClickMillis = 800

  private[this] val handler = new os.Handler()

  private[this] val runnable = new Runnable {
    override def run(): Unit = onLongClick()
  }

  def onLongClick(): Unit

  def startClick() = pressStart = System.currentTimeMillis()

  def resetClick() =  pressStart = 0

  def isClick = pressStart != 0 && (System.currentTimeMillis() - pressStart) < clickMillis

  def startLongClick() = handler.postDelayed(runnable, longClickMillis)

  def resetLongClick() = handler.removeCallbacks(runnable)

  def startClicks() = {
    startClick()
    startLongClick()
  }

  def resetClicks() = {
    resetClick()
    resetLongClick()
  }

}
