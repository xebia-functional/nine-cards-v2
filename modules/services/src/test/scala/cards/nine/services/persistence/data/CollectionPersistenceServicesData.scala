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

package cards.nine.services.persistence.data

import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.repository.model.{Collection, CollectionData}

trait CollectionPersistenceServicesData {

  def repoCollectionData(num: Int = 0) =
    CollectionData(
      position = collectionPosition + num,
      name = collectionName + num,
      collectionType = collectionType.name,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = Option(categoryStr),
      originalSharedCollectionId = Option(originalSharedCollectionId),
      sharedCollectionId = Option(sharedCollectionId),
      sharedCollectionSubscribed = Option(sharedCollectionSubscribed))

  val repoCollectionData: CollectionData = repoCollectionData(0)
  val seqRepoCollectionData =
    Seq(repoCollectionData(0), repoCollectionData(1), repoCollectionData(2))

  def repoCollection(num: Int = 0) = Collection(id = collectionId + num, data = repoCollectionData)

  val repoCollection: Collection = repoCollection(0)
  val seqRepoCollection          = Seq(repoCollection(0), repoCollection(1), repoCollection(2))

}
