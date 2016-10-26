package cards.nine.app.ui.launcher.actions.addmoment

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.action_filters.MomentAddedOrRemovedActionFilter
import cards.nine.app.ui.commons.{BroadAction, Jobs}
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.{Moment, MomentData, MomentTimeSlot}
import cards.nine.models.types._
import macroid.ActivityContextWrapper

class AddMomentJobs(actions: AddMomentUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions {

  def initialize(): TaskService[Unit] =
    for {
      _ <- actions.initialize()
      _ <- loadMoments()
    } yield ()

  def loadMoments(): TaskService[Unit] = {

    def getMomentNotUsed(currentMoments: Seq[Moment]): Seq[NineCardsMoment] =
      NineCardsMoment.moments filterNot (moment => currentMoments map (_.momentType) contains moment)

    for {
      _ <- actions.showLoading()
      moments <- di.momentProcess.getMoments
      momentNotUsed = getMomentNotUsed(moments)
      _ <- if (momentNotUsed.isEmpty) {
        actions.showEmptyMessageInScreen()
      } else {
        actions.addMoments(momentNotUsed)
      }
    } yield ()
  }

  def addMoment(nineCardsMoment: NineCardsMoment): TaskService[Unit] = {
    val moment = MomentData(
      collectionId = None,
      timeslot = toMomentTimeSlotSeq(nineCardsMoment),
      wifi = Seq.empty,
      headphone = false,
      momentType = nineCardsMoment)
    for {
      _ <- di.momentProcess.saveMoments(Seq(moment))
      _ <- sendBroadCastTask(BroadAction(MomentAddedOrRemovedActionFilter.action))
      _ <- actions.close()
    } yield ()
  }

  // We should remove this method when we resolves issue #1001
  private[this] def toMomentTimeSlotSeq(moment: NineCardsMoment): Seq[MomentTimeSlot] =
    moment match {
      case HomeMorningMoment => Seq(MomentTimeSlot(from = "08:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
      case WorkMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
      case HomeNightMoment => Seq(MomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)), MomentTimeSlot(from = "00:00", to = "08:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
      case StudyMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
      case MusicMoment => Seq.empty
      case CarMoment => Seq.empty
      case SportMoment => Seq.empty
      case OutAndAboutMoment => Seq(MomentTimeSlot(from = "00:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)))
      case UnknownMoment(_) => Seq.empty
    }

}
