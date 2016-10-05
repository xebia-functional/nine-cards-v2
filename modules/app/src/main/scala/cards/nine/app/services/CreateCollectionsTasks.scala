package cards.nine.app.services

import cards.nine.app.commons.{Conversions, NineCardIntentConversions}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.process.cloud.Conversions._
import cards.nine.process.commons.models.Collection
import cards.nine.process.device.{GetByName, ImplicitsDeviceException}
import com.google.android.gms.common.api.GoogleApiClient

trait CreateCollectionsTasks
  extends Conversions
  with NineCardIntentConversions {

  self: CreateCollectionService
    with ImplicitsDeviceException =>

  val dockAppsSize = 4

  def createNewConfiguration(
    client: GoogleApiClient,
    deviceToken: Option[String]): TaskService[Seq[Collection]] = {
    for {
      _ <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.saveInstalledApps
      _ = setProcess(GettingAppsProcess)
      dockApps <- di.deviceProcess.generateDockApps(dockAppsSize)
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ = setProcess(LoadingConfigProcess)
      contacts <- di.deviceProcess.getFavoriteContacts.resolveLeftTo(Seq.empty)
      _ = setProcess(CreatingCollectionsProcess)
      collections <- di.collectionProcess.createCollectionsFromUnformedItems(toSeqUnformedApp(apps), toSeqUnformedContact(contacts))
      momentCollections <- di.momentProcess.createMoments
      storedCollections <- di.collectionProcess.getCollections
      savedDevice <- di.cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
        client = client,
        collections = storedCollections map (collection => toCloudStorageCollection(collection, None)),
        moments = Seq.empty,
        dockApps = dockApps map toCloudStorageDockApp)
      _ <- di.userProcess.updateUserDevice(savedDevice.data.deviceName, savedDevice.cloudId, deviceToken)
    } yield collections ++ momentCollections
  }

  def loadConfiguration(
    client: GoogleApiClient,
    deviceToken: Option[String],
    cloudId: String): TaskService[Seq[Collection]] = {
    for {
      _ <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.saveInstalledApps
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ = setProcess(GettingAppsProcess)
      device <- di.cloudStorageProcess.getCloudStorageDevice(client, cloudId)
      _ = setProcess(LoadingConfigProcess)
      _ = setProcess(CreatingCollectionsProcess)
      collections <- di.collectionProcess.createCollectionsFromFormedCollections(toSeqFormedCollection(device.data.collections))
      momentSeq = device.data.moments map (_ map toSaveMomentRequest) getOrElse Seq.empty
      dockAppSeq = device.data.dockApps map (_ map toSaveDockAppRequest) getOrElse Seq.empty
      _ <- di.momentProcess.saveMoments(momentSeq)
      _ <- di.deviceProcess.saveDockApps(dockAppSeq)
      _ <- di.userProcess.updateUserDevice(device.data.deviceName, device.cloudId, deviceToken)
    } yield collections
  }

}
