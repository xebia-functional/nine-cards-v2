package cards.nine.app.services.sync

import android.content.Context
import cards.nine.app.observers.NineCardsObserver._
import cards.nine.app.ui.commons.SyncDeviceState._
import cards.nine.app.ui.commons.action_filters.{SyncAnswerActionFilter, SyncStateActionFilter}
import cards.nine.app.ui.commons.{BroadAction, ImplicitsJobExceptions, JobException, Jobs}
import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{AppCardType, PublishedByMe}
import cards.nine.process.cloud.Conversions._
import cards.nine.process.commons.models.Collection
import cards.nine.process.sharedcollections.models.UpdateSharedCollection
import cards.nine.process.user.models.User
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ContextWrapper
import monix.eval.Task

class SynchronizeDeviceJobs(actions: SynchronizeDeviceUiActions)(implicit contextWrapper: ContextWrapper)
  extends Jobs
  with ImplicitsJobExceptions {

  lazy val preferences = contextSupport.context.getSharedPreferences(notificationPreferences, Context.MODE_PRIVATE)

  var statuses = SynchronizeDeviceJobsStatuses()

  def startSync(): TaskService[Unit] = {

    def updateCollections() = {

      def updateCollection(collectionId: Int) = {

        def updateSharedCollection(collection: Collection): TaskService[Option[String]] =
          (collection.publicCollectionStatus, collection.sharedCollectionId) match {
            case (PublishedByMe, Some(sharedCollectionId)) =>
              di.sharedCollectionsProcess.updateSharedCollection(
                UpdateSharedCollection(
                  sharedCollectionId = sharedCollectionId,
                  name = collection.name,
                  packages = collection.cards.filter(_.cardType == AppCardType).flatMap(_.packageName))).map(Option(_))
            case _ => TaskService.right(None)
          }

        for {
          collection <- di.collectionProcess.getCollectionById(collectionId).resolveOption()
          _ <- updateSharedCollection(collection)
        } yield ()
      }

      val ids = preferences.getString(collectionIdsKey, "").split(",").toSeq
      val updateServices = ids filterNot (_.isEmpty) map (id => updateCollection(id.toInt).value)
      preferences.edit().remove(collectionIdsKey).apply()

      TaskService{
        Task.gatherUnordered(updateServices) map (_ => Right((): Unit))
      }

    }

    for {
      _ <- setState(stateSyncing, close = false)
      _ <- di.deviceProcess.saveInstalledApps
      _ <- updateCollections()
      _ <- tryToConnect()
    } yield ()
  }

  def syncDevice(): TaskService[Unit] = {

    def sync(client: GoogleApiClient): TaskService[Unit] = {
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
        savedDevice <- di.cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          client = client,
          collections = collections map (collection => toCloudStorageCollection(collection, collection.moment map (moment => widgets.filter(_.momentId == moment.id)))),
          moments = cloudStorageMoments,
          dockApps = dockApps map toCloudStorageDockApp)
        _ <- di.userProcess.updateUserDevice(savedDevice.data.deviceName, savedDevice.cloudId)
      } yield ()
    }

    statuses.apiClient match {
      case Some(apiClient) =>
        for {
          _ <- sync(apiClient)
          _ <- setState(stateSuccess, close = true)
        } yield ()
      case None => tryToConnect()
    }

  }

  def sendActualState: TaskService[Unit] =
    sendBroadCastTask(BroadAction(SyncAnswerActionFilter.action, statuses.currentState))

  def closeServiceWithError(): TaskService[Unit] = setState(stateFailure, close = true)

  private[this] def setState(state: String, close: Boolean = false): TaskService[Unit] = {
    statuses = statuses.copy(currentState = Option(state))
    for {
      _ <- sendBroadCastTask(BroadAction(SyncStateActionFilter.action, statuses.currentState))
      _ <- if (close) actions.endProcess else TaskService.empty
    } yield ()
  }

  private[this] def tryToConnect(): TaskService[Unit] = {

    def createAndConnectClient(email: String): TaskService[Unit] =
      for {
        apiClient <- di.cloudStorageProcess.createCloudStorageClient(email)
        _ <- TaskService.right(statuses = statuses.copy(apiClient = Some(apiClient)))
        _ <- TaskService(CatchAll[JobException](apiClient.connect()))
      } yield ()

    def connectUser(user: User): TaskService[Unit] =
      user.email match {
        case Some(email) => createAndConnectClient(email)
        case None => setState(stateFailure, close = true)
      }

    for {
      user <- di.userProcess.getUser
      _ <- connectUser(user)
    } yield ()
  }

}

case class SynchronizeDeviceJobsStatuses(
  currentState: Option[String] = None,
  apiClient: Option[GoogleApiClient] = None)
