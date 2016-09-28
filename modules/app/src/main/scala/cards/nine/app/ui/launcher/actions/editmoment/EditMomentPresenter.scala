package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.editmoment

import com.fortysevendeg.ninecardslauncher.app.commons.BroadAction
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.MomentConstrainsChangedActionFilter
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, Moment, MomentTimeSlot}
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher.process.moment.UpdateMomentRequest
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import macroid._

class EditMomentPresenter(actions: EditMomentActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs {

  var statuses = EditMomentStatuses()

  def initialize(moment: NineCardsMoment): Unit = {

    def getData = for {
      moment <- di.momentProcess.getMomentByType(moment)
      collections <- di.collectionProcess.getCollections
    } yield (moment, collections)

    getData.resolveAsyncUi2(
      onResult = {
        case (moment: Moment, collections: Seq[Collection]) =>
          statuses = statuses.start(moment)
          actions.initialize(moment, collections)
        case _ => actions.momentNoFound()
      },
      onException = (_) => actions.momentNoFound())
  }

  def momentNoFound(): Unit = actions.momentNoFound().run

  def setCollectionId(collectionId: Option[Int]): Unit =
    statuses = statuses.setCollectionId(collectionId match {
      case Some(0) => None
      case c => c
    })

  def swapDay(position: Int, day: Int): Unit = {
    statuses = statuses.swapDay(position, day)
    changePosition(position)
  }

  def changeFromHour(position: Int, hour: String): Unit = {
    statuses = statuses.changeFromHour(position, hour)
    changePosition(position)
  }

  def changeToHour(position: Int, hour: String): Unit = {
    statuses = statuses.changeToHour(position, hour)
    changePosition(position)
  }

  def addHour(): Unit = {
    val newTimeslot = MomentTimeSlot(from = "9:00", to = "14:00", days = Seq(0, 0, 0, 0, 0, 0, 0))
    val containsTimeslot = statuses.modifiedMoment exists (_.timeslot.contains(newTimeslot))
    if (containsTimeslot) {
      actions.showItemDuplicatedMessage().run
    } else {
      statuses = statuses.addHour(newTimeslot)
      (statuses.modifiedMoment match {
        case Some(moment) => actions.loadHours(moment)
        case _ => actions.showSavingMomentErrorMessage()
      }).run
    }
  }

  def removeHour(position: Int): Unit = {
    statuses = statuses.removeHour(position)
    (statuses.modifiedMoment match {
      case Some(moment) => actions.loadHours(moment)
      case _ => actions.showSavingMomentErrorMessage()
    }).run
  }

  def addWifi(): Unit = {
    di.deviceProcess.getConfiguredNetworks.resolveAsyncUi2(
      onResult = actions.showWifiDialog,
      onException = (_) => actions.showFieldErrorMessage()
    )
  }

  def addWifi(wifi: String): Unit = {
    val containsWifi = statuses.modifiedMoment exists (_.wifi.contains(wifi))
    if (containsWifi) {
      actions.showItemDuplicatedMessage().run
    } else {
      statuses = statuses.addWifi(wifi)
      (statuses.modifiedMoment match {
        case Some(moment) => actions.loadWifis(moment)
        case _ => actions.showSavingMomentErrorMessage()
      }).run
    }
  }

  def removeWifi(position: Int): Unit = {
    statuses = statuses.removeWifi(position)
    (statuses.modifiedMoment match {
      case Some(moment) => actions.loadWifis(moment)
      case _ => actions.showSavingMomentErrorMessage()
    }).run
  }

  def saveMoment(): Unit = (statuses.wasModified(), statuses.modifiedMoment) match {
    case (true, Some(moment)) =>
      val request = UpdateMomentRequest(
        id = moment.id,
        collectionId = moment.collectionId,
        timeslot = moment.timeslot,
        wifi = moment.wifi,
        headphone = moment.headphone,
        momentType = moment.momentType
      )
      di.momentProcess.updateMoment(request).resolveAsyncUi2(
        onResult = (_) => Ui(momentConstrainsChangedBroadCastIfNecessary()) ~ actions.success(),
        onException = (_) => actions.showSavingMomentErrorMessage())
    case _ => actions.success().run
  }

  private[this] def changePosition(position: Int): Unit = {
    val timeslot = statuses.modifiedMoment flatMap (_.timeslot.lift(position))
    (timeslot match {
      case Some(ts) => actions.reloadDays(position, ts)
      case _ => actions.showSavingMomentErrorMessage()
    }).run
  }

  private[this] def momentConstrainsChangedBroadCastIfNecessary() =
    sendBroadCast(BroadAction(MomentConstrainsChangedActionFilter.action))

}

case class EditMomentStatuses(
  moment: Option[Moment] = None,
  modifiedMoment: Option[Moment] = None) {

  def start(m: Moment): EditMomentStatuses = copy(moment = Option(m), modifiedMoment = Option(m))

  def setCollectionId(collectionId: Option[Int]) =
    copy(modifiedMoment = modifiedMoment map (_.copy(collectionId = collectionId)))

  def swapDay(position: Int, day: Int) = {
    val modMoment = modifiedMoment map { m =>
      val timeslots = m.timeslot.zipWithIndex map {
        case (timeslot, index) if index == position =>
          val modDays = timeslot.days.zipWithIndex map {
            case (s, indexDay) if indexDay == day && s == 0 => 1
            case (s, indexDay) if indexDay == day => 0
            case (s, indexDay) => s
          }
          timeslot.copy(days = modDays)
        case (timeslot, _) => timeslot
      }
      m.copy(timeslot = timeslots)
    }
    copy(modifiedMoment = modMoment)
  }

  def changeFromHour(position: Int, hour: String) = {
    val modMoment = modifiedMoment map { m =>
      val timeslots = m.timeslot.zipWithIndex map {
        case (timeslot, index) if index == position => timeslot.copy(from = hour)
        case (timeslot, _) => timeslot
      }
      m.copy(timeslot = timeslots)
    }
    copy(modifiedMoment = modMoment)
  }

  def changeToHour(position: Int, hour: String) = {
    val modMoment = modifiedMoment map { m =>
      val timeslots = m.timeslot.zipWithIndex map {
        case (timeslot, index) if index == position => timeslot.copy(to = hour)
        case (timeslot, _) => timeslot
      }
      m.copy(timeslot = timeslots)
    }
    copy(modifiedMoment = modMoment)
  }

  def addHour(time: MomentTimeSlot) = {
    val modMoment = modifiedMoment map { m =>
      m.copy(timeslot = m.timeslot :+ time)
    }
    copy(modifiedMoment = modMoment)
  }

  def removeHour(position: Int) = {
    val modMoment = modifiedMoment map { m =>
      m.copy(timeslot = m.timeslot.filterNot(m.timeslot.indexOf(_) == position))
    }
    copy(modifiedMoment = modMoment)
  }

  def addWifi(wifi: String) = {
    val modMoment = modifiedMoment map { moment =>
      moment.copy(wifi = moment.wifi :+ wifi)
    }
    copy(modifiedMoment = modMoment)
  }

  def removeWifi(position: Int) = {
    val modMoment = modifiedMoment map { m =>
      m.copy(wifi = m.wifi.filterNot(m.wifi.indexOf(_) == position))
    }
    copy(modifiedMoment = modMoment)
  }

  def wasModified(): Boolean = moment != modifiedMoment

}

trait EditMomentActions {

  def initialize(moment: Moment, collections: Seq[Collection]): Ui[Any]

  def momentNoFound(): Ui[Any]

  def success(): Ui[Any]

  def showSavingMomentErrorMessage(): Ui[Any]

  def reloadDays(position: Int, timeslot: MomentTimeSlot): Ui[Any]

  def loadHours(moment: Moment): Ui[Any]

  def showWifiDialog(wifis: Seq[String]): Ui[Any]

  def loadWifis(moment: Moment): Ui[Any]

  def showFieldErrorMessage(): Ui[Any]

  def showItemDuplicatedMessage(): Ui[Any]

}