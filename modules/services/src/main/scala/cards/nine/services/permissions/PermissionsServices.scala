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

package cards.nine.services.permissions

import cards.nine.commons.contexts.{ActivityContextSupport, ContextSupport}
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.PermissionStatus

trait PermissionsServices {

  /**
   * Verifies the status of some permissions
   *
   * @param permissions the permissions
   * @return a map with the status of each permission
   * @throws PermissionsServicesException if there was some problem verifying the permission
   */
  def checkPermissions(permissions: Seq[String])(
      implicit contextSupport: ContextSupport): TaskService[Map[String, PermissionStatus]]

  /**
   * Check if we could show a request for the specified permissions
   *
   * @param permissions the permissions
   * @return a map with true or false indicating if we should show the request
   * @throws PermissionsServicesException if there was some problem verifying the permission
   */
  def shouldShowRequestPermissions(permissions: Seq[String])(
      implicit contextSupport: ActivityContextSupport): TaskService[Map[String, Boolean]]

  /**
   * Try to ask for permissions
   *
   * @param requestCode the request code
   * @param permissions the permissions
   * @throws PermissionsServicesException if there was some problem trying to request the permissions
   */
  def requestPermissions(requestCode: Int, permissions: Seq[String])(
      implicit contextSupport: ActivityContextSupport): TaskService[Unit]

  /**
   * Parses the permission request response
   *
   * @param permissions the permissions names
   * @param grantResults the status codes
   * @return a map with the status of each permission
   * @throws PermissionsServicesException if there was some problem parsing the permission statuses
   */
  def readPermissionsRequestResult(
      permissions: Seq[String],
      grantResults: Seq[Int]): TaskService[Map[String, PermissionStatus]]

}
