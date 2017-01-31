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

package cards.nine.app.ui.profile.models

import java.util.Date

import cards.nine.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.R
import org.ocpsoft.prettytime.PrettyTime

sealed trait AccountSyncType

case object Header extends AccountSyncType

case class Device(current: Boolean) extends AccountSyncType

case class AccountSync(
    title: String,
    accountSyncType: AccountSyncType,
    cloudId: Option[String] = None,
    subtitle: Option[String] = None)

object AccountSync {

  def header(title: String): AccountSync =
    AccountSync(title = title, accountSyncType = Header)

  def syncDevice(title: String, syncDate: Date, current: Boolean = false, cloudId: String)(
      implicit context: ContextSupport): AccountSync = {
    val time = new PrettyTime().format(syncDate)
    AccountSync(
      title = title,
      accountSyncType = Device(current),
      cloudId = Option(cloudId),
      subtitle = Option(context.getResources.getString(R.string.syncLastSynced, time)))
  }

}

sealed trait ProfileTab

case object PublicationsTab extends ProfileTab

case object SubscriptionsTab extends ProfileTab

case object AccountsTab extends ProfileTab
