package com.fortysevendeg.ninecardslauncher.app.ui

import android.content.Context
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import macroid.ContextWrapper
import org.joda.time.DateTime

class PersistMoment(implicit contextWrapper: ContextWrapper) {

  private[this] val name = "persist-moment-preferences"

  private[this] val timeMomentChangedKey = "time-moment-changed"

  private[this] val momentPersistKey = "moment-persist"

  private[this] lazy val persistMomentPreferences = contextWrapper.bestAvailable.getSharedPreferences(name, Context.MODE_PRIVATE)

  def persist(momentType: NineCardsMoment): Unit =
    persistMomentPreferences.edit.
      putLong(timeMomentChangedKey, new DateTime().getMillis).
      putString(momentPersistKey, momentType.name).
      apply()

  def clean(): Unit = persistMomentPreferences.edit().remove(timeMomentChangedKey).remove(momentPersistKey).apply()

  def nonPersist: Boolean = {
    val defaultDate = new DateTime().minusDays(1)
    val timeChanged = new DateTime(persistMomentPreferences.getLong(timeMomentChangedKey, defaultDate.getMillis))
    timeChanged.plusHours(1).isBeforeNow
  }

  def getPersistMoment: Option[NineCardsMoment] = if (nonPersist) {
    None
  } else {
    Option(persistMomentPreferences.getString(momentPersistKey, javaNull)) map (m => NineCardsMoment(m))
  }

}
