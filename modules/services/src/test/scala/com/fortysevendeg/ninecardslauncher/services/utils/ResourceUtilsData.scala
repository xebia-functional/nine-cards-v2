package cards.nine.services.utils

import cards.nine.models.AppPackage

trait ResourceUtilsData {

  val appPackage = AppPackage(
    "com.fortysevendeg.ninecardslauncher.test",
    "ClassNameExample",
    "Sample Name")

  val fileFolder = "/file/example"

  val fileName = String.format("%s_%s", appPackage.packageName.toLowerCase.replace(".", "_"), appPackage.className.toLowerCase.replace(".", "_"))

  val resultFilePath = s"$fileFolder/$fileName"

}
