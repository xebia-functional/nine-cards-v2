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

import java.io.InputStream

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.services.drive.models.{DriveServiceFile, DriveServiceFileSummary}
import com.google.android.gms.common.api.GoogleApiClient

trait DriveServices {

  /**
   * Creates the Drive API client
   * @param account the email for the client
   * @return the GoogleAPIClient
   */
  def createDriveClient(account: String)(
      implicit contextSupport: ContextSupport): TaskService[GoogleApiClient]

  /**
   * Return a sequence of files in the user app space filtered by fileType key
   * @param client the google API client
   * @param maybeFileType any String or `None` to list all files
   * @return Sequence of `DriveServiceFile`
   * @throws DriveServicesException if there was an error with the request GoogleDrive api
   */
  def listFiles(
      client: GoogleApiClient,
      maybeFileType: Option[String]): TaskService[Seq[DriveServiceFileSummary]]

  /**
   * Verify if a specific file exists
   * @param client the google API client
   * @param driveId file identifier
   * @return the device name if exists, None if not exists
   * @throws DriveServicesException if there was an error with the request GoogleDrive api
   */
  def fileExists(client: GoogleApiClient, driveId: String): TaskService[Option[String]]

  /**
   * Returns the content of a file
   * @param client the google API client
   * @param driveId that identifies the file to be read
   * @return the file content as String
   * @throws DriveServicesException if there was an error with the request GoogleDrive api
   */
  def readFile(client: GoogleApiClient, driveId: String): TaskService[DriveServiceFile]

  /**
   * Creates a new text file
   * @param client the google API client
   * @param title the file title
   * @param content the content as String
   * @param deviceId custom device identifier
   * @param fileType a String that later can be used in #listFiles method
   * @param mimeType the file
   * @return the file identifier
   * @throws DriveServicesException if there was an error with the request GoogleDrive api
   */
  def createFile(
      client: GoogleApiClient,
      title: String,
      content: String,
      deviceId: String,
      fileType: String,
      mimeType: String): TaskService[DriveServiceFileSummary]

  /**
   * Creates a new file
   * @param client the google API client
   * @param title the file title
   * @param content the content as InputStream (won't be closed after finish)
   * @param deviceId custom device identifier
   * @param fileType a String that later can be used in #listFiles method
   * @param mimeType the file mimeType
   * @throws DriveServicesException if there was an error with the request GoogleDrive api
   */
  def createFile(
      client: GoogleApiClient,
      title: String,
      content: InputStream,
      deviceId: String,
      fileType: String,
      mimeType: String): TaskService[DriveServiceFileSummary]

  /**
   * Updates the content of an existing text file
   * @param client the google API client
   * @param driveId that identifies the file to be updated
   * @param title the new title
   * @param content the content as String
   * @throws DriveServicesException if there was an error with the request GoogleDrive api
   */
  def updateFile(
      client: GoogleApiClient,
      driveId: String,
      title: String,
      content: String): TaskService[DriveServiceFileSummary]

  /**
   * Updates the content of an existing file
   * @param client the google API client
   * @param driveId that identifies the file to be updated
   * @param title the new title
   * @param content the content as InputStream (won't be closed after finish)
   * @throws DriveServicesException if there was an error with the request GoogleDrive api
   */
  def updateFile(
      client: GoogleApiClient,
      driveId: String,
      title: String,
      content: InputStream): TaskService[DriveServiceFileSummary]

  /**
   * Try to delete the file
   * @param client the google API client
   * @param driveId that identifies the file to be updated
   * @return Unit
   * @throws DriveServicesException if there was an error or there is no file with this identifier
   */
  def deleteFile(client: GoogleApiClient, driveId: String): TaskService[Unit]

}
