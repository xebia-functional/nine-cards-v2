package com.fortysevendeg.ninecardslauncher.services.apps.impl

import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

trait AppsServicesImplData {

  val androidFeedback = "com.google.android.feedback"

  val sampleApp1 =  Application(
    name = "Sample Name 1",
    packageName = "com.fortysevendeg.ninecardslauncher.test.sampleapp1",
    className = "ClassNameExample1",
    resourceIcon = 0,
    colorPrimary = "",
    dateInstalled = 1L,
    dateUpdate = 1L,
    version = "22",
    installedFromGooglePlay = true)

  val sampleApp2 =  Application(
    name = "Sample Name 2",
    packageName = "com.fortysevendeg.ninecardslauncher.test.sampleapp2",
    className = "ClassNameExample2",
    resourceIcon = 0,
    colorPrimary = "",
    dateInstalled = 1L,
    dateUpdate = 1L,
    version = "22",
    installedFromGooglePlay = true)

  val applicationList = Seq(sampleApp1, sampleApp2)

  val validPackageName = sampleApp1.packageName

  val invalidPackageName = "com.fortysevendeg.ninecardslauncher.test.sampleapp3"

}
