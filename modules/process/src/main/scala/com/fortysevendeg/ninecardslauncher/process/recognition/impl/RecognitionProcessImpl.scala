package com.fortysevendeg.ninecardslauncher.process.recognition.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.{TaskService, _}
import com.fortysevendeg.ninecardslauncher.process.recognition._
import com.fortysevendeg.ninecardslauncher.services.awareness.AwarenessServices

class RecognitionProcessImpl(awarenessServices: AwarenessServices)
  extends RecognitionProcess
  with Conversions
  with ImplicitsRecognitionProcessExceptions {

  override def getMostProbableActivity: TaskService[ProbablyActivity] =
    (awarenessServices.getTypeActivity map toProbablyActivity).resolve[RecognitionProcessException]

  override def getHeadphone: TaskService[Headphones] =
    (awarenessServices.getHeadphonesState map toHeadphones).resolve[RecognitionProcessException]

  override def getLocation(implicit contextSupport: ContextSupport): TaskService[Location] =
    (awarenessServices.getLocation map toLocation).resolve[RecognitionProcessException]

  override def getWeather: TaskService[Weather] =
    (awarenessServices.getWeather map toWeather).resolve[RecognitionProcessException]

}
