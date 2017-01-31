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
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.MomentTestData
import cards.nine.models.types._
import cards.nine.process.recognition.RecognitionProcessException
import cards.nine.services.awareness.{AwarenessException, AwarenessServices}
import cards.nine.services.persistence.PersistenceServices
import cats.syntax.either._
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait RecognitionProcessSpecification
    extends TaskServiceSpecification
    with Mockito
    with RecognitionProcessData
    with MomentTestData {

  val awarenessException = AwarenessException("Irrelevant message")

  trait RecognitionProcessScope extends Scope {

    val contextSupport = mock[ContextSupport]

    val mockPersistenceServices = mock[PersistenceServices]
    val mockServices            = mock[AwarenessServices]

    val receiver = mock[BroadcastReceiver]

    val process = new RecognitionProcessImpl(mockPersistenceServices, mockServices)

  }
}

class RecognitionProcessImplSpec extends RecognitionProcessSpecification {

  "getMostProbableActivity" should {

    "get the most probable activity" in new RecognitionProcessScope {

      mockServices.getTypeActivity returns TaskService(Task(Either.right(typeActivity)))

      val result = process.getMostProbableActivity.run
      result shouldEqual Either.right(probablyActivity)

      there was one(mockServices).getTypeActivity
    }

    "return a RecognitionProcessException when the service return an exception" in new RecognitionProcessScope {

      mockServices.getTypeActivity returns TaskService(Task(Either.left(awarenessException)))

      process.getMostProbableActivity.mustLeft[RecognitionProcessException]
    }

  }

  "registerFenceUpdates" should {

    "call to register updates with the right fences" in new RecognitionProcessScope {

      val moment1 = moment.copy(momentType = MusicMoment)
      val moment4 = moment.copy(momentType = CarMoment)

      mockPersistenceServices.fetchMoments returns TaskService.right(Seq(moment1, moment4))
      mockServices.registerFenceUpdates(any, any, any)(any) returns TaskService.empty

      val result = process.registerFenceUpdates("", receiver)(contextSupport).run
      result shouldEqual Either.right((): Unit)

      there was one(mockServices)
        .registerFenceUpdates("", Seq(HeadphonesFence, InVehicleFence), receiver)(contextSupport)
    }

    "return unit when the're no moments in the database" in new RecognitionProcessScope {

      mockPersistenceServices.fetchMoments returns TaskService.right(Seq.empty)
      mockServices.registerFenceUpdates(any, any, any)(any) returns TaskService(
        Task(Either.left(awarenessException)))

      process.registerFenceUpdates("", receiver)(contextSupport).run shouldEqual Either.right(
        (): Unit)
    }

    "return a RecognitionProcessException when the service return an exception" in new RecognitionProcessScope {

      val moment1 = moment.copy(momentType = MusicMoment)
      mockPersistenceServices.fetchMoments returns TaskService.right(Seq(moment1))
      mockServices.registerFenceUpdates(any, any, any)(any) returns TaskService(
        Task(Either.left(awarenessException)))

      process
        .registerFenceUpdates("", receiver)(contextSupport)
        .mustLeft[RecognitionProcessException]
    }

  }

  "getHeadphone" should {

    "get headphone status" in new RecognitionProcessScope {

      mockServices.getHeadphonesState returns TaskService(Task(Either.right(headphonesState)))

      val result = process.getHeadphone.run
      result shouldEqual Either.right(headphones)

      there was one(mockServices).getHeadphonesState
    }

    "return a RecognitionProcessException when the service return an exception" in new RecognitionProcessScope {

      mockServices.getHeadphonesState returns TaskService(Task(Either.left(awarenessException)))

      process.getHeadphone.mustLeft[RecognitionProcessException]
    }

  }

  "getLocation" should {

    "get location" in new RecognitionProcessScope {

      mockServices.getLocation(any) returns TaskService(Task(Either.right(awarenessLocation)))

      val result = process.getLocation(contextSupport).run
      result shouldEqual Either.right(location)

      there was one(mockServices).getLocation(any)
    }

    "return a RecognitionProcessException when the service return an exception" in new RecognitionProcessScope {

      mockServices.getLocation(any) returns TaskService(Task(Either.left(awarenessException)))

      process.getLocation(contextSupport).mustLeft[RecognitionProcessException]
    }

  }

  "getWeather" should {

    "get weather" in new RecognitionProcessScope {

      mockServices.getWeather returns TaskService(Task(Either.right(weatherState)))

      val result = process.getWeather.run
      result shouldEqual Either.right(weather)

      there was one(mockServices).getWeather
    }

    "return a RecognitionProcessException when the service return an exception" in new RecognitionProcessScope {

      mockServices.getWeather returns TaskService(Task(Either.left(awarenessException)))

      process.getWeather.mustLeft[RecognitionProcessException]
    }

  }

}
