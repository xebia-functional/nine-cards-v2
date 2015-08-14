package com.fortysevendeg.ninecardslauncher.process.utils

import com.fortysevendeg.ninecardslauncher.services.image._

trait UtilsData {

  val appPackage = AppPackage(
    "com.fortysevendeg.ninecardslauncher.test",
    "ClassNameExample",
    "Sample Name",
    0)

  val fileName = String.format("%s_%s", appPackage.packageName.toLowerCase.replace(".", "_"), appPackage.className.toLowerCase.replace(".", "_"))

  val fileJson = """{
                      "packageName": "com.fortysevendeg.ninecardslauncher.test",
                      "className": "ClassNameExample",
                      "name": "Sample Name"
                     }"""

  val androidId = "012354654894654654"

  val token = "Session token"

}
