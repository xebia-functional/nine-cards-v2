package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService._
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.Moment
import com.fortysevendeg.ninecardslauncher.repository.provider.MomentEntity
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import rapture.core.{Answer, Errata}

import scalaz.concurrent.Task

trait MomentPersistenceServicesImpl extends PersistenceServices {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def addMoment(request: AddMomentRequest) =
    (for {
      moment <- momentRepository.addMoment(toRepositoryMomentData(request))
    } yield toMoment(moment)).resolve[PersistenceServiceException]

  def addMoments(request: Seq[AddMomentRequest]) =
    (for {
      moment <- momentRepository.addMoments(request map toRepositoryMomentData)
    } yield moment map toMoment).resolve[PersistenceServiceException]

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

  def fetchMomentByType(momentType: String) =
    (for {
      moments <- momentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentType))
      moment <- getHead(moments.headOption)
    } yield toMoment(moment)).resolve[PersistenceServiceException]

  def updateMoment(request: UpdateMomentRequest) =
    (for {
      updated <- momentRepository.updateMoment(toRepositoryMoment(request))
    } yield updated).resolve[PersistenceServiceException]

  private[this] def getHead(maybeMoment: Option[Moment]): ServiceDef2[Moment, RepositoryException]=
    maybeMoment map { m =>
      Service(Task(Answer[Moment, RepositoryException](m)))
    } getOrElse Service(Task(Errata(RepositoryException("Moment not found"))))
}
