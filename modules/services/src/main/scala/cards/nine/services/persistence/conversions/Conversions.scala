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

import cards.nine.models.TermCounter
import cards.nine.repository.model.{DataCounter => RepoDataCounter}

trait Conversions
    extends AppConversions
    with CardConversions
    with CollectionConversions
    with UserConversions
    with MomentConversions
    with DockAppConversions
    with WidgetConversions {

  def toDataCounterSeq(items: Seq[RepoDataCounter]): Seq[TermCounter] = items map toDataCounter

  def toDataCounter(item: RepoDataCounter): TermCounter =
    TermCounter(term = item.term, count = item.count)

}
