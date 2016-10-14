package cards.nine.commons.utils

import java.io.File

trait FileUtilsData {

  val packageName = "cards.nine.test"

  val className = "ClassNameExample"

  val fileName = String.format("%s_%s", packageName.toLowerCase.replace(".", "_"), className.toLowerCase.replace(".", "_"))

  val fileFolder = "target"

  val resultFilePath = s"$fileFolder/$fileName"

  val fileJson = """{
                      "packageName": "cards.nine.test",
                      "className": "ClassNameExample",
                      "name": "Sample Name"
                     }"""

  val sourceString = "Source String"

  val existingFile = new File(resultFilePath)
  existingFile.createNewFile()
}
