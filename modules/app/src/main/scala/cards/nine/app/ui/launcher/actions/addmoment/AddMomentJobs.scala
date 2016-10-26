package cards.nine.app.ui.launcher.actions.addmoment

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.action_filters.MomentAddedOrRemovedActionFilter
import cards.nine.app.ui.commons.{BroadAction, Jobs}
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.Moment.MomentTimeSlotOps
import cards.nine.models.types._
import cards.nine.models.{Moment, MomentData}
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
      timeslot = nineCardsMoment.toMomentTimeSlot,
      wifi = Seq.empty,
      headphone = false,
      momentType = nineCardsMoment)
    for {
      _ <- di.momentProcess.saveMoments(Seq(moment))
      _ <- sendBroadCastTask(BroadAction(MomentAddedOrRemovedActionFilter.action))
      _ <- actions.close()
    } yield ()
  }

}
