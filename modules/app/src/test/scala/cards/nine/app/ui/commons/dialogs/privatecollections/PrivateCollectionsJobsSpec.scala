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

package cards.nine.app.ui.commons.dialogs.privatecollections

import cards.nine.app.di.Injector
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.{ApplicationTestData, CollectionTestData}
import cards.nine.models.types.GetByName
import cards.nine.process.collection.{CollectionException, CollectionProcess}
import cards.nine.process.device.DeviceProcess
import cards.nine.process.moment.MomentProcess
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait PrivateCollectionsJobsSpecification extends TaskServiceSpecification with Mockito {

  trait PrivateCollectionsScope extends Scope with CollectionTestData with ApplicationTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector: Injector = mock[Injector]

    val mockPrivateCollectionsUiActions = mock[PrivateCollectionsUiActions]

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val privateCollectionsJobs =
      new PrivateCollectionsJobs(mockPrivateCollectionsUiActions)(contextWrapper) {

        override lazy val di: Injector = mockInjector

      }
  }

}

class PrivateCollectionsJobsSpec extends PrivateCollectionsJobsSpecification {

  "initialize" should {
    "returns a valid response when the service returns a right response" in new PrivateCollectionsScope {

      mockTrackEventProcess.openMyCollections() returns serviceRight(Unit)
      mockPrivateCollectionsUiActions.initialize() returns serviceRight(Unit)
      mockPrivateCollectionsUiActions.showLoading() returns serviceRight(Unit)
      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)
      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)
      mockCollectionProcess.generatePrivateCollections(any)(any) returns serviceRight(
        seqCollectionData)
      mockPrivateCollectionsUiActions.showEmptyMessageInScreen() returns serviceRight(Unit)

      privateCollectionsJobs.initialize().mustRightUnit

      there was one(mockTrackEventProcess).openMyCollections()
      there was one(mockPrivateCollectionsUiActions).initialize()
      there was one(mockPrivateCollectionsUiActions).showLoading()
      there was one(mockCollectionProcess).getCollections
      there was one(mockMomentProcess).getMoments
      there was one(mockDeviceProcess).getSavedApps(===(GetByName))(any)
      there was one(mockCollectionProcess).generatePrivateCollections(any)(any)
    }
  }

  "loadPrivateCollections" should {
    "returns a valid response when the service returns a right response" in new PrivateCollectionsScope {

      mockPrivateCollectionsUiActions.showLoading() returns serviceRight(Unit)
      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)
      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)
      mockCollectionProcess.generatePrivateCollections(any)(any) returns serviceRight(
        seqCollectionData)
      mockPrivateCollectionsUiActions.showEmptyMessageInScreen() returns serviceRight(Unit)

      privateCollectionsJobs.loadPrivateCollections().mustRightUnit

      there was one(mockPrivateCollectionsUiActions).showLoading()
      there was one(mockCollectionProcess).getCollections
      there was one(mockMomentProcess).getMoments
      there was one(mockDeviceProcess).getSavedApps(===(GetByName))(any)
      there was one(mockCollectionProcess).generatePrivateCollections(any)(any)
    }

    "returns a valid response when the service returns a right response when the newCollections hasn't appsCategory" in new PrivateCollectionsScope {

      mockPrivateCollectionsUiActions.showLoading() returns serviceRight(Unit)
      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)
      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)
      mockCollectionProcess.generatePrivateCollections(any)(any) returns serviceRight(
        seqCollectionData.map(_.copy(appsCategory = None)))
      mockPrivateCollectionsUiActions.addPrivateCollections(any) returns serviceRight(Unit)

      privateCollectionsJobs.loadPrivateCollections().mustRightUnit

      there was one(mockPrivateCollectionsUiActions).showLoading()
      there was one(mockCollectionProcess).getCollections
      there was one(mockMomentProcess).getMoments
      there was one(mockDeviceProcess).getSavedApps(===(GetByName))(any)
      there was one(mockCollectionProcess).generatePrivateCollections(any)(any)
    }
  }

  "saveCollection" should {
    "returns a valid response when the service returns a right response" in new PrivateCollectionsScope {

      mockTrackEventProcess.createNewCollectionFromMyCollection(any) returns serviceRight(Unit)
      mockCollectionProcess.addCollection(any) returns serviceRight(collection)
      mockPrivateCollectionsUiActions.close() returns serviceRight(Unit)

      privateCollectionsJobs.saveCollection(collectionData).mustRight(_ shouldEqual collection)

      there was one(mockTrackEventProcess).createNewCollectionFromMyCollection(collectionData.name)
      there was one(mockCollectionProcess).addCollection(collectionData)
      there was one(mockPrivateCollectionsUiActions).close()
    }

    "returns a CollectionException when the service returns an exception" in new PrivateCollectionsScope {

      mockTrackEventProcess.createNewCollectionFromMyCollection(any) returns serviceRight(Unit)
      mockCollectionProcess.addCollection(any) returns serviceLeft(CollectionException(""))

      privateCollectionsJobs.saveCollection(collectionData).mustLeft[CollectionException]

      there was one(mockTrackEventProcess).createNewCollectionFromMyCollection(collectionData.name)
      there was one(mockCollectionProcess).addCollection(collectionData)
      there was no(mockPrivateCollectionsUiActions).close()
    }
  }
}
