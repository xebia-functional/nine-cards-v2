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

package cards.nine.services.persistence.conversions

import cards.nine.models.reads.MomentImplicits
import cards.nine.models.types.NineCardsMoment
import cards.nine.models.{Moment, MomentData, MomentTimeSlot}
import cards.nine.repository.model.{Moment => RepositoryMoment, MomentData => RepositoryMomentData}
import play.api.libs.json.Json

trait MomentConversions {

  import MomentImplicits._

  def toMoment(moment: RepositoryMoment): Moment =
    Moment(
      id = moment.id,
      collectionId = moment.data.collectionId,
      timeslot = Json.parse(moment.data.timeslot).as[Seq[MomentTimeSlot]],
      wifi = if (moment.data.wifi.isEmpty) List.empty else moment.data.wifi.split(",").toList,
      bluetooth =
        if (moment.data.bluetooth.isEmpty) List.empty else moment.data.bluetooth.split(",").toList,
      headphone = moment.data.headphone,
      momentType = NineCardsMoment(moment.data.momentType),
      widgets = None)

  def toRepositoryMoment(moment: Moment): RepositoryMoment =
    RepositoryMoment(id = moment.id, data = toRepositoryMomentData(moment.toData))

  def toRepositoryMomentWithoutCollection(moment: Moment): RepositoryMoment =
    RepositoryMoment(
      id = moment.id,
      data = toRepositoryMomentData(moment.toData).copy(collectionId = None))

  def toRepositoryMomentData(moment: MomentData): RepositoryMomentData =
    RepositoryMomentData(
      collectionId = moment.collectionId,
      timeslot = Json.toJson(moment.timeslot).toString,
      wifi = moment.wifi.mkString(","),
      bluetooth = moment.bluetooth.mkString(","),
      headphone = moment.headphone,
      momentType = Option(moment.momentType.name))
}
