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

package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.MomentValues._
import cards.nine.repository.model.{Moment, MomentData}

trait MomentPersistenceServicesData {

  def repoMomentData(num: Int = 0) =
    MomentData(
      collectionId = Option(momentCollectionId + num),
      timeslot = timeslotJson,
      wifi = Seq(wifiSeq(num)).mkString(","),
      bluetooth = Seq(bluetoothSeq(num)).mkString(","),
      headphone = headphone,
      momentType = Option(momentTypeSeq(num)))

  val repoMomentData: MomentData = repoMomentData(0)
  val seqRepoMomentData          = Seq(repoMomentData(0), repoMomentData(1), repoMomentData(2))

  def repoMoment(num: Int = 0) = Moment(id = momentId + num, data = repoMomentData(num))

  val repoMoment: Moment = repoMoment(0)
  val seqRepoMoment      = Seq(repoMoment(0), repoMoment(1), repoMoment(2))

}
