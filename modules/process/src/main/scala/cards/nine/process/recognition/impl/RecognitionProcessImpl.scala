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

package cards.nine.process.recognition.impl

import android.content.BroadcastReceiver
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models._
import cards.nine.models.types._
import cards.nine.process.recognition._
import cards.nine.services.awareness.AwarenessServices
import cards.nine.services.persistence.PersistenceServices

class RecognitionProcessImpl(
    persistenceServices: PersistenceServices,
    awarenessServices: AwarenessServices)
    extends RecognitionProcess
    with ImplicitsRecognitionProcessExceptions {

  override def getMostProbableActivity: TaskService[ProbablyActivity] =
    awarenessServices.getTypeActivity.resolve[RecognitionProcessException]

  override def registerFenceUpdates(action: String, receiver: BroadcastReceiver)(
      implicit contextSupport: ContextSupport) = {

    def getFencesFromMoments(moments: Seq[Moment]): Seq[AwarenessFenceUpdate] =
      moments.map(_.momentType).flatMap {
        case MusicMoment => Some(HeadphonesFence)
        case CarMoment   => Some(InVehicleFence)
        case _           => None
      }

    (for {
      moments <- persistenceServices.fetchMoments
      fences = getFencesFromMoments(moments)
      _ <- if (fences.nonEmpty) awarenessServices.registerFenceUpdates(action, fences, receiver)
      else TaskService.empty
    } yield ()).resolve[RecognitionProcessException]
  }

  override def unregisterFenceUpdates(action: String)(
      implicit contextSupport: ContextSupport): TaskService[Unit] =
    awarenessServices.unregisterFenceUpdates(action).resolve[RecognitionProcessException]

  override def getHeadphone: TaskService[Headphones] =
    awarenessServices.getHeadphonesState.resolve[RecognitionProcessException]

  override def getLocation(implicit contextSupport: ContextSupport): TaskService[Location] =
    awarenessServices.getLocation.resolve[RecognitionProcessException]

  override def getWeather: TaskService[WeatherState] =
    awarenessServices.getWeather.resolve[RecognitionProcessException]

}
