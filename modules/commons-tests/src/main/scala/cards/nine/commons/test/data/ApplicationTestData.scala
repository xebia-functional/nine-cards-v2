package cards.nine.commons.test.data

import cards.nine.commons.test.data.ApplicationValues._
import cards.nine.models.{Application, ApplicationData}

trait ApplicationTestData {

  def application(num: Int = 0) = Application(
    id = applicationId + num,
    name = applicationName + num,
    packageName = applicationPackageName + num,
    className = applicationClassName + num,
    category = applicationCategory,
    dateInstalled = dateInstalled,
    dateUpdated = dateUpdated,
    version = version,
    installedFromGooglePlay = installedFromGooglePlay)

  val application: Application = application(0)
  val seqApplication: Seq[Application] = Seq(application(0), application(1), application(2))

  val applicationData: ApplicationData = application.toData
  val seqApplicationData: Seq[ApplicationData] = seqApplication map (_.toData)

}
