package cards.nine.process.recognition.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.process.recognition._
import cards.nine.services.awareness.AwarenessServices

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
