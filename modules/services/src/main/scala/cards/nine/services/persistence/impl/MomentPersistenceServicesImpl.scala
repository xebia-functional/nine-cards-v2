package cards.nine.services.persistence.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{Moment, MomentData, PersistenceWidgetData}
import cards.nine.repository.RepositoryException
import cards.nine.repository.model.{Moment => RepositoryMoment}
import cards.nine.repository.provider.MomentEntity
import cards.nine.services.persistence._
import cards.nine.services.persistence.conversions.Conversions
import monix.eval.Task

trait MomentPersistenceServicesImpl extends PersistenceServices {

  self: Conversions
    with PersistenceDependencies
    with WidgetPersistenceServicesImpl
    with ImplicitsPersistenceServiceExceptions =>

  def addMoment(moment: MomentData, widgets: Seq[PersistenceWidgetData]) =
    (for {
      moment <- momentRepository.addMoment(toRepositoryMomentData(moment))
      _ <- addWidgets(widgets map (widget => widget.copy(momentId = moment.id)))
    } yield toMoment(moment)).resolve[PersistenceServiceException]

  def addMoments(momentsWithWidgets: Seq[(MomentData, Seq[PersistenceWidgetData])]) = {
    val moments = momentsWithWidgets map (_._1)
    val widgetsData = momentsWithWidgets flatMap (_._2)
    (for {
      momentsAdded <- momentRepository.addMoments(moments map toRepositoryMomentData)
      widgets = momentsAdded.zip(widgetsData) map {
        case (moment, widgetRequest) => widgetRequest.copy(momentId = moment.id)
      }
      _ <- addWidgets(widgets)
    } yield momentsAdded map toMoment).resolve[PersistenceServiceException]
  }

  def deleteAllMoments() =
    (for {
      deleted <- momentRepository.deleteMoments()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteMoment(moment: Moment) =
    (for {
      deleted <- momentRepository.deleteMoment(toRepositoryMoment(moment))
    } yield deleted).resolve[PersistenceServiceException]

  def fetchMoments =
    (for {
      momentItems <- momentRepository.fetchMoments()
    } yield momentItems map toMoment).resolve[PersistenceServiceException]

  def findMomentById(momentId: Int) =
    (for {
      maybeMoment <- momentRepository.findMomentById(momentId)
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

  def updateMoment(moment: Moment) =
    (for {
      updated <- momentRepository.updateMoment(toRepositoryMoment(moment))
    } yield updated).resolve[PersistenceServiceException]

  private[this] def getHead(maybeMoment: Option[RepositoryMoment]): TaskService[RepositoryMoment]=
    maybeMoment map { m =>
      TaskService(Task(Right(m)))
    } getOrElse TaskService(Task(Left(RepositoryException("Moment not found"))))
}
