package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.editmoment

import com.fortysevendeg.ninecardslauncher.app.commons.BroadAction
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.MomentsReloadedActionFilter
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, Moment}
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher.process.moment.UpdateMomentRequest
import macroid._

import scalaz.concurrent.Task

class EditMomentPresenter(actions: EditMomentActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter {

  var statuses = EditMomentStatuses()

  def initialize(moment: NineCardsMoment): Unit = {

    def getData = for {
      moment <- di.momentProcess.getMomentByType(moment)
      collections <- di.collectionProcess.getCollections
    } yield (moment, collections)

    Task.fork(getData.run).resolveAsyncUi(
      onResult = {
        case (moment: Moment, collections: Seq[Collection]) =>
          statuses = statuses.start(moment)
          actions.initialize(moment, collections)
        case _ => actions.momentNoFound()
      },
      onException = (_) => actions.momentNoFound())
  }

  def momentNoFound(): Unit = actions.momentNoFound().run

  def setCollectionId(collectionId: Option[Int]): Unit = statuses = statuses.setCollectionId(collectionId)

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
      Task.fork(di.momentProcess.updateMoment(request).run).resolveAsyncUi(
        onResult = (_) => Ui(momentReloadBroadCastIfNecessary()) ~ actions.success(),
        onException = (_) => actions.showSavingMomentErrorMessage())
    case _ =>
  }

  private[this] def momentReloadBroadCastIfNecessary() = sendBroadCast(BroadAction(MomentsReloadedActionFilter.action))

}

case class EditMomentStatuses(
  moment: Option[Moment] = None,
  modifiedMoment: Option[Moment] = None) {

  def start(m: Moment): EditMomentStatuses = copy(moment = Option(m), modifiedMoment = Option(m))

  def setCollectionId(collectionId: Option[Int]) =
    copy(modifiedMoment = modifiedMoment map (_.copy(collectionId = collectionId)))

  def wasModified(): Boolean = moment != modifiedMoment

}

trait EditMomentActions {

  def initialize(moment: Moment, collections: Seq[Collection]): Ui[Any]

  def momentNoFound(): Ui[Any]

  def success(): Ui[Any]

  def showSavingMomentErrorMessage(): Ui[Any]

}