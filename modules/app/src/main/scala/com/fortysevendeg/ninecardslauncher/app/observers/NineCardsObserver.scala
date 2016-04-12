package com.fortysevendeg.ninecardslauncher.app.observers

import android.app.{AlarmManager, PendingIntent}
import android.content.{Context, Intent}
import android.database.ContentObserver
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.app.services.SynchronizeDeviceService
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

class NineCardsObserver(implicit contextSupport: ContextSupport)
  extends ContentObserver(javaNull) {

  private[this] lazy val nextAlarmTime = 10 * 60 * 1000

  private[this] lazy val syncUris = Seq(
    cardUriNotificationString,
    collectionUriNotificationString,
    dockAppUriNotificationString,
    momentUriNotificationString)

  override def onChange(selfChange: Boolean, uri: Uri): Unit =
    if (syncUris.contains(uri.toString)) {
      createAlarm()
    }

  private[this] def createAlarm(): Unit =
    maybeAlarmManager foreach { alarmManager =>
      val nextAlarm = System.currentTimeMillis() + nextAlarmTime
      val pendingIntent = PendingIntent.getService(
        contextSupport.context,
        0,
        new Intent(contextSupport.context, classOf[SynchronizeDeviceService]),
        PendingIntent.FLAG_CANCEL_CURRENT)

      alarmManager.set(
        AlarmManager.RTC,
        nextAlarm,
        pendingIntent)
    }

  private[this] def maybeAlarmManager: Option[AlarmManager] =
    contextSupport.context.getSystemService(Context.ALARM_SERVICE) match {
      case a: AlarmManager => Some(a)
      case _ => None
    }
}
