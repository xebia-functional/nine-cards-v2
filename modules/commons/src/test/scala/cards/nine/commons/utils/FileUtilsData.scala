package cards.nine.commons.utils

trait FileUtilsData {

  val packageName = "com.fortysevendeg.ninecardslauncher.test"

  val className = "ClassNameExample"

  val fileName = String.format("%s_%s", packageName.toLowerCase.replace(".", "_"), className.toLowerCase.replace(".", "_"))

  val fileFolder = "/file/example"

  val resultFilePath = s"$fileFolder/$fileName"

  val fileJson = """{
                      "packageName": "com.fortysevendeg.ninecardslauncher.test",
                      "className": "ClassNameExample",
                      "name": "Sample Name"
                     }"""

  val sourceString = "Source String"
}
