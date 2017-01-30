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

package cards.nine.services.apps.impl

import cards.nine.models.ApplicationData
import cards.nine.models.types.{Communication, Misc}

import scala.util.Random

trait AppsServicesImplData {

  val androidFeedback = "com.google.android.feedback"

  val name: String                     = Random.nextString(5)
  val packageName: String              = Random.nextString(5)
  val className: String                = Random.nextString(5)
  val resourceIcon: Int                = Random.nextInt(10)
  val dateInstalled: Long              = Random.nextLong()
  val dateUpdate: Long                 = Random.nextLong()
  val version: String                  = Random.nextInt(10).toString
  val installedFromGooglePlay: Boolean = true

  val applicationList = createSeqApplication()

  val defaultApplicationList = createSeqApplication(10)

  val sampleApp1 = applicationList(0)

  val sampleApp2 = applicationList(1)

  val validPackageName = sampleApp1.packageName

  val invalidPackageName = "cards.nine.test.sampleapp3"

  def createSeqApplication(
      num: Int = 2,
      name: String = name,
      packageName: String = packageName,
      className: String = className,
      resourceIcon: Int = resourceIcon,
      dateInstalled: Long = dateInstalled,
      dateUpdate: Long = dateUpdate,
      version: String = version,
      installedFromGooglePlay: Boolean = installedFromGooglePlay): Seq[ApplicationData] =
    List.tabulate(num)(
      item =>
        ApplicationData(
          name = name,
          packageName = packageName,
          className = className,
          category = Misc,
          dateInstalled = dateInstalled,
          dateUpdated = dateUpdate,
          version = version,
          installedFromGooglePlay = installedFromGooglePlay))

}
