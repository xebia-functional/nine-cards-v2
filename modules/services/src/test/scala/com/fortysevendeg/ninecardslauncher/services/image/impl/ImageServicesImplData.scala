package cards.nine.services.image.impl

import java.io.{FileInputStream, File}

import cards.nine.models._

trait ImageServicesImplData {

  val fileFolder = "target"

  val packageName = "com.fortysevendeg.ninecardslauncher.test"

  val className = "ClassNameExample"

  val resultFileName = "C"

  val resultFilePath = s"$fileFolder/C"

  val resultFilePathPackage = s"$fileFolder/$packageName"

  val uri = "http://www.example.com/image.jpg"

  val name = "Sample Name"

  val textToMeasure = "M"

  val textSize = 71

  val colorsList = List(1, 2, 3)

  val densityDpi = 240

  val widthPixels = 240

  val heightPixels = 320

  val bitmapName = "aeiuo-12345"

  val resultFileSaveBitmap = s"$fileFolder/$bitmapName"

  val bitmapPath = BitmapPath(
    name = "",
    path = resultFileSaveBitmap)

  val existingFile = new File(resultFilePath)
  existingFile.createNewFile()

  val fileInputStream = new FileInputStream(existingFile)

}
