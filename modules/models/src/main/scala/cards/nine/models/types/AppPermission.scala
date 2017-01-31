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

package cards.nine.models.types

sealed trait AppPermission {
  val value: String
}

case object GetAccounts extends AppPermission {
  override val value: String = android.Manifest.permission.GET_ACCOUNTS
}

case object ReadContacts extends AppPermission {
  override val value: String = android.Manifest.permission.READ_CONTACTS
}

case object ReadCallLog extends AppPermission {
  override val value: String = android.Manifest.permission.READ_CALL_LOG
}

case object CallPhone extends AppPermission {
  override val value: String = android.Manifest.permission.CALL_PHONE
}

case object FineLocation extends AppPermission {
  override val value: String = android.Manifest.permission.ACCESS_FINE_LOCATION
}

object AppPermission {

  def values = Seq(GetAccounts, ReadContacts, ReadCallLog, CallPhone, FineLocation)

}

case class PermissionResult(permission: AppPermission, result: Boolean) {
  def hasPermission(p: AppPermission): Boolean = permission == p && result
}
