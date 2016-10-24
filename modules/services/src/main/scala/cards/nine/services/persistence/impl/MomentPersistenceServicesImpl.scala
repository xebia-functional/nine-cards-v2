package cards.nine.services.persistence.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.NineCardsMoment
import cards.nine.models.{Moment, MomentData, WidgetData}
import cards.nine.repository.RepositoryException
import cards.nine.repository.model.{Moment => RepositoryMoment}
import cards.nine.repository.provider.MomentEntity
import cards.nine.services.persistence._
import cards.nine.services.persistence.conversions.Conversions

trait MomentPersistenceServicesImpl extends PersistenceServices {

  self: Conversions
    with PersistenceDependencies
    with WidgetPersistenceServicesImpl
    with ImplicitsPersistenceServiceExceptions =>

  def addMoment(momentData: MomentData) =
    (for {
      moment <- momentRepository.addMoment(toRepositoryMomentData(momentData))
      _ <- addWidgets(getWidgets(momentData.widgets) map (widget => widget.copy(momentId = moment.id)))
    } yield toMoment(moment)).resolve[PersistenceServiceException]

  private[this] def getWidgets(maybeWidgets: Option[Seq[WidgetData]]) =
    maybeWidgets match {
      case Some(widgets) => widgets
      case None => Seq.empty
    }

  def addMoments(moments: Seq[MomentData]) = {
    val widgetsData = moments map (moment => getWidgets(moment.widgets))
    (for {
      momentsAdded <- momentRepository.addMoments(moments map toRepositoryMomentData)
      widgets = momentsAdded.zip(widgetsData) flatMap {
        case (moment, widgetRequest) => widgetRequest map (widget => widget.copy(momentId = moment.id))
      }
      _ <- addWidgets(widgets)
    } yield momentsAdded map toMoment).resolve[PersistenceServiceException]
  }

  def deleteAllMoments() =
    (for {
      deleted <- momentRepository.deleteMoments()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteMoment(momentId: Int): TaskService[Int] =
    (for {
      deleted <- momentRepository.deleteMoment(momentId)
    } yield deleted).resolve[PersistenceServiceException]

  def fetchMoments =
    (for {
      momentItems <- momentRepository.fetchMoments()
    } yield momentItems map toMoment).resolve[PersistenceServiceException]

  def findMomentById(momentId: Int) =
    (for {
      maybeMoment <- momentRepository.findMomentById(momentId)
    } yield maybeMoment map toMoment).resolve[PersistenceServiceException]

  def getMomentByType(momentType: NineCardsMoment) = {

    def readFirstMoment(moments: Seq[RepositoryMoment]): TaskService[Moment] = moments.headOption match {
      case Some(m) => TaskService.right(toMoment(m))
      case _ => TaskService.left(RepositoryException("Moment not found"))
    }

    (for {
      moments <- momentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentType.name))
      moment <- readFirstMoment(moments)
    } yield Option(moment).getOrElse(throw new RuntimeException(""))).resolve[PersistenceServiceException]
  }

  def fetchMomentByType(momentType: String) =
    (for {
      moments <- momentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentType))
    } yield moments.headOption map toMoment).resolve[PersistenceServiceException]

  def fetchMomentById(momentId: Int): TaskService[Option[Moment]] =
    (for {
      moment <- momentRepository.findMomentById(momentId)
    } yield moment map toMoment).resolve[PersistenceServiceException]

  def updateMoment(moment: Moment) =
    (for {
      updated <- momentRepository.updateMoment(toRepositoryMoment(moment))
    } yield updated).resolve[PersistenceServiceException]
}
