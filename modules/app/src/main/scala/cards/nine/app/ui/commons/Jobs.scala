package cards.nine.app.ui.commons

import android.content.Intent
import android.content.Intent._
import android.graphics.{Bitmap, BitmapFactory}
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cards.nine.app.commons.BroadcastDispatcher._
import cards.nine.app.commons.{ContextSupportProvider, Conversions}
import cards.nine.app.di.{Injector, InjectorImpl}
import cards.nine.app.ui.preferences.commons.Theme
import cards.nine.commons._
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.ShortcutCardType
import cards.nine.models.{Card, CardData, NineCardsTheme}
import cards.nine.process.cloud.Conversions._
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ContextWrapper

import scala.util.Try


class Jobs(implicit contextWrapper: ContextWrapper)
  extends ContextSupportProvider
  with ImplicitsJobExceptions {

  implicit lazy val di: Injector = new InjectorImpl

  def themeFile = Theme.getThemeFile

  def getThemeTask: TaskService[NineCardsTheme] =
    di.themeProcess.getTheme(themeFile)

  def sendBroadCastTask(broadAction: BroadAction): TaskService[Unit] =
    TaskService(CatchAll[JobException](sendBroadCast(commandType, broadAction)))

  def askBroadCastTask(broadAction: BroadAction): TaskService[Unit] =
    TaskService(CatchAll[JobException](sendBroadCast(questionType, broadAction)))

  private[this] def sendBroadCast(
    broadCastKeyType: String,
    broadAction: BroadAction): Unit = {
    val intent = new Intent(broadAction.action)
    intent.putExtra(keyType, broadCastKeyType)
    broadAction.command foreach (d => intent.putExtra(keyCommand, d))
    contextWrapper.bestAvailable.sendBroadcast(intent)
  }

  def withActivityTask(f: (AppCompatActivity => Unit)): TaskService[Unit] =
    withActivity(activity => TaskService(CatchAll[JobException](f(activity))))

  def withActivity(f: (AppCompatActivity => TaskService[Unit])): TaskService[Unit] =
    contextWrapper.original.get match {
      case Some(activity: AppCompatActivity) => f(activity)
      case _ => TaskService.empty
    }

  def readIntValue(i: Intent, key: String): Option[Int] =
    if (i.hasExtra(key)) Option(i.getIntExtra(key, 0)) else None

  def readStringValue(i: Intent, key: String): Option[String] =
    if (i.hasExtra(key)) Option(i.getStringExtra(key)) else None

  def readArrayValue(i: Intent, key: String): Option[Array[String]] =
    if (i.hasExtra(key)) Option(i.getStringArrayExtra(key)) else None

}

case class BroadAction(action: String, command: Option[String] = None)

class SynchronizeDeviceJobs(implicit contextWrapper: ContextWrapper)
  extends Jobs {

  def synchronizeDevice(client: GoogleApiClient): TaskService[Unit] = {
    for {
      collections <- di.collectionProcess.getCollections.resolveRight { seq =>
        if (seq.isEmpty) Left(JobException("Can't synchronize the device, no collections found")) else Right(seq)
      }
      moments <- di.momentProcess.getMoments
      widgets <- di.widgetsProcess.getWidgets
      dockApps <- di.deviceProcess.getDockApps
      cloudStorageMoments = moments.filter(_.collectionId.isEmpty) map { moment =>
        val widgetSeq = widgets.filter(_.momentId == moment.id) match {
          case wSeq if wSeq.isEmpty => None
          case wSeq => Some(wSeq)
        }
        toCloudStorageMoment(moment, widgetSeq)
      }
      firebaseToken <- di.externalServicesProcess.readFirebaseToken.map(token => Option(token)).resolveLeftTo(None)
      savedDevice <- di.cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
        client = client,
        collections = collections map (collection => toCloudStorageCollection(collection, collection.moment map (moment => widgets.filter(_.momentId == moment.id)))),
        moments = cloudStorageMoments,
        dockApps = dockApps map toCloudStorageDockApp)
      _ <- di.userProcess.updateUserDevice(savedDevice.data.deviceName, savedDevice.cloudId, firebaseToken)
    } yield ()
  }

}

class ShortcutJobs(implicit contextWrapper: ContextWrapper)
  extends Jobs
  with Conversions {

  def addNewShortcut(collectionId: Int, data: Intent): TaskService[Card] = {

    def getBitmapFromShortcutIntent(bundle: Bundle): Option[Bitmap] = bundle match {
      case b if b.containsKey(EXTRA_SHORTCUT_ICON) =>
        Try(b.getParcelable[Bitmap](EXTRA_SHORTCUT_ICON)).toOption
      case b if b.containsKey(EXTRA_SHORTCUT_ICON_RESOURCE) =>
        val extra = Try(b.getParcelable[ShortcutIconResource](EXTRA_SHORTCUT_ICON_RESOURCE)).toOption
        extra flatMap { e =>
          val resources = contextWrapper.bestAvailable.getPackageManager.getResourcesForApplication(e.packageName)
          val id = resources.getIdentifier(e.resourceName, javaNull, javaNull)
          Option(BitmapFactory.decodeResource(resources, id))
        }
      case _ => None
    }

    def createShortcut(name: String, shortcutIntent: Intent, bitmap: Option[Bitmap]): TaskService[Option[Card]] = for {
      path <- bitmap map (di.deviceProcess.saveShortcutIcon(_).map(Option(_))) getOrElse TaskService.right(None)
      cardData = CardData(
        term = name,
        packageName = None,
        cardType = ShortcutCardType,
        intent = toNineCardIntent(shortcutIntent),
        imagePath = path)
      cards <- di.collectionProcess.addCards(collectionId, Seq(cardData))
    } yield cards.headOption

    Option(data) flatMap (i => Option(i.getExtras)) match {
      case Some(b: Bundle) if b.containsKey(EXTRA_SHORTCUT_NAME) && b.containsKey(EXTRA_SHORTCUT_INTENT) =>
        val shortcutName = b.getString(EXTRA_SHORTCUT_NAME)
        val shortcutIntent = b.getParcelable[Intent](EXTRA_SHORTCUT_INTENT)
        val maybeBitmap = getBitmapFromShortcutIntent(b)
        createShortcut(shortcutName, shortcutIntent, maybeBitmap).resolveRight {
          case Some(card) => Right(card)
          case None => Left(JobException(s"Error creating card for intent $shortcutName"))
        }
    }
  }

}