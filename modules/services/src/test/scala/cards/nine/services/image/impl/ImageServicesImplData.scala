/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.services.image.impl

import cards.nine.models._

trait ImageServicesImplData {

  val fileFolder = "/file/example"

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

  val bitmapPath = BitmapPath(name = "", path = resultFileSaveBitmap)

}
