package com.fortysevendeg.ninecardslauncher.services

import android.app.{PendingIntent, NotificationManager, Service}
import android.content.{Context, Intent}
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.{AppItem, GetAppsByCategoryRequest}
import com.fortysevendeg.ninecardslauncher.modules.repository.{CardItem, InsertCollectionRequest, InsertGeoInfoRequest}
import com.fortysevendeg.ninecardslauncher.repository.model.CollectionData
import com.fortysevendeg.ninecardslauncher.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher2.R
import macroid.AppContext
import CreateCollectionService._
import com.fortysevendeg.ninecardslauncher.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCardsMoments
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.fortysevendeg.ninecardslauncher.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCategories._
import com.fortysevendeg.ninecardslauncher.ui.commons.CollectionType
import com.fortysevendeg.ninecardslauncher.ui.commons.CardType

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
    userConfig.geoInfo.homeMorning map (addUserConfigUserLocation(_, NineCardsMoments.HomeMorning))
    userConfig.geoInfo.homeNight map (addUserConfigUserLocation(_, NineCardsMoments.HomeNight))
    userConfig.geoInfo.work map (addUserConfigUserLocation(_, NineCardsMoments.Work))
  }

  private def addUserConfigUserLocation(config: UserConfigUserLocation, constrain: String) = {
    if (!config.wifi.isEmpty)
      sharedPreferences.edit.putString(HomeMorningKey, config.wifi).apply()
    import play.api.libs.json._
    val reads = Json.writes[UserConfigTimeSlot]
    val ocurrenceStr: String = config.occurrence map (o => (Json.toJson(o)(reads)).toString()) mkString("[", ", ", "]")
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
    val collectionFutures = Seq(
      createCollection(Game),
      createCollection(BooksAndReference),
      createCollection(Business),
      createCollection(Comics),
      createCollection(Communication),
      createCollection(Education),
      createCollection(Entertainment),
      createCollection(Finance),
      createCollection(HealthAndFitness),
      createCollection(LibrariesAndDemo),
      createCollection(Lifestyle),
      createCollection(AppWallpaper),
      createCollection(MediaAndVideo),
      createCollection(Medical),
      createCollection(MusicAndAudio),
      createCollection(NewsAndMagazines),
      createCollection(Personalization),
      createCollection(Photography),
      createCollection(Productivity),
      createCollection(Shopping),
      createCollection(Social),
      createCollection(Sports),
      createCollection(Tools),
      createCollection(Transportation),
      createCollection(TravelAndLocal),
      createCollection(Weather),
      createCollection(AppWidgets)
    )
    Future.sequence(collectionFutures) map {
      inserts =>
        val insertFutures = inserts map {
          insert =>
            repositoryServices.insertCollection(insert)
        }
        Future.sequence(insertFutures) map {
          responses =>
            closeService()
        } recover {
          case _ => // TODO handler exception
        }
    } recover {
      case _ => // TODO handler exception
    }
  }

  private def createCollectionFromDevice(device: UserConfigDevice) = {
    // TODO Create from device
  }

  private def createCollection(category: String): Future[InsertCollectionRequest] = {
    appManagerServices.getAppsByCategory(GetAppsByCategoryRequest(category)) map {
      response =>
        val apps = response.apps.sortWith(_.getMFIndex < _.getMFIndex).drop(NumSpaces)
        InsertCollectionRequest(
          position = 0,
          name = category,
          `type` = CollectionType.Apps,
          icon = category.toLowerCase,
          themedColorIndex = 0,
          appsCategory = Some(category),
          sharedCollectionSubscribed = false,
          cards = apps map toCardItem
        )
    }
  }

  private def toCardItem(appItem: AppItem) =
    CardItem(
      position = 0,
      packageName = Some(appItem.packageName),
      term = appItem.name,
      imagePath = appItem.imagePath,
      intent = appItem.intent,
      `type` = CardType.App
    )

  private def closeService() = {
    stopForeground(true)
    stopSelf()
  }

  override def onBind(intent: Intent): IBinder = null

}


object CreateCollectionService {
  val KeyDevice: String = "__key_device__"
  val NotificationId: Int = 1101
  val NotificationErrorId: Int = 1111

  val HomeMorningKey = "home"
}