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

package cards.nine.process.accounts.impl

trait UserAccountsProcessImplData {

  val accountName1 = "name1"

  val accountName2 = "name2"

  val accountType = "com.google"

  val androidAccount1 = new android.accounts.Account(accountName1, accountType)

  val androidAccount2 = new android.accounts.Account(accountName2, accountType)

  val scope = "fake-scope"

  val authToken = "fake-auth-token"

  val permissionCode = 100

}
