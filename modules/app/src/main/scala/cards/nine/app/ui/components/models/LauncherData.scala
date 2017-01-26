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

package cards.nine.app.ui.components.models

import cards.nine.models.Collection
import cards.nine.models.types.NineCardsMoment

case class LauncherData(
    workSpaceType: WorkSpaceType,
    moment: Option[LauncherMoment] = None,
    collections: Seq[Collection] = Seq.empty,
    positionByType: Int = 0)

case class LauncherMoment(momentType: Option[NineCardsMoment], collection: Option[Collection])

sealed trait WorkSpaceType {
  val value: Int

  def isMomentWorkSpace: Boolean = this == MomentWorkSpace
}

case object MomentWorkSpace extends WorkSpaceType {
  override val value: Int = 0
}

case object CollectionsWorkSpace extends WorkSpaceType {
  override val value: Int = 1
}

object WorkSpaceType {
  def apply(value: Int): WorkSpaceType = value match {
    case MomentWorkSpace.value => MomentWorkSpace
    case _                     => CollectionsWorkSpace
  }
}
