package cards.nine.process.moment

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.{KindActivity, NineCardsMoment}
import cards.nine.models.{Collection, Moment, MomentData, WidgetData}

trait MomentProcess {

  /**
    * Gets the existing moments
    *
    * @return the Seq[cards.nine.models.Moment]
    * @throws MomentException if there was an error getting the existing moments
    */
  def getMoments: TaskService[Seq[Moment]]

  /**
    * Get moment by type, if the moment don't exist return an exception
    *
    * @param momentType type of moment
    * @return the cards.nine.models.Moment
    * @throws MomentException if there was an error getting the existing moments
    */
  def getMomentByType(momentType: NineCardsMoment): TaskService[Moment]

  /**
    * Get moment by type, if the moment don't exist return None
    *
    * @param momentType type of moment
    * @return the Option[cards.nine.models.Moment]
    * @throws MomentException if there was an error getting the existing moments
    */
  def fetchMomentByType(momentType: NineCardsMoment): TaskService[Option[Moment]]

  /**
    * Create new Moment without collection by type
    *
    * @return the List[cards.nine.models.Moment]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def createMomentWithoutCollection(nineCardsMoment: NineCardsMoment)(implicit context: ContextSupport): TaskService[Moment]

  /**
    * Creates Moments from some already formed and given Moments
    *
    * @param moment the cards.nine.models.Moment of Moments
    * @throws MomentException if there was an error creating the moments' collections
    */
  def updateMoment(moment: Moment)(implicit context: ContextSupport): TaskService[Unit]

  /**
    * Creates Moments from some already formed and given Moments
    *
    * @param moments sequence of of cards.nine.models.MomentData
    * @return the List[cards.nine.models.Moment]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def saveMoments(moments: Seq[MomentData])(implicit context: ContextSupport): TaskService[Seq[Moment]]

  /**
    * Delete moment in database
    *
    * @throws MomentException if exist some problem to get the app or storing it
    */
  def deleteMoment(momentId: Int): TaskService[Unit]

  /**
    * Delete all moments in database
    *
    * @throws MomentException if exist some problem to get the app or storing it
    */
  def deleteAllMoments(): TaskService[Unit]

  /**
    * Gets the best available moments
    *
    * @return the best Moment or None if the database is empty
    * @throws MomentException if there was an error getting the best moment
    */
  def getBestAvailableMoment(
    maybeHeadphones: Option[Boolean] = None,
    maybeActivity: Option[KindActivity] = None)(implicit context: ContextSupport): TaskService[Option[Moment]]

  /**
    * Gets all available moments. Only the moments with collection
    *
    * @return sequence of tuples of cards.nine.models.Moment and cards.nine.models.Collection
    * @throws MomentException if there was an error getting the best moment
    */
  @deprecated
  def getAvailableMoments(implicit context: ContextSupport): TaskService[Seq[(Moment, Collection)]]

}
