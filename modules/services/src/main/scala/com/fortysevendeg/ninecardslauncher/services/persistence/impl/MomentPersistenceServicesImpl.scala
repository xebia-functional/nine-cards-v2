package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService._
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions

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

  def updateMoment(request: UpdateMomentRequest) =
    (for {
      updated <- momentRepository.updateMoment(toRepositoryMoment(request))
    } yield updated).resolve[PersistenceServiceException]
}
