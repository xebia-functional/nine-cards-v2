package cards.nine.app.services.collections

import android.content.Intent
import cards.nine.app.commons.{BroadAction, Conversions, NineCardIntentConversions}
import cards.nine.app.services.commons.FirebaseExtensions._
import cards.nine.app.ui.commons.WizardState._
import cards.nine.app.ui.commons.action_filters.WizardStateActionFilter
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, Jobs, UiException}
import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.process.cloud.Conversions._
import cards.nine.process.device.GetByName
import cards.nine.process.user.models.User
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ContextWrapper

class CreateCollectionsJobs(actions: CreateCollectionsUiActions)(implicit contextWrapper: ContextWrapper)
  extends Jobs
  with Conversions
  with NineCardIntentConversions
  with ImplicitsUiExceptions {

  import CreateCollectionsService._

  var statuses = CreateCollectionsJobsStatuses()

  def sendActualState: TaskService[Unit] = {
    sendBroadCastTask(BroadAction(WizardStateActionFilter.action, statuses.currentState))
  }

  def startCommand(intent: Intent): TaskService[Unit] = {

    def readCloudId: TaskService[Option[String]] = TaskService {
      CatchAll[UiException] {
        Option(intent) flatMap { i =>
          if (i.hasExtra(cloudIdKey)) {
            val key = i.getStringExtra(cloudIdKey)
            if (key == newConfiguration) None else Some(key)
          } else None
        }
      }
    }

    def storeCloudId(cloudId: Option[String]): TaskService[Unit] =
      TaskService {
        CatchAll[UiException] {
          statuses = statuses.copy(selectedCloudId = cloudId)
        }
      }

    def storeApiClient(apiClient: Option[GoogleApiClient]): TaskService[Unit] =
      TaskService {
        CatchAll[UiException] {
          statuses = statuses.copy(apiClient = apiClient)
        }
      }

    def tryToStartService(user: User): TaskService[Unit] = {
      val hasKey = Option(intent) exists (_.hasExtra(cloudIdKey))
      (hasKey, user.deviceCloudId.isEmpty, user.email) match {
        case (true, true, Some(email)) =>
          for {
            cloudId <- readCloudId
            _ <- storeCloudId(cloudId)
            _ <- setState(stateCreatingCollections)
            _ <- actions.initialize()
            apiClient <- di.cloudStorageProcess.createCloudStorageClient(email)
            _ <- storeApiClient(Option(apiClient))
            _ <- TaskService(CatchAll[UiException](apiClient.connect()))
          } yield ()
        case (false, _, _) => setState(stateCloudIdNotSend, close = true)
        case (_, false, _) => setState(stateUserCloudIdPresent, close = true)
        case (_, _, None) => setState(stateUserEmailNotPresent, close = true)
      }
    }

    for {
      user <- di.userProcess.getUser
      _ <- tryToStartService(user)
    } yield ()

  }

  def createConfiguration(): TaskService[Unit] = {
    (statuses.apiClient, statuses.selectedCloudId)  match {
      case (Some(client), Some(cloudId)) =>
        for {
          _ <- loadConfiguration(client, readToken, cloudId)
          _ <- setState(stateSuccess, close = true)
        } yield ()
      case (Some(client), None) =>
        for {
          _ <- createNewConfiguration(client, readToken)
          _ <- setState(stateSuccess, close = true)
        } yield()
      case _ => TaskService.left(UiException("GoogleAPIClient not initialized"))
    }
  }

  def closeServiceWithError(): TaskService[Unit] = setState(stateFailure, close = true)

  private[this] def setState(state: String, close: Boolean = false): TaskService[Unit] = {
    statuses = statuses.copy(currentState = Option(state))
    for {
      _ <- sendActualState
      _ <- if (close) actions.endProcess else TaskService.right((): Unit)
    } yield ()
  }

  private[this] def createNewConfiguration(
    client: GoogleApiClient,
    deviceToken: Option[String]): TaskService[Unit] = {
    val dockAppsSize = 4
    for {
      _ <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.saveInstalledApps
      _ <- actions.setProcess(statuses.selectedCloudId, GettingAppsProcess)
      dockApps <- di.deviceProcess.generateDockApps(dockAppsSize)
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ <- actions.setProcess(statuses.selectedCloudId, LoadingConfigProcess)
      contacts <- di.deviceProcess.getFavoriteContacts.resolveLeftTo(Seq.empty)
      _ <- actions.setProcess(statuses.selectedCloudId, CreatingCollectionsProcess)
      _ <- di.collectionProcess.createCollectionsFromUnformedItems(toSeqUnformedApp(apps), toSeqUnformedContact(contacts))
      _ <- di.momentProcess.createMoments
      storedCollections <- di.collectionProcess.getCollections
      savedDevice <- di.cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
        client = client,
        collections = storedCollections map (collection => toCloudStorageCollection(collection, None)),
        moments = Seq.empty,
        dockApps = dockApps map toCloudStorageDockApp)
      _ <- di.userProcess.updateUserDevice(savedDevice.data.deviceName, savedDevice.cloudId, deviceToken)
    } yield ()
  }

  private[this] def loadConfiguration(
    client: GoogleApiClient,
    deviceToken: Option[String],
    cloudId: String): TaskService[Unit] = {
    for {
      _ <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.saveInstalledApps
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ <- actions.setProcess(statuses.selectedCloudId, GettingAppsProcess)
      device <- di.cloudStorageProcess.getCloudStorageDevice(client, cloudId)
      _ <- actions.setProcess(statuses.selectedCloudId, LoadingConfigProcess)
      _ <- actions.setProcess(statuses.selectedCloudId, CreatingCollectionsProcess)
      _ <- di.collectionProcess.createCollectionsFromFormedCollections(toSeqFormedCollection(device.data.collections))
      momentSeq = device.data.moments map (_ map toSaveMomentRequest) getOrElse Seq.empty
      dockAppSeq = device.data.dockApps map (_ map toSaveDockAppRequest) getOrElse Seq.empty
      _ <- di.momentProcess.saveMoments(momentSeq)
      _ <- di.deviceProcess.saveDockApps(dockAppSeq)
      _ <- di.userProcess.updateUserDevice(device.data.deviceName, device.cloudId, deviceToken)
    } yield ()
  }

}

case class CreateCollectionsJobsStatuses(
  selectedCloudId: Option[String] = None,
  currentState: Option[String] = None,
  apiClient: Option[GoogleApiClient] = None)
