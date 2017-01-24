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

package cards.nine.process.recommendations.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.NotCategorizedPackage
import cards.nine.process.recommendations._
import cards.nine.process.utils.ApiUtils
import cards.nine.services.api.{ApiServiceConfigurationException, ApiServices}
import cards.nine.services.persistence.PersistenceServices
import cards.nine.models.types.NineCardsCategory

class RecommendationsProcessImpl(
    apiServices: ApiServices,
    persistenceServices: PersistenceServices)
    extends RecommendationsProcess {

  val apiUtils = new ApiUtils(persistenceServices)

  val defaultRecommendedAppsLimit = 20
  val defaultSearchAppsLimit      = 20

  override def getRecommendedAppsByCategory(
      category: NineCardsCategory,
      excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getRecommendedApps(
        category.name,
        excludePackages,
        defaultRecommendedAppsLimit)(userConfig)
    } yield response.seq).resolveLeft(mapLeft)

  override def getRecommendedAppsByPackages(
      packages: Seq[String],
      excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getRecommendedAppsByPackages(
        packages,
        excludePackages,
        defaultRecommendedAppsLimit)(userConfig)
    } yield response.seq).resolveLeft(mapLeft)

  override def searchApps(query: String, excludePackages: Seq[String])(
      implicit context: ContextSupport): TaskService[Seq[NotCategorizedPackage]] =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.searchApps(query, excludePackages, defaultSearchAppsLimit)(
        userConfig)
    } yield response.seq).resolveLeft(mapLeft)

  private[this] def mapLeft[T]: (NineCardException) => Either[NineCardException, T] = {
    case e: ApiServiceConfigurationException =>
      Left(RecommendedAppsConfigurationException(e.message, Some(e)))
    case e => Left(RecommendedAppsException(e.message, Some(e)))
  }

}
