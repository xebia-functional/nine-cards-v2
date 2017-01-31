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

package cards.nine.services.drive

import cards.nine.services.drive.impl.DriveServicesImpl._
import cards.nine.services.drive.models.DriveServiceFileSummary
import com.google.android.gms.drive.Metadata

trait Conversions {

  def toGoogleDriveFileSummary(metadata: Metadata): DriveServiceFileSummary =
    DriveServiceFileSummary(
      uuid = metadata.getCustomProperties.get(propertyUUID),
      deviceId = Option(metadata.getCustomProperties.get(propertyDeviceId)),
      title = metadata.getTitle,
      createdDate = metadata.getCreatedDate,
      modifiedDate = metadata.getModifiedDate)

  def toGoogleDriveFileSummary(uuid: String, metadata: Metadata): DriveServiceFileSummary =
    DriveServiceFileSummary(
      uuid = uuid,
      deviceId = Option(metadata.getCustomProperties.get(propertyDeviceId)),
      title = metadata.getTitle,
      createdDate = metadata.getCreatedDate,
      modifiedDate = metadata.getModifiedDate)

}
