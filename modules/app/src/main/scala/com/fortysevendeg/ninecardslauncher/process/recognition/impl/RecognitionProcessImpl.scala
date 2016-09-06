package com.fortysevendeg.ninecardslauncher.process.recognition.impl

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.recognition._
import com.fortysevendeg.ninecardslauncher.services.awareness.AwarenessServices
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._

class RecognitionProcessImpl(awarenessServices: AwarenessServices)
  extends RecognitionProcess
  with Conversions
  with ImplicitsRecognitionProcessExceptions {

  override def getMostProbableActivity: TaskService[ProbablyActivity] =
    (awarenessServices.getTypeActivity map toProbablyActivity).resolve[RecognitionProcessException]

  override def getHeadphone: TaskService[Headphones] =
    (awarenessServices.getHeadphonesState map toHeadphones).resolve[RecognitionProcessException]

  override def getWeather: TaskService[Weather] =
    (awarenessServices.getWeather map toWeather).resolve[RecognitionProcessException]

}
