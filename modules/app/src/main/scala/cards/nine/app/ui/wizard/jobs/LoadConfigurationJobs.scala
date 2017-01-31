/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.wizard.jobs

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.{JobException, Jobs}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.CloudStorageDevice
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ActivityContextWrapper

class LoadConfigurationJobs(implicit contextWrapper: ActivityContextWrapper)
    extends Jobs
    with Conversions {

  def loadConfiguration(client: GoogleApiClient, cloudId: String): TaskService[Unit] = {

    def loadConfiguration(device: CloudStorageDevice): TaskService[Unit] = {
      for {
        firebaseToken <- di.externalServicesProcess.readFirebaseToken
          .map(token => Option(token))
          .resolveLeftTo(None)
        _ <- di.collectionProcess.createCollectionsFromCollectionData(
          toSeqCollectionData(device.data.collections))
        momentSeq  = device.data.moments map (_ map toMomentData) getOrElse Seq.empty
        dockAppSeq = device.data.dockApps map (_ map toDockAppData) getOrElse Seq.empty
        _ <- di.momentProcess.saveMoments(momentSeq)
        _ <- di.deviceProcess.saveDockApps(dockAppSeq)
        _ <- di.userProcess.updateUserDevice(device.data.deviceName, device.cloudId, firebaseToken)
      } yield ()
    }

    for {
      _      <- di.deviceProcess.resetSavedItems()
      _      <- di.deviceProcess.synchronizeInstalledApps
      device <- di.cloudStorageProcess.getCloudStorageDevice(client, cloudId)
      _ <- if (device.data.collections.nonEmpty) {
        loadConfiguration(device)
      } else
        TaskService.left(JobException("The device doesn't have collections"))
    } yield ()

  }

}
