package com.fortysevendeg.ninecardslauncher.app.ui

import android.content.Context
import macroid.ContextWrapper
import org.joda.time.DateTime

class CachePreferences(implicit contextWrapper: ContextWrapper) {

  private[this] val name = "cache-preferences"

  private[this] val timeMomentChanged = "time-moment-changed"

  private[this] lazy val cachePreferences = contextWrapper.bestAvailable.getSharedPreferences(name, Context.MODE_PRIVATE)

  def updateTimeMomentChangedManually(): Unit =
    cachePreferences.edit.putLong(timeMomentChanged, new DateTime().getMillis).apply()

  def clearTime(): Unit = cachePreferences.edit().remove(timeMomentChanged).apply()

  def canReloadMomentInResume: Boolean = {
    val defaultDate = new DateTime().minusDays(1)
    val timeChanged = new DateTime(cachePreferences.getLong(timeMomentChanged, defaultDate.getMillis))
    timeChanged.plusHours(1).isBeforeNow
  }

}
