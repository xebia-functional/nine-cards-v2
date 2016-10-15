package cards.nine.commons.test.data

import cards.nine.commons.test.data.ApplicationValues._
import cards.nine.models.{Application, ApplicationData}

trait ApplicationTestData {

  def application(num: Int = 0) = Application(
    id = applicationId + item,
    name = applicationName,
    packageName = applicationPackageName,
    className = applicationClassName,
    category = applicationCategory,
    dateInstalled = dateInstalled,
    dateUpdated = dateUpdated,
    version = version,
    installedFromGooglePlay = installedFromGooglePlay)

  val application: Application = application(0)
  val seqApplication: Seq[Application] = Seq(application(0), application(1), application(2))

  def applicationData(num: Int = 0) = ApplicationData(
    name = applicationName,
    packageName = applicationPackageName,
    className = applicationClassName,
    category = applicationCategory,
    dateInstalled = dateInstalled,
    dateUpdated = dateUpdated,
    version = version,
    installedFromGooglePlay = installedFromGooglePlay)

  val applicationData: ApplicationData = applicationData(0)
  val seqApplicationData: Seq[ApplicationData] = Seq(applicationData(0), applicationData(1), applicationData(2))

}
