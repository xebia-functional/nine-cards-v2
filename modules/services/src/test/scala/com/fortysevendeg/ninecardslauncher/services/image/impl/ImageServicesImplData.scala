package cards.nine.services.image.impl

import cards.nine.services.image._

trait ImageServicesImplData {

  val appPackage = AppPackage(
    "com.fortysevendeg.ninecardslauncher.test",
    "ClassNameExample",
    "Sample Name")

  val appWebsite = AppWebsite(
    "com.fortysevendeg.ninecardslauncher.test",
    "http://www.example.com/image.jpg",
    "Sample Name")

  val fileFolder = "/file/example"

  val fileName = String.format("%s_%s", appPackage.packageName.toLowerCase.replace(".", "_"), appPackage.className.toLowerCase.replace(".", "_"))

  val filePath = s"$fileFolder/$fileName"

  val packageName = appPackage.packageName

  val className = appPackage.className

  val resultFileName = "C"

  val resultFilePath = s"$fileFolder/C"

  val resultFilePathPackage = s"$fileFolder/$packageName"

  val uri = appWebsite.url

  val name = appWebsite.name

  val textToMeasure = "M"

  val textSize = 71

  val colorsList = List(1, 2, 3)

  val densityDpi = 240

  val widthPixels = 240

  val heightPixels = 320

  val appPackagePath = AppPackagePath(
    packageName = appPackage.packageName,
    className = appPackage.className,
    path = filePath)

  val appWebsitePath = AppWebsitePath(
    packageName = appWebsite.packageName,
    url = appWebsite.url,
    path = filePath)

  val bitmapName = "aeiuo-12345"

  val resultFileSaveBitmap = s"$fileFolder/$bitmapName"

  val saveBitmapPath = SaveBitmapPath(
    name = "",
    path = resultFileSaveBitmap)

  val imageServiceConfig = ImageServicesConfig(List(1, 2, 3, 4, 5))

}
