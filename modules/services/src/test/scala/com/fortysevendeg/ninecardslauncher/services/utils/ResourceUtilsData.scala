package com.fortysevendeg.ninecardslauncher.services.utils

import com.fortysevendeg.ninecardslauncher.services.image._

trait ResourceUtilsData {

  val appPackage = AppPackage(
    "com.fortysevendeg.ninecardslauncher.test",
    "ClassNameExample",
    "Sample Name",
    0)

  val fileFolder = "/file/example"

  val fileName = String.format("%s_%s", appPackage.packageName.toLowerCase.replace(".", "_"), appPackage.className.toLowerCase.replace(".", "_"))

  val packageName = appPackage.packageName

  val className = appPackage.className

  val resultFilePath = s"$fileFolder/$fileName"

}
