package com.fortysevendeg.ninecardslauncher.process.moment

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
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
  def getMoments: ServiceDef2[Seq[Moment], MomentException]

  /**
    * Get moment by type, if the moment don't exist return an exception
    *
    * @param momentType type of moment
    * @return the com.fortysevendeg.ninecardslauncher.process.moment.models.Moment
    * @throws MomentException if there was an error getting the existing moments
    */
  def getMomentByType(momentType: NineCardsMoment): ServiceDef2[Moment, MomentException]

  /**
    * Creates Moments and their associated Collections with the apps installed in the device
    *
    * @return the List[com.fortysevendeg.ninecardslauncher.process.commons.models.Collection]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def createMoments(implicit context: ContextSupport): ServiceDef2[Seq[Collection], MomentException]

  /**
    * Creates Moments from some already formed and given Moments
    *
    * @param items the Seq[com.fortysevendeg.ninecardslauncher.process.moment.models.Moment] of Moments
    * @return the List[com.fortysevendeg.ninecardslauncherprocess.moment.models.Moment]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def saveMoments(items: Seq[SaveMomentRequest])(implicit context: ContextSupport): ServiceDef2[Seq[Moment], MomentException]

  /**
    * Generate Private Moments Collections with the apps installed in the device
    *
    * @param apps the Seq[com.fortysevendeg.ninecardslauncher.process.moment.models.App] with the apps' data
    * @param position the position of the next collection
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.commons.PrivateCollection]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def generatePrivateMoments(apps: Seq[App], position: Int)(implicit context: ContextSupport): ServiceDef2[Seq[PrivateCollection], MomentException]

  /**
    * Delete all moments in database
    *
    * @throws MomentException if exist some problem to get the app or storing it
    */
  def deleteAllMoments(): ServiceDef2[Unit, MomentException]

  /**
    * Gets the best available moments
    *
    * @return the best com.fortysevendeg.ninecardslauncher.process.moment.models.Moment
    * @throws MomentException if there was an error getting the best moment
    */
  def getBestAvailableMoment(implicit context: ContextSupport): ServiceDef2[Option[Moment], MomentException]

  /**
    * Gets all available moments. Only the moments with collection
    *
    * @return sequuence com.fortysevendeg.ninecardslauncher.process.moment.models.Moment
    * @throws MomentException if there was an error getting the best moment
    */
  def getAvailableMoments(implicit context: ContextSupport): ServiceDef2[Seq[MomentWithCollection], MomentException]

}
