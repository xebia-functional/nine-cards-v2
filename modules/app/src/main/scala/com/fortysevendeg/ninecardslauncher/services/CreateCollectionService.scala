package com.fortysevendeg.ninecardslauncher.services

import android.app.{PendingIntent, NotificationManager, Service}
import android.content.{Context, Intent}
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.repository.InsertGeoInfoRequest
import com.fortysevendeg.ninecardslauncher.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher2.R
import macroid.AppContext
import CreateCollectionService._
import com.fortysevendeg.ninecardslauncher.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCardsMoments._
import scala.concurrent.ExecutionContext.Implicits.global

class CreateCollectionService
  extends Service
  with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = AppContext(getApplicationContext)

  private var loadDeviceId: Option[String] = None

  private lazy val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext)

  private lazy val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    loadDeviceId = Option(intent) flatMap {
      i => if (i.hasExtra(KeyDevice)) Some(i.getStringExtra(KeyDevice)) else None
    }

    val builder = new NotificationCompat.Builder(this)
    val notificationIntent: Intent = new Intent(this, classOf[WizardActivity])
    val title: String = getString(R.string.workingNotificationTitle)
    builder.
      setContentTitle(title).
      setTicker(title).
      setContentText(getString(R.string.loadingYourAppsMessage)).
      setSmallIcon(R.drawable.icon_notification_working).
      setProgress(0, 0, true).
      setContentIntent(PendingIntent.getActivity(this, getUniqueId, notificationIntent, 0))

    startForeground(NotificationId, builder.build)

    super.onStartCommand(intent, flags, startId)
  }

  private def createConfiguration() = (for {
    user <- userServices.getUser
    token <- user.sessionToken
    androidId <- userServices.getAndroidId
  } yield {
    apiServices.getUserConfig(GetUserConfigRequest(androidId, token)) map {
      response =>
        response.userConfig map synchronizeGeoInfo
        loadDeviceId map {
          id =>
            (for {
              userConfig <- response.userConfig
              device <- userConfig.devices.find(_.deviceId == id)
            } yield {
                createCollectionFromDevice(device)
            }) getOrElse {
              // TODO handler exception
            }
        } getOrElse {
          createCollectionFromMyDevice()
        }
    } recover {
      case _ => // TODO handler exception
    }
  }) getOrElse {
    // TODO handler exception
  }

  private def synchronizeGeoInfo(userConfig: UserConfig) = {
    userConfig.geoInfo.homeMorning map (addUserConfigUserLocation(_, HomeMorning))
    userConfig.geoInfo.homeNight map (addUserConfigUserLocation(_, HomeNight))
    userConfig.geoInfo.work map (addUserConfigUserLocation(_, Work))
  }

  private def addUserConfigUserLocation(config: UserConfigUserLocation, constrain: String) = {
    if (!config.wifi.isEmpty)
      sharedPreferences.edit.putString(HomeMoningKey, config.wifi).apply()
    import play.api.libs.json._
    val reads = Json.writes[UserConfigTimeSlot]
    val ocurrenceStr: String = config.occurrence map(o => (Json.toJson(o)(reads)).toString()) mkString("[", ", ", "]")
    val request = InsertGeoInfoRequest(
      constrain = constrain,
      occurrence = ocurrenceStr,
      wifi = config.wifi,
      latitude = config.lat,
      longitude = config.lng,
      system = true
    )
    repositoryServices.insertGeoInfo(request)
  }

  private def createCollectionFromMyDevice() = {

  }

  private def createCollectionFromDevice(device: UserConfigDevice) = {

  }

  override def onBind(intent: Intent): IBinder = null

}


object CreateCollectionService {
  val KeyDevice: String = "__key_device__"
  val NotificationId: Int = 1101
  val NotificationErrorId: Int = 1111

  val HomeMoningKey = "home"
}