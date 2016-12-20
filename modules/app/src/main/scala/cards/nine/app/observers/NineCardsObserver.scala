package cards.nine.app.observers

import android.app.AlarmManager
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import cards.nine.app.observers.NineCardsObserver._
import cards.nine.app.services.sync.SynchronizeDeviceService
import cards.nine.commons._
import cards.nine.commons.contentresolver.NotificationUri._
import cards.nine.commons.contexts.ContextSupport

class NineCardsObserver(implicit contextSupport: ContextSupport)
    extends ContentObserver(javaNull) {

  lazy val preferences =
    contextSupport.context.getSharedPreferences(notificationPreferences, Context.MODE_PRIVATE)

  private[this] def addCollectionId(collectionId: Int) = {
    val collections = preferences.getString(collectionIdsKey, "")
    val ids: Array[Int] =
      collections.split(",").filterNot(_.isEmpty).map(_.toInt)
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
      case _       =>
    }

  private[this] def createAlarm(): Unit =
    contextSupport.getAlarmManager foreach { alarmManager =>
      val nextAlarm = System.currentTimeMillis() + nextAlarmTime
      alarmManager.set(AlarmManager.RTC, nextAlarm, SynchronizeDeviceService.pendingIntent)
    }
}

object NineCardsObserver {

  val notificationPreferences = "notificationPreferences"

  val collectionIdsKey = "_collectionsIds_"

  sealed trait UriType

  case object UriCard       extends UriType
  case object UriCollection extends UriType
  case object UriDockApp    extends UriType
  case object UriMoment     extends UriType
  case object UriWidget     extends UriType

  private[this] val uriRegExp =
    s"$baseUriNotificationString/([^/]+)(/(\\d+))?".r

  def matchUri(uri: String): Option[(UriType, Option[Int])] = {

    def readType(uriType: String): Option[UriType] = uriType match {
      case `appUriPath`        => Some(UriCard)
      case `collectionUriPath` => Some(UriCollection)
      case `dockAppUriPath`    => Some(UriDockApp)
      case `momentUriPath`     => Some(UriMoment)
      case `widgetUriPath`     => Some(UriWidget)
      case _                   => None
    }

    uri match {
      case uriRegExp(uriType, _, id) =>
        readType(uriType) map ((_, Option(id) map (_.toInt)))
      case _ => None
    }
  }

}
