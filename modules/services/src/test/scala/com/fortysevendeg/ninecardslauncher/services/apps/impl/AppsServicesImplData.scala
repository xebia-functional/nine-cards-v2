package com.fortysevendeg.ninecardslauncher.services.apps.impl

import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

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

  val invalidPackageName = "com.fortysevendeg.ninecardslauncher.test.sampleapp3"

  def createSeqApplication(
    num: Int = 2,
    name: String = name,
    packageName: String = packageName,
    className: String = className,
    resourceIcon: Int = resourceIcon,
    dateInstalled: Long = dateInstalled,
    dateUpdate: Long = dateUpdate,
    version: String = version,
    installedFromGooglePlay: Boolean = installedFromGooglePlay): Seq[Application] = List.tabulate(num)(
    item => Application(
      name = name,
      packageName = packageName,
      className = className,
      resourceIcon = resourceIcon,
      dateInstalled = dateInstalled,
      dateUpdate = dateUpdate,
      version = version,
      installedFromGooglePlay = installedFromGooglePlay))

}
