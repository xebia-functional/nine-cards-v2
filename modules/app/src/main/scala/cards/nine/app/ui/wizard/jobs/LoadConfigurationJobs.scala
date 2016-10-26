package cards.nine.app.ui.wizard.jobs

import cards.nine.app.commons.Conversions
import cards.nine.app.services.commons.FirebaseExtensions._
import cards.nine.app.ui.commons.{JobException, Jobs}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.CloudStorageDevice
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ActivityContextWrapper

class LoadConfigurationJobs(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions {

  def loadConfiguration(client: GoogleApiClient, cloudId: String): TaskService[Unit] = {

    def loadConfiguration(
      device: CloudStorageDevice,
      deviceToken: Option[String]): TaskService[Unit] = {
      for {
        _ <- di.collectionProcess.createCollectionsFromCollectionData(toSeqCollectionData(device.data.collections))
        momentSeq = device.data.moments map (_ map toMomentData) getOrElse Seq.empty
        dockAppSeq = device.data.dockApps map (_ map toDockAppData) getOrElse Seq.empty
        _ <- di.momentProcess.saveMoments(momentSeq)
        _ <- di.deviceProcess.saveDockApps(dockAppSeq)
        _ <- di.userProcess.updateUserDevice(device.data.deviceName, device.cloudId, deviceToken)
      } yield ()
    }

    for {
      _ <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.synchronizeInstalledApps
      device <- di.cloudStorageProcess.getCloudStorageDevice(client, cloudId)
      _ <- if (device.data.collections.nonEmpty) {
        loadConfiguration(device, readToken)
      } else TaskService.left(JobException("The device doesn't have collections"))
    } yield ()

  }

}


