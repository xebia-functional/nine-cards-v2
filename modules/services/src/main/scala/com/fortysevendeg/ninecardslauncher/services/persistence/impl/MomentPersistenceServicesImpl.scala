package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.Moment
import com.fortysevendeg.ninecardslauncher.repository.provider.MomentEntity
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions

import scalaz.concurrent.Task

trait MomentPersistenceServicesImpl extends PersistenceServices {

  self: Conversions
    with PersistenceDependencies
    with WidgetPersistenceServicesImpl
    with ImplicitsPersistenceServiceExceptions =>

  def addMoment(request: AddMomentRequest) =
    (for {
      moment <- momentRepository.addMoment(toRepositoryMomentData(request))
      _ <- addWidgets(request.widgets map (w => toAddWidgetRequest(moment.id, w)))
    } yield toMoment(moment)).resolve[PersistenceServiceException]

  def addMoments(request: Seq[AddMomentRequest]) = {
    val widgetsData = request map (_.widgets)
    (for {
      moments <- momentRepository.addMoments(request map toRepositoryMomentData)
      widgets = moments.zip(widgetsData) flatMap {
        case (moment, widgetRequest) => toAddWidgetRequestSeq(moment.id, widgetRequest)
      }
      _ <- addWidgets(widgets)
    } yield moments map toMoment).resolve[PersistenceServiceException]
  }

  def deleteAllMoments() =
    (for {
      deleted <- momentRepository.deleteMoments()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteMoment(request: DeleteMomentRequest) =
    (for {
      deleted <- momentRepository.deleteMoment(toRepositoryMoment(request.moment))
    } yield deleted).resolve[PersistenceServiceException]

  def fetchMoments =
    (for {
      momentItems <- momentRepository.fetchMoments()
    } yield momentItems map toMoment).resolve[PersistenceServiceException]

  def findMomentById(request: FindMomentByIdRequest) =
    (for {
      maybeMoment <- momentRepository.findMomentById(request.id)
    } yield maybeMoment map toMoment).resolve[PersistenceServiceException]

  def getMomentByType(momentType: String) =
    (for {
      moments <- momentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentType))
      moment <- getHead(moments.headOption)
    } yield toMoment(moment)).resolve[PersistenceServiceException]

  def fetchMomentByType(momentType: String) =
    (for {
      moments <- momentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentType))
    } yield moments.headOption map toMoment).resolve[PersistenceServiceException]

  def updateMoment(request: UpdateMomentRequest) =
    (for {
      updated <- momentRepository.updateMoment(toRepositoryMoment(request))
    } yield updated).resolve[PersistenceServiceException]

  private[this] def getHead(maybeMoment: Option[Moment]): TaskService[Moment]=
    maybeMoment map { m =>
      TaskService(Task(Xor.right(m)))
    } getOrElse TaskService(Task(Xor.left(RepositoryException("Moment not found"))))
}
