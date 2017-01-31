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

package cards.nine.models

import cards.nine.models.types._

case class Moment(
    id: Int,
    collectionId: Option[Int],
    timeslot: Seq[MomentTimeSlot],
    wifi: Seq[String],
    bluetooth: Seq[String],
    headphone: Boolean,
    momentType: NineCardsMoment,
    widgets: Option[Seq[WidgetData]] = None)

case class MomentData(
    collectionId: Option[Int],
    timeslot: Seq[MomentTimeSlot],
    wifi: Seq[String],
    bluetooth: Seq[String],
    headphone: Boolean,
    momentType: NineCardsMoment,
    widgets: Option[Seq[WidgetData]] = None)

case class MomentTimeSlot(from: String, to: String, days: Seq[Int])

object Moment {

  implicit class MomentOps(moment: Moment) {

    def toData =
      MomentData(
        collectionId = moment.collectionId,
        timeslot = moment.timeslot,
        wifi = moment.wifi,
        bluetooth = moment.bluetooth,
        headphone = moment.headphone,
        momentType = moment.momentType,
        widgets = moment.widgets)
  }

  implicit class MomentTimeSlotOps(moment: NineCardsMoment) {

    def toMomentTimeSlot: Seq[MomentTimeSlot] =
      moment match {
        case HomeMorningMoment =>
          Seq(MomentTimeSlot(from = "08:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
        case WorkMoment =>
          Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
        case HomeNightMoment =>
          Seq(
            MomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)),
            MomentTimeSlot(from = "00:00", to = "08:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
        case StudyMoment =>
          Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
        case MusicMoment       => Seq.empty
        case CarMoment         => Seq.empty
        case SportMoment       => Seq.empty
        case OutAndAboutMoment => Seq.empty
        case _                 => Seq.empty
      }
  }
}
