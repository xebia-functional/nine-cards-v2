package com.fortysevendeg.ninecardslauncher.services.image.impl

import com.fortysevendeg.ninecardslauncher.services.image._

trait ImageServicesImplData {

  val appPackage = AppPackage(
    "com.fortysevendeg.ninecardslauncher.test",
    "ClassNameExample",
    "Sample Name",
    0)

  val appWebsite = AppWebsite(
    "com.fortysevendeg.ninecardslauncher.test",
    "http://www.example.com/image.jpg",
    "Sample Name")

  val fileFolder = "/file/example"

  val fileName = String.format("%s_%s", appPackage.packageName.toLowerCase.replace(".", "_"), appPackage.className.toLowerCase.replace(".", "_"))

  val filePath = s"$fileFolder/$fileName"

  val packageName = appPackage.packageName

  val className = appPackage.className

  val icon = appPackage.icon

  val uri = appWebsite.url

  val appPackagePath = AppPackagePath(
    packageName = appPackage.packageName,
    className = appPackage.className,
    path = filePath
  )

  val appWebsitePath = AppWebsitePath(
    packageName = appWebsite.packageName,
    url = appWebsite.url,
    path = filePath
  )

  val imageServiceConfig = ImageServicesConfig(List(1, 2, 3, 4, 5))

}
