/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
