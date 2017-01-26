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

package cards.nine.app.ui.commons.dialogs.addMoment

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.BroadAction
import cards.nine.app.ui.commons.dialogs.addmoment.{AddMomentJobs, AddMomentUiActions}
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.MomentTestData
import cards.nine.models.MomentData
import cards.nine.models.types._
import cards.nine.process.moment.{MomentException, MomentProcess}
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait AddMomentJobsSpecification extends TaskServiceSpecification with Mockito {

  trait AddMomentJobsScope extends Scope with MomentTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector: Injector = mock[Injector]

    val mockAddMomentUiActions = mock[AddMomentUiActions]

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val addMomentJobs = new AddMomentJobs(mockAddMomentUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def sendBroadCastTask(broadAction: BroadAction) = TaskService.empty

    }
  }

}

class AddMomentJobsSpec extends AddMomentJobsSpecification {

  "initialize" should {
    "return a valid response when the service returns a right response" in new AddMomentJobsScope {

      mockAddMomentUiActions.initialize() returns serviceRight(Unit)
      mockAddMomentUiActions.showLoading() returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)
      mockAddMomentUiActions.addMoments(any) returns serviceRight(Unit)

      addMomentJobs.initialize().mustRightUnit

      there was one(mockAddMomentUiActions).initialize()
      there was one(mockAddMomentUiActions).showLoading()
      there was one(mockMomentProcess).getMoments
      there was one(mockAddMomentUiActions).addMoments(
        Seq(StudyMoment, SportMoment, MusicMoment, OutAndAboutMoment, CarMoment))
    }

    "returns a MomentException when the service returns an exception" in new AddMomentJobsScope {
      mockAddMomentUiActions.initialize() returns serviceRight(Unit)

      mockAddMomentUiActions.showLoading() returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceLeft(MomentException(""))

      addMomentJobs.initialize().mustLeft[MomentException]

      there was one(mockAddMomentUiActions).initialize()
      there was one(mockAddMomentUiActions).showLoading()
      there was one(mockMomentProcess).getMoments
    }
  }

  "loadMoments" should {
    "return a valid response when the service returns a right response" in new AddMomentJobsScope {

      mockAddMomentUiActions.showLoading() returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)
      mockAddMomentUiActions.addMoments(any) returns serviceRight(Unit)

      addMomentJobs.loadMoments().mustRightUnit

      there was one(mockAddMomentUiActions).showLoading()
      there was one(mockMomentProcess).getMoments
      there was one(mockAddMomentUiActions).addMoments(
        Seq(StudyMoment, SportMoment, MusicMoment, OutAndAboutMoment, CarMoment))
    }

    "shows a empty message in screen when the service returns a right response with all moment possible" in new AddMomentJobsScope {

      mockAddMomentUiActions.showLoading() returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(
        Seq(
          moment(0),
          moment(1),
          moment(2),
          moment(3),
          moment(4),
          moment(5),
          moment(6),
          moment(7)))
      mockAddMomentUiActions.showEmptyMessageInScreen() returns serviceRight(Unit)

      addMomentJobs.loadMoments().mustRightUnit

      there was one(mockAddMomentUiActions).showLoading()
      there was one(mockMomentProcess).getMoments
      there was one(mockAddMomentUiActions).showEmptyMessageInScreen()
    }

    "returns a MomentException when the service returns an exception" in new AddMomentJobsScope {

      mockAddMomentUiActions.showLoading() returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceLeft(MomentException(""))

      addMomentJobs.loadMoments().mustLeft[MomentException]

      there was one(mockAddMomentUiActions).showLoading()
      there was one(mockMomentProcess).getMoments
    }
  }

  "addMoment" should {
    "return a valid response when the service returns a right response" in new AddMomentJobsScope {

      mockTrackEventProcess.addMoment(any) returns serviceRight(Unit)
      mockMomentProcess.saveMoments(any)(any) returns serviceRight(seqMoment)
      mockAddMomentUiActions.close() returns serviceRight(Unit)

      addMomentJobs.addMoment(NineCardsMoment.defaultMoment).mustRightUnit

      there was one(mockTrackEventProcess).addMoment(NineCardsMoment.defaultMoment.name)
      there was one(mockMomentProcess).saveMoments(
        ===(
          Seq(
            MomentData(
              collectionId = None,
              timeslot = Seq.empty,
              wifi = Seq.empty,
              bluetooth = Seq.empty,
              headphone = false,
              momentType = NineCardsMoment.defaultMoment))))(any)
      there was one(mockAddMomentUiActions).close()

    }

    "returns a MomentException when the service returns an exception" in new AddMomentJobsScope {

      mockTrackEventProcess.addMoment(any) returns serviceRight(Unit)
      mockMomentProcess.saveMoments(any)(any) returns serviceLeft(MomentException(""))

      addMomentJobs.addMoment(NineCardsMoment.defaultMoment).mustLeft[MomentException]

      there was one(mockTrackEventProcess).addMoment(NineCardsMoment.defaultMoment.name)
      there was one(mockMomentProcess).saveMoments(
        ===(
          Seq(
            MomentData(
              collectionId = None,
              timeslot = Seq.empty,
              wifi = Seq.empty,
              bluetooth = Seq.empty,
              headphone = false,
              momentType = NineCardsMoment.defaultMoment))))(any)
    }
  }
}
