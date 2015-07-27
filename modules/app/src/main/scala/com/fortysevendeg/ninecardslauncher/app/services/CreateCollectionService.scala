package com.fortysevendeg.ninecardslauncher.app.services

import android.app.{PendingIntent, Service}
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.WizardActivity
import macroid.Contexts

import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._

import scalaz.concurrent.Task

class CreateCollectionService
  extends Service
  with Contexts[Service]
  with ContextSupportProvider
  with CreateCollectionsTasks {

  val tag = "9CARDS"

  implicit lazy val di = new Injector

  private var loadDeviceId: Option[String] = None

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    loadDeviceId = Option(intent) flatMap {
      i => if (i.hasExtra(keyDevice)) Some(i.getStringExtra(keyDevice)) else None
    }

    val builder = new NotificationCompat.Builder(this)
    val notificationIntent: Intent = new Intent(this, classOf[WizardActivity])
    val title: String = getString(com.fortysevendeg.ninecardslauncher2.R.string.workingNotificationTitle)
    builder.
      setContentTitle(title).
      setTicker(title).
      setContentText(getString(com.fortysevendeg.ninecardslauncher2.R.string.loadingYourAppsMessage)).
      setSmallIcon(com.fortysevendeg.ninecardslauncher2.R.drawable.icon_notification_working).
      setProgress(0, 0, true).
      setContentIntent(PendingIntent.getActivity(this, getUniqueId, notificationIntent, 0))

    startForeground(notificationId, builder.build)

    val task = loadDeviceId map loadConfiguration getOrElse createNewConfiguration

    Task.fork(task).resolveAsync(
      collections => closeService(),
      ex => {
        Log.d(tag, ex.getMessage) // TODO We should show the error in UI
        closeService()
      }
    )
    super.onStartCommand(intent, flags, startId)
  }

  // TODO 9C-190 - Move synchronizeGeoInfo to UserConfigProcess

//  private def synchronizeGeoInfo(userConfig: UserConfig) = {
//    userConfig.geoInfo.homeMorning map (addUserConfigUserLocation(_, NineCardsMoments.HomeMorning))
//    userConfig.geoInfo.homeNight map (addUserConfigUserLocation(_, NineCardsMoments.HomeNight))
//    userConfig.geoInfo.work map (addUserConfigUserLocation(_, NineCardsMoments.Work))
//  }
//
//  private def addUserConfigUserLocation(config: UserConfigUserLocation, constrain: String) = {
//    if (!config.wifi.isEmpty)
//      sharedPreferences.edit.putString(HomeMorningKey, config.wifi).apply()
//
//    import play.api.libs.json._
//
//    val reads = Json.writes[UserConfigTimeSlot]
//    val ocurrenceStr: String = config.occurrence map (o => Json.toJson(o)(reads).toString()) mkString("[", ", ", "]")
//    val request = AddGeoInfoRequest(
//      constrain = constrain,
//      occurrence = ocurrenceStr,
//      wifi = config.wifi,
//      latitude = config.lat,
//      longitude = config.lng,
//      system = true
//    )
//    persistenceServices.addGeoInfo(request)
//  }

  private def closeService() = {
    stopForeground(true)
    stopSelf()
  }

  override def onBind(intent: Intent): IBinder = null

}


object CreateCollectionService {
  val keyDevice: String = "__key_device__"
  val notificationId: Int = 1101
  val homeMorningKey = "home"
}