package com.fortysevendeg.ninecardslauncher.app.observers

import android.app.{AlarmManager, PendingIntent}
import android.content.{Context, Intent}
import android.database.ContentObserver
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.app.services.SynchronizeDeviceService
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import NineCardsObserver._

class NineCardsObserver(implicit contextSupport: ContextSupport)
  extends ContentObserver(javaNull) {

  lazy val preferences = contextSupport.context.getSharedPreferences(notificationPreferences, Context.MODE_PRIVATE)

  private[this] def addCollectionId(collectionId: Int) = {
    val collections = preferences.getString(collectionIdsKey, "")
    val ids: Array[Int] = collections.split(",").filterNot(_.isEmpty).map(_.toInt)
    val newIds = ids.toSet + collectionId
    preferences.edit.putString(collectionIdsKey, newIds.mkString(",")).apply()
  }

  private[this] val nextAlarmTime = 10 * 60 * 1000

  override def onChange(selfChange: Boolean, uri: Uri): Unit =
    matchUri(uri.toString) match {
      case Some((UriCollection, maybeId)) =>
        maybeId foreach addCollectionId
        createAlarm()
      case Some(_) => createAlarm()
      case _ =>
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

object NineCardsObserver {

  val notificationPreferences = "notificationPreferences"

  val collectionIdsKey = "_collectionsIds_"

  sealed trait UriType

  case object UriCard extends UriType
  case object UriCollection extends UriType
  case object UriDockApp extends UriType
  case object UriMoment extends UriType
  case object UriWidget extends UriType

  private[this] val uriRegExp = s"$baseUriNotificationString/([^/]+)(/(\\d+))?".r

  def matchUri(uri: String): Option[(UriType, Option[Int])] = {

    def readType(uriType: String): Option[UriType] = uriType match {
      case `appUriPath` => Some(UriCard)
      case `collectionUriPath` => Some(UriCollection)
      case `dockAppUriPath` => Some(UriDockApp)
      case `momentUriPath` => Some(UriMoment)
      case `widgetUriPath` => Some(UriWidget)
      case _ => None
    }

    uri match {
      case uriRegExp(uriType, _, id) => readType(uriType) map ((_, Option(id) map (_.toInt)))
      case _ => None
    }
  }

}