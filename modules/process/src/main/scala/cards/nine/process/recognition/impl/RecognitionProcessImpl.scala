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

  override def registerFenceUpdates(
    action: String,
    receiver: BroadcastReceiver)(implicit contextSupport: ContextSupport) = {

    def getFencesFromMoments(moments: Seq[Moment]): Seq[AwarenessFenceUpdate] =
      moments.map(_.momentType).flatMap {
        case MusicMoment => Some(HeadphonesFence)
        case CarMoment => Some(InVehicleFence)
        case _ => None
      }

    (for {
      moments <- persistenceServices.fetchMoments
      fences = getFencesFromMoments(moments)
      _ <- if (fences.nonEmpty) awarenessServices.registerFenceUpdates(action, fences, receiver) else TaskService.empty
    } yield ()).resolve[RecognitionProcessException]
  }

  override def unregisterFenceUpdates(action: String)(implicit contextSupport: ContextSupport): TaskService[Unit] =
    awarenessServices.unregisterFenceUpdates(action).resolve[RecognitionProcessException]

  override def getHeadphone: TaskService[Headphones] =
    awarenessServices.getHeadphonesState.resolve[RecognitionProcessException]

  override def getLocation(implicit contextSupport: ContextSupport): TaskService[Location] =
    awarenessServices.getLocation.resolve[RecognitionProcessException]

  override def getWeather: TaskService[WeatherState] =
    awarenessServices.getWeather.resolve[RecognitionProcessException]

}
