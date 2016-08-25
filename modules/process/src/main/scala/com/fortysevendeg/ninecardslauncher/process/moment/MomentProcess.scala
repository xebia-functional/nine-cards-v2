package com.fortysevendeg.ninecardslauncher.process.moment

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, Moment, MomentWithCollection, PrivateCollection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher.process.moment.models._

trait MomentProcess {

  /**
    * Gets the existing moments
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.moment.models.Moment]
    * @throws MomentException if there was an error getting the existing moments
    */
  def getMoments: CatsService[Seq[Moment]]

  /**
    * Get moment by type, if the moment don't exist return an exception
    *
    * @param momentType type of moment
    * @return the com.fortysevendeg.ninecardslauncher.process.moment.models.Moment
    * @throws MomentException if there was an error getting the existing moments
    */
  def getMomentByType(momentType: NineCardsMoment): CatsService[Moment]

  /**
    * Get moment by type, if the moment don't exist return None
    *
    * @param momentType type of moment
    * @return the com.fortysevendeg.ninecardslauncher.process.moment.models.Moment
    * @throws MomentException if there was an error getting the existing moments
    */
  def fetchMomentByType(momentType: NineCardsMoment): CatsService[Option[Moment]]

  /**
    * Creates Moments and their associated Collections with the apps installed in the device
    *
    * @return the List[com.fortysevendeg.ninecardslauncher.process.commons.models.Collection]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def createMoments(implicit context: ContextSupport): CatsService[Seq[Collection]]

  /**
    * Create new Moment without collection by type
    *
    * @return the List[com.fortysevendeg.ninecardslauncher.process.commons.models.Collection]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def createMomentWithoutCollection(nineCardsMoment: NineCardsMoment)(implicit context: ContextSupport): CatsService[Moment]

  /**
    * Creates Moments from some already formed and given Moments
    *
    * @param item the com.fortysevendeg.ninecardslauncher.process.moment.UpdateMomentRequest of Moments
    * @return Unit
    * @throws MomentException if there was an error creating the moments' collections
    */
  def updateMoment(item: UpdateMomentRequest)(implicit context: ContextSupport): CatsService[Unit]

  /**
    * Creates Moments from some already formed and given Moments
    *
    * @param items the Seq[com.fortysevendeg.ninecardslauncher.process.moment.SaveMomentRequest] of Moments
    * @return the List[com.fortysevendeg.ninecardslauncherprocess.moment.models.Moment]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def saveMoments(items: Seq[SaveMomentRequest])(implicit context: ContextSupport): CatsService[Seq[Moment]]

  /**
    * Generate Private Moments Collections with the apps installed in the device
    *
    * @param apps the Seq[com.fortysevendeg.ninecardslauncher.process.moment.models.App] with the apps' data
    * @param position the position of the next collection
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.commons.PrivateCollection]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def generatePrivateMoments(apps: Seq[App], position: Int)(implicit context: ContextSupport): CatsService[Seq[PrivateCollection]]

  /**
    * Delete all moments in database
    *
    * @throws MomentException if exist some problem to get the app or storing it
    */
  def deleteAllMoments(): CatsService[Unit]

  /**
    * Gets the best available moments
    *
    * @return the best com.fortysevendeg.ninecardslauncher.process.moment.models.Moment
    * @throws MomentException if there was an error getting the best moment
    */
  def getBestAvailableMoment(implicit context: ContextSupport): CatsService[Option[Moment]]

  /**
    * Gets all available moments. Only the moments with collection
    *
    * @return sequuence com.fortysevendeg.ninecardslauncher.process.moment.models.Moment
    * @throws MomentException if there was an error getting the best moment
    */
  def getAvailableMoments(implicit context: ContextSupport): CatsService[Seq[MomentWithCollection]]

}
