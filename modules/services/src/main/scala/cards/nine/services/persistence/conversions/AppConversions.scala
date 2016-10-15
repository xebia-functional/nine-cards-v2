package cards.nine.services.persistence.conversions

import cards.nine.models.types.NineCardsCategory
import cards.nine.models.{Application, ApplicationData}
import cards.nine.repository.model.{App => RepositoryApp, AppData => RepositoryAppData}

trait AppConversions {

  def toApp(app: RepositoryApp): Application =
    Application(
      id = app.id,
      name = app.data.name,
      packageName = app.data.packageName,
      className = app.data.className,
      category = NineCardsCategory(app.data.category),
      dateInstalled = app.data.dateInstalled,
      dateUpdated = app.data.dateUpdate,
      version = app.data.version,
      installedFromGooglePlay = app.data.installedFromGooglePlay)

  def toRepositoryApp(app: Application): RepositoryApp =
    RepositoryApp(
      id = app.id,
      data = RepositoryAppData(
        name = app.name,
        packageName = app.packageName,
        className = app.className,
        category = app.category.name,
        dateInstalled = app.dateInstalled,
        dateUpdate = app.dateUpdated,
        version = app.version,
        installedFromGooglePlay = app.installedFromGooglePlay))

  def toRepositoryAppData(app: ApplicationData): RepositoryAppData =
    RepositoryAppData(
      name = app.name,
      packageName = app.packageName,
      className = app.className,
      category = app.category.name,
      dateInstalled = app.dateInstalled,
      dateUpdate = app.dateUpdated,
      version = app.version,
      installedFromGooglePlay = app.installedFromGooglePlay)
}
