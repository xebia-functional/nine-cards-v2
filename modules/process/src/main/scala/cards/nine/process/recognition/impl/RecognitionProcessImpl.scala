package cards.nine.process.recognition.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.{Location, Headphones, ProbablyActivity, WeatherState}
import cards.nine.process.recognition._
import cards.nine.services.awareness.AwarenessServices

class RecognitionProcessImpl(awarenessServices: AwarenessServices)
  extends RecognitionProcess
  with ImplicitsRecognitionProcessExceptions {

  override def getMostProbableActivity: TaskService[ProbablyActivity] =
    awarenessServices.getTypeActivity.resolve[RecognitionProcessException]

  override def getHeadphone: TaskService[Headphones] =
    awarenessServices.getHeadphonesState.resolve[RecognitionProcessException]

  override def getLocation(implicit contextSupport: ContextSupport): TaskService[Location] =
    awarenessServices.getLocation.resolve[RecognitionProcessException]

  override def getWeather: TaskService[WeatherState] =
    awarenessServices.getWeather.resolve[RecognitionProcessException]

}
