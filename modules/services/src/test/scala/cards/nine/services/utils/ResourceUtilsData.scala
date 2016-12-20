package cards.nine.services.utils

trait ResourceUtilsData {

  val fileFolder = "/file/example"

  val packageName = "com.fortysevendeg.ninecardslauncher.test"

  val className = "ClassNameExample"

  val fileName = String.format(
    "%s_%s",
    packageName.toLowerCase.replace(".", "_"),
    className.toLowerCase.replace(".", "_"))

  val resultFilePath = s"$fileFolder/$fileName"

}
