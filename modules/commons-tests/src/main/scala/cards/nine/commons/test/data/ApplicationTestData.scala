package cards.nine.commons.test.data

import cards.nine.models.{ApplicationData, Application}
import cards.nine.models.types.NineCardsCategory
import cards.nine.commons.test.data.ApplicationValues._

trait ApplicationTestData {

  def createSeqApp(
    num: Int = 5,
    id: Int = appId,
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    category: String = category,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): Seq[Application] = List.tabulate(num)(
    item => Application(
      id = id + item,
      name = name,
      packageName = packageName,
      className = className,
      category = NineCardsCategory(category),
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay))

  def createApplicationData(
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    category: String = category,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): ApplicationData =
    ApplicationData(
      name = name,
      packageName = packageName,
      className = className,
      category = NineCardsCategory(category),
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay)

  def createApplication(
    id: Int = appId,
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    category: String = category,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): Application =
    Application(
      id = id,
      name = name,
      packageName = packageName,
      className = className,
      category = NineCardsCategory(category),
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay)

  val seqApp: Seq[Application] = createSeqApp()
  val app: Application = seqApp(0)

}
