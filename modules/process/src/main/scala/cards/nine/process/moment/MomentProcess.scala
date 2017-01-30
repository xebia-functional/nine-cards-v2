/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.process.moment

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.{KindActivity, NineCardsMoment}
import cards.nine.models.{Moment, MomentData}

trait MomentProcess {

  /**
   * Gets the existing moments
   *
   * @return the Seq[cards.nine.models.Moment]
   * @throws MomentException if there was an error getting the existing moments
   */
  def getMoments: TaskService[Seq[Moment]]

  /**
   * Gets a moment by the collectionId
   *
   * @param collectionId the id of the collection related
   * @return the Option[cards.nine.models.Moment]
   * @throws MomentException if there was an error getting the existing moments
   */
  def getMomentByCollectionId(collectionId: Int): TaskService[Option[Moment]]

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
  @deprecated
  def fetchMomentByType(momentType: NineCardsMoment): TaskService[Option[Moment]]

  /**
   * Get moment by id, if the moment don't exist return None
   *
   * @param momentId id of moment
   * @return the Option[cards.nine.models.Moment]
   * @throws MomentException if there was an error getting the existing moments
   */
  def findMoment(momentId: Int): TaskService[Option[Moment]]

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
  def saveMoments(moments: Seq[MomentData])(
      implicit context: ContextSupport): TaskService[Seq[Moment]]

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
      maybeActivity: Option[KindActivity] = None)(
      implicit context: ContextSupport): TaskService[Option[Moment]]

}
