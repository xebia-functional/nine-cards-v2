package com.fortysevendeg.ninecardslauncher.process.recognition.impl

import cats.syntax.either._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.commons.test.TaskServiceTestOps._
import com.fortysevendeg.ninecardslauncher.process.recognition.RecognitionProcessException
import com.fortysevendeg.ninecardslauncher.services.awareness.{AwarenessException, AwarenessServices}
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.reflect.ClassTag

trait RecognitionProcessSpecification
  extends Specification
  with Mockito
  with RecognitionProcessData {

  val awarenessException = AwarenessException("Irrelevant message")

  trait RecognitionProcessScope
    extends Scope {

    val contextSupport = mock[ContextSupport]

    val mockServices = mock[AwarenessServices]

    val process = new RecognitionProcessImpl(mockServices)

    def mustLeft[T <: NineCardException](service: TaskService[_])(implicit classTag: ClassTag[T]): Unit =
      service.value.run must beLike {
        case Left(e) => e must beAnInstanceOf[T]
      }

  }
}

class RecognitionProcessImplSpec
  extends RecognitionProcessSpecification {

  "getMostProbableActivity" should {

    "get the most probable activity" in new RecognitionProcessScope {

      mockServices.getTypeActivity returns TaskService(Task(Either.right(typeActivity)))

      val result = process.getMostProbableActivity.value.run
      result shouldEqual Either.right(probablyActivity)

      there was one(mockServices).getTypeActivity
    }

    "return a RecognitionProcessException when the service return an exception" in new RecognitionProcessScope {

      mockServices.getTypeActivity returns TaskService(Task(Either.left(awarenessException)))

      mustLeft[RecognitionProcessException](process.getMostProbableActivity)
    }

  }

  "getHeadphone" should {

    "get headphone status" in new RecognitionProcessScope {

      mockServices.getHeadphonesState returns TaskService(Task(Either.right(headphonesState)))

      val result = process.getHeadphone.value.run
      result shouldEqual Either.right(headphones)

      there was one(mockServices).getHeadphonesState
    }

    "return a RecognitionProcessException when the service return an exception" in new RecognitionProcessScope {

      mockServices.getHeadphonesState returns TaskService(Task(Either.left(awarenessException)))

      mustLeft[RecognitionProcessException](process.getHeadphone)
    }

  }

  "getLocation" should {

    "get location" in new RecognitionProcessScope {

      mockServices.getLocation(any) returns TaskService(Task(Either.right(awarenessLocation)))

      val result = process.getLocation(contextSupport).value.run
      result shouldEqual Either.right(location)

      there was one(mockServices).getLocation(any)
    }

    "return a RecognitionProcessException when the service return an exception" in new RecognitionProcessScope {

      mockServices.getLocation(any) returns TaskService(Task(Either.left(awarenessException)))

      mustLeft[RecognitionProcessException](process.getLocation(contextSupport))
    }

  }

  "getWeather" should {

    "get weather" in new RecognitionProcessScope {

      mockServices.getWeather returns TaskService(Task(Either.right(weatherState)))

      val result = process.getWeather.value.run
      result shouldEqual Either.right(weather)

      there was one(mockServices).getWeather
    }

    "return a RecognitionProcessException when the service return an exception" in new RecognitionProcessScope {

      mockServices.getWeather returns TaskService(Task(Either.left(awarenessException)))

      mustLeft[RecognitionProcessException](process.getWeather)
    }

  }

}
