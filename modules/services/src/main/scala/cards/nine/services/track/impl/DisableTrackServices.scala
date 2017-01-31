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

package cards.nine.services.track.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.TrackEvent
import cards.nine.models.types.MomentCategory
import cards.nine.services.track.TrackServices
import monix.eval.Task

class DisableTrackServices extends TrackServices {

  override def trackEvent(event: TrackEvent): TaskService[Unit] = TaskService {
    val categoryName = event.category match {
      case MomentCategory(moment) => s"WIDGET_${moment.name}"
      case _                      => event.category.name
    }

    Task(Right(println(s"""Event no tracked
                          | Action ${event.action.name}
                          | Category $categoryName
                          | Label ${event.label.getOrElse("")}
                          | Screen ${event.screen.name}
                          | Value ${event.value.map(_.value).getOrElse(0)}""".stripMargin)))
  }
}
