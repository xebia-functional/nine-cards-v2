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

import cards.nine.commons.test.data.CardValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.repository.model.{Card, CardData}

trait CardPersistenceServicesData {

  def repoCardData(num: Int = 0) =
    CardData(
      position = cardPosition + num,
      term = term,
      packageName = Option(cardPackageName + num),
      cardType = cardType,
      intent = intent,
      imagePath = Option(cardImagePath),
      notification = Option(notification))

  val repoCardData: CardData         = repoCardData(0)
  val seqRepoCardData: Seq[CardData] = Seq(repoCardData(0), repoCardData(1), repoCardData(2))

  def repoCard(num: Int = 0) = Card(id = cardId + num, data = repoCardData(num))

  val repoCard: Card         = repoCard(0)
  val seqRepoCard: Seq[Card] = Seq(repoCard(0), repoCard(1), repoCard(2))

}
