package com.fortysevendeg.ninecardslauncher.app.services

import android.app.{IntentService, Service}
import android.content.{Context, Intent}
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadAction, BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.di.InjectorImpl
import com.fortysevendeg.ninecardslauncher.app.observers.NineCardsObserver._
import com.fortysevendeg.ninecardslauncher.app.services.commons.GoogleDriveApiClientService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SyncDeviceState
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.cloud.Conversions._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppCardType
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.UpdateSharedCollection
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.{ImplicitsSharedCollectionsExceptions, SharedCollectionsExceptions}
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient
import macroid.Contexts
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._

import scalaz.concurrent.Task

class SynchronizeDeviceService
  extends IntentService("synchronizeDeviceService")
  with Contexts[Service]
  with ContextSupportProvider
  with GoogleDriveApiClientService
  with BroadcastDispatcher
  with ImplicitsSharedCollectionsExceptions { self =>

  import SyncDeviceState._

  implicit lazy val di = new InjectorImpl

  lazy val preferences = contextSupport.context.getSharedPreferences(notificationPreferences, Context.MODE_PRIVATE)

  private var currentState: Option[String] = None

  override def onHandleIntent(intent: Intent): Unit = {
    registerDispatchers

    Task.fork(updateCollections().value).resolveAsync()

    synchronizeDevice
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher
  }

  override val actionsFilters: Seq[String] = SyncActionFilter.cases map (_.action)

  override def manageQuestion(action: String): Option[BroadAction] = SyncActionFilter(action) match {
    case SyncAskActionFilter => Option(BroadAction(SyncAnswerActionFilter.action, currentState))
    case _ => None
  }

  override def connected(client: GoogleApiClient): Unit = {

    def sync(
      client: GoogleApiClient): TaskService[Unit] = {
      val cloudStorageProcess = di.createCloudStorageProcess(client)
      for {
        collections <- di.collectionProcess.getCollections
        moments <- di.momentProcess.getMoments
        dockApps <- di.deviceProcess.getDockApps
        savedDevice <- cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          collections = collections map toCloudStorageCollection,
          moments = moments.filter(_.collectionId.isEmpty) map toCloudStorageMoment,
          dockApps = dockApps map toCloudStorageDockApp)
        _ <- di.userProcess.updateUserDevice(savedDevice.data.deviceName, savedDevice.cloudId)
      } yield ()
    }

    Task.fork(sync(client).value).resolveAsync(
      _ => sendStateAndFinish(stateSuccess),
      throwable => {
        error(
          message = getString(R.string.errorConnectingGoogle),
          maybeException = Some(throwable))
      })
  }

  override def error(message: String, maybeException: Option[Throwable] = None) = {
    maybeException foreach (ex => printErrorMessage(ex))
    sendStateAndFinish(stateFailure)
  }

  private[this] def updateCollections() = {

    def updateCollection(collectionId: Int) = {

      def updateSharedCollection(collection: Collection): TaskService[Option[String]] =
        collection.sharedCollectionId match {
          case Some(id) =>
            di.sharedCollectionsProcess.updateSharedCollection(
              UpdateSharedCollection(
                sharedCollectionId = id,
                name = collection.name,
                description = None,
                packages = collection.cards.filter(_.cardType == AppCardType).flatMap(_.packageName))).map(Option(_))
          case _ => services.TaskService(Task(Xor.right(None)))
        }

      for {
        collection <- di.collectionProcess.getCollectionById(collectionId).resolveOption()
        _ <- updateSharedCollection(collection)
      } yield ()
    }

    val ids = preferences.getString(collectionIdsKey, "").split(",")
    val updateServices = ids filterNot (_.isEmpty) map (id => updateCollection(id.toInt).value)
    preferences.edit().remove(collectionIdsKey).apply()

    services.TaskService {
      Task.gatherUnordered(updateServices, exceptionCancels = false) map { results =>
        XorCatchAll[SharedCollectionsExceptions](results.collect { case Xor.Right(r) => r })
      }
    }
  }

  private[this] def sendStateAndFinish(state: String) = {

    def closeService() = {
      stopForeground(true)
      stopSelf()
    }

    currentState = Option(state)
    self ! BroadAction(SyncStateActionFilter.action, currentState)
    closeService()
  }

}