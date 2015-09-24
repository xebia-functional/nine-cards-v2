package com.fortysevendeg.ninecardslauncher.services.apps.impl

import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

trait AppsServicesImplData {

  val sampleApp1 =  Application(
    "Sample Name 1",
    "com.fortysevendeg.ninecardslauncher.test.sampleapp1",
    "ClassNameExample1",
    0,
    "10",
    1d,
    1d,
    "22",
    installedFromGooglePlay = true)

  val sampleApp2 =  Application(
    "Sample Name 2",
    "com.fortysevendeg.ninecardslauncher.test.sampleapp2",
    "ClassNameExample2",
    0,
    "10",
    1d,
    1d,
    "22",
    installedFromGooglePlay = true)

  val applicationList = Seq(sampleApp1, sampleApp2)

}
