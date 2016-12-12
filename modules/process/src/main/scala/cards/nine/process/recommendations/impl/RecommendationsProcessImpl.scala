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


class RecommendationsProcessImpl(apiServices: ApiServices, persistenceServices: PersistenceServices)
  extends RecommendationsProcess {

  val apiUtils = new ApiUtils(persistenceServices)

  val defaultRecommendedAppsLimit = 20
  val defaultSearchAppsLimit = 20

  override def getRecommendedAppsByCategory(
    category: NineCardsCategory,
    excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getRecommendedApps(category.name, excludePackages, defaultRecommendedAppsLimit)(userConfig)
    } yield response.seq).resolveLeft(mapLeft)

  override def getRecommendedAppsByPackages(
    packages: Seq[String],
    excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getRecommendedAppsByPackages(packages, excludePackages, defaultRecommendedAppsLimit)(userConfig)
    } yield response.seq).resolveLeft(mapLeft)

  override def searchApps(
    query: String,
    excludePackages: Seq[String])(implicit context: ContextSupport): TaskService[Seq[NotCategorizedPackage]] =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.searchApps(query, excludePackages, defaultSearchAppsLimit)(userConfig)
    } yield response.seq).resolveLeft(mapLeft)

  private[this] def mapLeft[T]: (NineCardException) => Either[NineCardException, T] = {
    case e: ApiServiceConfigurationException => Left(RecommendedAppsConfigurationException(e.message, Some(e)))
    case e => Left(RecommendedAppsException(e.message, Some(e)))
  }

}
