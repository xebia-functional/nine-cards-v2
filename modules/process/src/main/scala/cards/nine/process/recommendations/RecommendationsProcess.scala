package cards.nine.process.recommendations

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.NotCategorizedPackage
import cards.nine.models.types.NineCardsCategory

trait RecommendationsProcess {

  /**
    * Get recommended apps based on a category
    *
    * @param category a valid category identification
    * @return the Seq[NotCategorizedPackage]
    * @throws RecommendedAppsConfigurationException if there was an error with the API configuration
    * @throws RecommendedAppsException if there was an error fetching the recommended apps
    */
  def getRecommendedAppsByCategory(
    category: NineCardsCategory,
    excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport): TaskService[Seq[NotCategorizedPackage]]

  /**
    * Get recommended apps based on a category
    *
    * @param packages a valid list of packages
    * @return the Seq[NotCategorizedPackage]
    * @throws RecommendedAppsConfigurationException if there was an error with the API configuration
    * @throws RecommendedAppsException if there was an error fetching the recommended apps
    */
  def getRecommendedAppsByPackages(
    packages: Seq[String],
    excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport): TaskService[Seq[NotCategorizedPackage]]

  /**
    * Search apps based on a query string
    *
    * @param query the query string
    * @return the Seq[NotCategorizedPackage]
    * @throws RecommendedAppsConfigurationException if there was an error with the API configuration
    * @throws RecommendedAppsException if there was an error fetching the recommended apps
    */
  def searchApps(
    query: String,
    excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport): TaskService[Seq[NotCategorizedPackage]]
}
