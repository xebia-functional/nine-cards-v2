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

package cards.nine.app.ui.commons.dialogs.addmoment

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.action_filters.MomentAddedOrRemovedActionFilter
import cards.nine.app.ui.commons.{BroadAction, Jobs}
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.Moment.MomentTimeSlotOps
import cards.nine.models.types._
import cards.nine.models.{Moment, MomentData}
import macroid.ActivityContextWrapper

class AddMomentJobs(actions: AddMomentUiActions)(implicit contextWrapper: ActivityContextWrapper)
    extends Jobs
    with Conversions {

  def initialize(): TaskService[Unit] =
    for {
      _ <- actions.initialize()
      _ <- loadMoments()
    } yield ()

  def loadMoments(): TaskService[Unit] = {

    def getMomentNotUsed(currentMoments: Seq[Moment]): Seq[NineCardsMoment] =
      NineCardsMoment.moments filterNot (moment =>
                                           currentMoments map (_.momentType) contains moment)

    for {
      _       <- actions.showLoading()
      moments <- di.momentProcess.getMoments
      momentNotUsed = getMomentNotUsed(moments)
      _ <- if (momentNotUsed.isEmpty) {
        actions.showEmptyMessageInScreen()
      } else {
        actions.addMoments(momentNotUsed)
      }
    } yield ()
  }

  def addMoment(nineCardsMoment: NineCardsMoment): TaskService[Unit] = {
    val moment = MomentData(
      collectionId = None,
      timeslot = nineCardsMoment.toMomentTimeSlot,
      wifi = Seq.empty,
      bluetooth = Seq.empty,
      headphone = false,
      momentType = nineCardsMoment)
    for {
      _ <- di.trackEventProcess.addMoment(nineCardsMoment.name)
      _ <- di.momentProcess.saveMoments(Seq(moment))
      _ <- sendBroadCastTask(BroadAction(MomentAddedOrRemovedActionFilter.action))
      _ <- actions.close()
    } yield ()
  }

}
