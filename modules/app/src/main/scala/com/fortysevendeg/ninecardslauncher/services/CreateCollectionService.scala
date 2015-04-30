package com.fortysevendeg.ninecardslauncher.services

import android.app.{NotificationManager, PendingIntent, Service}
import android.content.{Context, Intent}
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.repository.{CardItem, InsertCollectionRequest, InsertGeoInfoRequest}
import com.fortysevendeg.ninecardslauncher.services.CreateCollectionService._
import com.fortysevendeg.ninecardslauncher.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.ui.commons.{CardType, CollectionType, NineCardsMoments}
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCategories._
import com.fortysevendeg.ninecardslauncher.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher2.R
import macroid.AppContext

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import macroid.Logging._

class CreateCollectionService
  extends Service
  with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = AppContext(getApplicationContext)

  private var loadDeviceId: Option[String] = None

  private val minAppsToAdd = 4

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

    appManagerServices.categorizeApps(CategorizeAppsRequest()) map {
      response =>
        if (response.success) {
          createConfiguration()
        } else {
          logD"Categorize apps doesn't work"("9CARDS")
          closeService()
        }
    }

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
                logD"UserConfig don't created"("9CARDS")
                closeService()
              }
          } getOrElse {
            createCollectionFromMyDevice()
          }
      } recover {
        case ex: Throwable =>
          logD"UserConfig endpoind failed: ${ex.getMessage}"("9CARDS")
          closeService()
      }
    }) getOrElse {
    logD"User unserialize failed"("9CARDS")
    closeService()
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
    appManagerServices.getCategorizedApps(GetCategorizedAppsRequest()) map {
      response =>
        val categories = Seq(Game, BooksAndReference, Business, Comics, Communication, Education,
          Entertainment, Finance, HealthAndFitness, LibrariesAndDemo, Lifestyle, AppWallpaper,
          MediaAndVideo, Medical, MusicAndAudio, NewsAndMagazines, Personalization, Photography,
          Productivity, Shopping, Social, Sports, Tools, Transportation, TravelAndLocal, Weather, AppWidgets)
        val inserts = createInsertSeq(response.apps, categories, Seq.empty)
        val insertFutures = inserts map {
          insert =>
            repositoryServices.insertCollection(insert)
        }
        Future.sequence(insertFutures) map {
          responses =>
            closeService()
        } recover {
          case _ =>
            logD"Insert sequence failed"("9CARDS")
            closeService()
        }

    }
  }

  private def createCollectionFromDevice(device: UserConfigDevice) = {
    // TODO Create from device
  }

  @tailrec
  private def createInsertSeq(apps: Seq[AppItem], categories: Seq[String], acc: Seq[InsertCollectionRequest]) : Seq[InsertCollectionRequest] = {
    categories match {
      case Nil => acc
      case h :: t =>
        val insert = createCollection(apps, h, acc.length)
        val a = if (insert.cards.length >= minAppsToAdd) acc :+ insert else acc
        createInsertSeq(apps, t, a)
    }
  }

  private def createCollection(apps: Seq[AppItem], category: String, index: Int): InsertCollectionRequest = {
    val appsCategory = apps.filter(_.category == Some(category)).sortWith(_.getMFIndex < _.getMFIndex).take(NumSpaces)
    InsertCollectionRequest(
      position = index % NumInLine,
      name = category,
      `type` = CollectionType.Apps,
      icon = Social.toLowerCase, // TODO Put "category.toLowerCase" when we have all icons
      themedColorIndex = index % NumInLine,
      appsCategory = Some(category),
      sharedCollectionSubscribed = false,
      cards = appsCategory map toCardItem
    )
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