package cards.nine.app.ui.commons.dialogs.editmoment

import cards.nine.app.ui.commons.action_filters.MomentConstrainsChangedActionFilter
import cards.nine.app.ui.commons.{BroadAction, JobException, Jobs}
import cards.nine.app.ui.commons.dialogs.editmoment.EditMomentFragment._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.types.NineCardsMoment
import cards.nine.models.{Moment, MomentTimeSlot}
import macroid.ActivityContextWrapper

class EditMomentJobs(actions: EditMomentUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs {

  def initialize(nineCardsMoment: NineCardsMoment): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.editMoment(nineCardsMoment.name)
      moment <- di.momentProcess.getMomentByType(nineCardsMoment)
      collections <- di.collectionProcess.getCollections
      _ <- updateStatus(statuses.start(moment))
      _ <- actions.initialize(moment, collections)
    } yield ()

  def momentNotFound(): TaskService[Unit] = actions.close()

  def setCollectionId(collectionId: Option[Int]): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.quickAccessToCollection()
      _ <- updateStatus(statuses.setCollectionId(collectionId match {
        case Some(0) => None
        case id => id
      }))
    } yield ()

  def swapDay(position: Int, day: Int): TaskService[Unit] = {
    statuses = statuses.swapDay(position, day)
    changePosition(position)
  }

  def changeFromHour(position: Int, hour: String): TaskService[Unit] = {
    statuses = statuses.changeFromHour(position, hour)
    changePosition(position)
  }

  def changeToHour(position: Int, hour: String): TaskService[Unit] = {
    statuses = statuses.changeToHour(position, hour)
    changePosition(position)
  }

  def addHour(): TaskService[Unit] = {
    val newTimeslot = MomentTimeSlot(from = "9:00", to = "14:00", days = Seq(0, 0, 0, 0, 0, 0, 0))
    val containsTimeslot = statuses.modifiedMoment exists (_.timeslot.contains(newTimeslot))
    if (containsTimeslot) {
      actions.showItemDuplicatedMessage()
    } else {
      for {
        _ <- di.trackEventProcess.setHours()
        _ <- updateStatus(statuses.addHour(newTimeslot))
        _ <- statuses.modifiedMoment match {
          case Some(moment) => actions.loadHours(moment)
          case _ => actions.showSavingMomentErrorMessage()
        }
      } yield ()
    }
  }

  def removeHour(position: Int): TaskService[Unit] =
    for {
      _ <- updateStatus(statuses.removeHour(position))
      _ <- statuses.modifiedMoment match {
        case Some(moment) => actions.loadHours(moment)
        case _ => actions.showSavingMomentErrorMessage()
      }
    } yield ()

  def addWifi(): TaskService[Unit] =
    for {
      wifis <- di.deviceProcess.getConfiguredNetworks
      _ <- actions.showWifiDialog(wifis)
    } yield ()

  def addWifi(wifi: String): TaskService[Unit] = {
    val containsWifi = statuses.modifiedMoment exists (_.wifi.contains(wifi))
    if (containsWifi) {
      actions.showItemDuplicatedMessage()
    } else {
      for {
        _ <- di.trackEventProcess.setWifi()
        _ <- updateStatus(statuses.addWifi(wifi))
        _ <- statuses.modifiedMoment match {
          case Some(moment) => actions.loadWifis(moment)
          case _ => actions.showSavingMomentErrorMessage()
        }
      } yield ()
    }
  }

  def removeWifi(position: Int): TaskService[Unit] =
    for {
      _ <- updateStatus(statuses.removeWifi(position))
      _ <- statuses.modifiedMoment match {
        case Some(moment) => actions.loadWifis(moment)
        case _ => actions.showSavingMomentErrorMessage()
      }
    } yield ()

  def saveMoment(): TaskService[Unit] = (statuses.wasModified(), statuses.modifiedMoment) match {
    case (true, Some(moment)) =>
      val request = Moment(
        id = moment.id,
        collectionId = moment.collectionId,
        timeslot = moment.timeslot,
        wifi = moment.wifi,
        headphone = moment.headphone,
        momentType = moment.momentType)
      for {
        _ <- di.momentProcess.updateMoment(request)
        _ <- sendBroadCastTask(BroadAction(MomentConstrainsChangedActionFilter.action))
        _ <- actions.close()
      } yield ()
    case _ => actions.close()
  }

  private[this] def changePosition(position: Int): TaskService[Unit] = {
    val timeslot = statuses.modifiedMoment flatMap (_.timeslot.lift(position))
    timeslot match {
      case Some(ts) => actions.reloadDays(position, ts)
      case _ => TaskService.left(JobException("Timeslot not found"))
    }
  }

  private[this] def updateStatus(editMomentStatuses: EditMomentStatuses): TaskService[Unit] =
    TaskService.right(statuses = editMomentStatuses)

}
