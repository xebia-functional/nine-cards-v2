package com.fortysevendeg.ninecardslauncher.process.recognition.impl

import android.location.Geocoder
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.recognition._
import com.fortysevendeg.ninecardslauncher.services.awareness.{AwarenessServices, LocationState}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService


class RecognitionProcessImpl(awarenessServices: AwarenessServices)
  extends RecognitionProcess
    with Conversions
    with ImplicitsRecognitionProcessExceptions {

  override def getMostProbableActivity: TaskService[ProbablyActivity] =
    (awarenessServices.getTypeActivity map toProbablyActivity).resolve[RecognitionProcessException]

  override def getHeadphone: TaskService[Headphones] =
    (awarenessServices.getHeadphonesState map toHeadphones).resolve[RecognitionProcessException]

  override def getLocation(implicit contextSupport: ContextSupport): TaskService[Location] = {

    def loadAddress(locationState: LocationState): TaskService[Location] =
      TaskService {
        CatchAll[RecognitionProcessException] {
          val address = new Geocoder(contextSupport.context)
            .getFromLocation(locationState.latitude, locationState.longitude, 1)
          Option(address) match {
            case Some(list) if list.size() > 0 => toLocation(list.get(0))
            case None => throw new IllegalStateException("Geocoder doesn't return a valid address")
          }
        }
      }


    for {
      locationState <- awarenessServices.getLocation.resolve[RecognitionProcessException]
      location <- loadAddress(locationState)
    } yield location
  }

  override def getWeather: TaskService[Weather] =
    (awarenessServices.getWeather map toWeather).resolve[RecognitionProcessException]

}
