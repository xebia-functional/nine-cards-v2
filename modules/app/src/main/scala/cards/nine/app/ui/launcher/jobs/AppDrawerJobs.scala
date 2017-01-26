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

package cards.nine.app.ui.launcher.jobs

import cards.nine.app.ui.commons.{Jobs, RequestCodes}
import cards.nine.app.ui.launcher.jobs.uiactions.AppDrawerUiActions
import cards.nine.app.ui.launcher.types._
import cards.nine.commons.services.TaskService._
import cards.nine.models
import cards.nine.models._
import cards.nine.models.types._
import macroid.ActivityContextWrapper

class AppDrawerJobs(val mainAppDrawerUiActions: AppDrawerUiActions)(
    implicit activityContextWrapper: ActivityContextWrapper)
    extends Jobs { self =>

  def loadSearch(query: String): TaskService[Unit] = {
    for {
      _      <- di.trackEventProcess.goToGooglePlayButton()
      _      <- mainAppDrawerUiActions.showLoadingInGooglePlay()
      result <- di.recommendationsProcess.searchApps(query)
      _      <- mainAppDrawerUiActions.reloadSearchInDrawer(result)
    } yield ()
  }

  def loadApps(appsMenuOption: AppsMenuOption): TaskService[Unit] = {
    val getAppOrder = toGetAppOrder(appsMenuOption)
    for {
      _      <- di.trackEventProcess.goToAppDrawer()
      _      <- di.trackEventProcess.goToApps()
      result <- getLoadApps(getAppOrder)
      (apps, counters) = result
      _ <- mainAppDrawerUiActions
        .reloadAppsInDrawer(apps = apps, getAppOrder = getAppOrder, counters = counters)
    } yield ()
  }

  def loadContacts(contactsMenuOption: ContactsMenuOption): TaskService[Unit] = {

    def getLoadContacts(
        order: ContactsFilter): TaskService[(models.IterableContacts, Seq[TermCounter])] =
      for {
        iterableContacts <- di.deviceProcess.getIterableContacts(order)
        counters         <- di.deviceProcess.getTermCountersForContacts(order)
      } yield (iterableContacts, counters)

    contactsMenuOption match {
      case ContactsByLastCall =>
        for {
          _        <- di.trackEventProcess.goToContacts()
          contacts <- di.deviceProcess.getLastCalls
          _        <- mainAppDrawerUiActions.reloadLastCallContactsInDrawer(contacts)
        } yield ()
      case _ =>
        val getContactFilter = toGetContactFilter(contactsMenuOption)
        for {
          _      <- di.trackEventProcess.goToContacts()
          result <- getLoadContacts(getContactFilter)
          (contacts, counters) = result
          _ <- mainAppDrawerUiActions.reloadContactsInDrawer(contacts, counters)
        } yield ()
    }
  }

  def loadAppsByKeyword(keyword: String): TaskService[Unit] =
    for {
      apps <- di.deviceProcess.getIterableAppsByKeyWord(keyword, GetByName)
      _    <- mainAppDrawerUiActions.reloadAppsInDrawer(apps)
    } yield ()

  def loadContactsByKeyword(keyword: String): TaskService[Unit] =
    for {
      contacts <- di.deviceProcess.getIterableContactsByKeyWord(keyword)
      _        <- mainAppDrawerUiActions.reloadContactsInDrawer(contacts)
    } yield ()

  def requestReadContacts(): TaskService[Unit] =
    di.userAccountsProcess.requestPermission(RequestCodes.contactsPermission, ReadContacts)

  def requestReadCallLog(): TaskService[Unit] =
    di.userAccountsProcess.requestPermission(RequestCodes.callLogPermission, ReadCallLog)

  private[this] def getLoadApps(
      order: GetAppOrder): TaskService[(IterableApplicationData, Seq[TermCounter])] =
    for {
      _            <- di.trackEventProcess.goToFiltersByButton(order.name)
      iterableApps <- di.deviceProcess.getIterableApps(order)
      counters     <- di.deviceProcess.getTermCountersForApps(order)
    } yield (iterableApps, counters)

  private[this] def toGetAppOrder(appsMenuOption: AppsMenuOption): GetAppOrder =
    appsMenuOption match {
      case AppsAlphabetical  => GetByName
      case AppsByCategories  => GetByCategory
      case AppsByLastInstall => GetByInstallDate
    }

  private[this] def toGetContactFilter(contactMenuOption: ContactsMenuOption): ContactsFilter =
    contactMenuOption match {
      case ContactsFavorites => FavoriteContacts
      case _                 => AllContacts
    }

}
