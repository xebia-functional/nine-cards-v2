package cards.nine.services.apps.impl

import cards.nine.models.ApplicationData
import cards.nine.models.types.{Communication, Misc}

import scala.util.Random

trait AppsServicesImplData {

  val androidFeedback = "com.google.android.feedback"

  val name: String = Random.nextString(5)
  val packageName: String = Random.nextString(5)
  val className: String = Random.nextString(5)
  val resourceIcon: Int = Random.nextInt(10)
  val dateInstalled: Long = Random.nextLong()
  val dateUpdate: Long = Random.nextLong()
  val version: String = Random.nextInt(10).toString
  val installedFromGooglePlay: Boolean = true

  val applicationList = createSeqApplication()

  val defaultApplicationList = createSeqApplication(10)

  val sampleApp1 = applicationList(0)

  val sampleApp2 = applicationList(1)

  val validPackageName = sampleApp1.packageName

  val invalidPackageName = "cards.nine.test.sampleapp3"

  def createSeqApplication(
    num: Int = 2,
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    resourceIcon: Int = resourceIcon,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): Seq[ApplicationData] = List.tabulate(num)(
    item => ApplicationData(
      name = name,
      packageName = packageName,
      className = className,
      category = Misc,
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay))

}
