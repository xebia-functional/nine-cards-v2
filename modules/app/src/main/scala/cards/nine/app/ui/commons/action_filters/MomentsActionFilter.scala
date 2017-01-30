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

package cards.nine.app.ui.commons.action_filters

sealed trait MomentsActionFilter {
  val action: String
}

case object MomentReloadedActionFilter extends MomentsActionFilter {
  override val action: String = "moments-reloaded-action-filter"
}

case object MomentConstrainsChangedActionFilter extends MomentsActionFilter {
  override val action: String = "moments-constrains-changed-action-filter"
}

case object MomentAddedOrRemovedActionFilter extends MomentsActionFilter {
  override val action: String = "moments-added-or-removed-action-filter"
}

case object MomentBestAvailableActionFilter extends MomentsActionFilter {
  override val action: String = "moments-best-available-action-filter"
}

case object MomentForceBestAvailableActionFilter extends MomentsActionFilter {
  override val action: String = "moments-force-best-available-action-filter"
}

object MomentsActionFilter {

  val cases = Seq(
    MomentReloadedActionFilter,
    MomentConstrainsChangedActionFilter,
    MomentAddedOrRemovedActionFilter,
    MomentBestAvailableActionFilter,
    MomentForceBestAvailableActionFilter)

  def apply(action: String): Option[MomentsActionFilter] =
    cases find (_.action == action)

}
