package com.fortysevendeg.ninecardslauncher.services

import android.app.{NotificationManager, PendingIntent, Service}
import android.content.{Context, Intent}
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import com.fortysevendeg.ninecardslauncher.models._
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.repository.collection.{AddCollectionResponse, AddCollectionRequest}
import com.fortysevendeg.ninecardslauncher.modules.repository.geoInfo.AddGeoInfoRequest
import com.fortysevendeg.ninecardslauncher.services.CreateCollectionService._
import com.fortysevendeg.ninecardslauncher.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.ui.commons.{CollectionType, NineCardsMoments}
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCategories._
import com.fortysevendeg.ninecardslauncher.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Contexts, ContextWrapper}
import com.fortysevendeg.macroid.extras.ResourcesExtras._

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import android.util.Log

class CreateCollectionService
  extends Service
  with Contexts[Service]
  with ComponentRegistryImpl
  with AppConversions {

  override lazy val contextProvider: ContextWrapper = serviceContextWrapper

  val Tag = "9CARDS"

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
      response => createConfiguration()
    } recover {
      case ex: CategorizeAppsException =>
        Log.d(Tag, ex.getMessage)
        closeService()
      case _ =>
        Log.d(Tag, "Categorize apps unexpected exception")
        closeService()
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
                Log.d(Tag, "UserConfig don't created")
                closeService()
              }
          } getOrElse {
            createCollectionFromMyDevice()
          }
      } recover {
        case ex: Throwable =>
          Log.d(Tag, s"UserConfig endpoind failed: ${ex.getMessage}")
          closeService()
      }
    }) getOrElse {
    Log.d(Tag, "User unserialize failed")
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
    val request = AddGeoInfoRequest(
      constrain = constrain,
      occurrence = ocurrenceStr,
      wifi = config.wifi,
      latitude = config.lat,
      longitude = config.lng,
      system = true
    )
    geoInfoRepositoryServices.addGeoInfo(request)
  }

  private def createCollectionFromMyDevice() = appManagerServices.getCategorizedApps(GetCategorizedAppsRequest()) map {
    response =>
      val categories = Seq(Game, BooksAndReference, Business, Comics, Communication, Education,
        Entertainment, Finance, HealthAndFitness, LibrariesAndDemo, Lifestyle, AppWallpaper,
        MediaAndVideo, Medical, MusicAndAudio, NewsAndMagazines, Personalization, Photography,
        Productivity, Shopping, Social, Sports, Tools, Transportation, TravelAndLocal, Weather, AppWidgets)
      val inserts = createAddSeq(response.apps, categories, Seq.empty)
      val insertFutures = inserts map {
        insert =>
          collectionRepositoryServices.addCollection(insert)
      }
      addFuturesInDB(insertFutures)
  }

  private def createCollectionFromDevice(device: UserConfigDevice) = {
    // Store the icons for the apps not installed from internet
    val intents = device.collections flatMap (_.items map (_.metadata))
    appManagerServices.createBitmapsForNoPackagesInstalled(IntentsRequest(intents)) map {
      response =>
        // Save collection in repository
        val addFutures = toAddCollectionRequestFromUserConfigSeq(device.collections, response.packages) map collectionRepositoryServices.addCollection
        addFuturesInDB(addFutures)
    } recover {
      case _ =>
        Log.d(Tag, "Store images of apps not installed failed")
        closeService()
    }
  }

  private def addFuturesInDB(insertFutures: Seq[Future[AddCollectionResponse]]) = {
    Future.sequence(insertFutures) map {
      responses =>
        closeService()
    } recover {
      case _ =>
        Log.d(Tag, "Insert sequence failed")
        closeService()
    }
  }

  @tailrec
  private def createAddSeq(apps: Seq[AppItem], categories: Seq[String], acc: Seq[AddCollectionRequest]): Seq[AddCollectionRequest] = {
    categories match {
      case Nil => acc
      case h :: t =>
        val insert = createCollection(apps, h, acc.length)
        val a = if (insert.cards.length >= minAppsToAdd) acc :+ insert else acc
        createAddSeq(apps, t, a)
    }
  }

  private def createCollection(apps: Seq[AppItem], category: String, index: Int): AddCollectionRequest = {
    val appsCategory = apps.filter(_.category == Some(category)).sortWith(_.getMFIndex < _.getMFIndex).take(NumSpaces)
    val pos = if (index >= NumSpaces) index % NumSpaces else index
    AddCollectionRequest(
      position = pos,
      name = resGetString(category.toLowerCase).getOrElse(category.toLowerCase),
      collectionType = CollectionType.Apps,
      icon = category.toLowerCase,
      themedColorIndex = pos,
      appsCategory = Some(category),
      sharedCollectionSubscribed = Option(false),
      cards = toCartItemFromAppItemSeq(appsCategory)
    )
  }

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