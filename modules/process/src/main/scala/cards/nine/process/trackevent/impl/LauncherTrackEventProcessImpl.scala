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

package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{
  ImplicitsTrackEventException,
  TrackEventException,
  TrackEventProcess
}
import cats.implicits._
import monix.eval.Task

trait LauncherTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  override def openAppFromAppDrawer(packageName: String, category: Category) = {
    val event = TrackEvent(
      screen = LauncherScreen,
      category = category,
      action = OpenAction,
      label = Option(packageName),
      value = Option(OpenAppFromAppDrawerValue))

    def eventForGames(category: Category): TaskService[Unit] =
      category match {
        case AppCategory(nineCardCategory) if nineCardCategory.isGameCategory =>
          trackServices
            .trackEvent(event.copy(category = AppCategory(Game)))
            .resolve[TrackEventException]
        case _ => TaskService(Task(Right(())))
      }

    (trackServices.trackEvent(event) *> eventForGames(category)).resolve[TrackEventException]
  }

}
