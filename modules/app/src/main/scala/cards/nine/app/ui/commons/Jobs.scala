package cards.nine.app.ui.commons

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import cards.nine.app.commons.BroadcastDispatcher._
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.di.{Injector, InjectorImpl}
import cards.nine.app.ui.preferences.commons.Theme
import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.NineCardsTheme
import cards.nine.process.cloud.Conversions._
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ContextWrapper


class Jobs(implicit contextWrapper: ContextWrapper)
  extends ContextSupportProvider
  with ImplicitsJobExceptions {

  implicit lazy val di: Injector = new InjectorImpl

  def getThemeTask: TaskService[NineCardsTheme] =
    di.themeProcess.getTheme(Theme.getThemeFile)

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