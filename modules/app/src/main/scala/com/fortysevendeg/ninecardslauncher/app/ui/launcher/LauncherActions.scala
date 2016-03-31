package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.view.View
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.device.{GetAppOrder, GetByName}
import com.fortysevendeg.ninecardslauncher.process.user.models.User
import macroid.Ui

trait LauncherActions {

  def addCollection(collection: Collection): Ui[Any]

  def showDialogForRemoveCollection(collection: Collection): Ui[Any]

  def removeCollection(collection: Collection): Ui[Any]

  def showContactUsError(): Ui[Any]

  def showMinimumOneCollectionMessage(): Ui[Any]

  def showLoading(): Ui[Any]

  def loadCollections(collections: Seq[Collection], apps: Seq[DockApp]): Ui[Any]

  def loadUserProfile(user: User): Ui[Any]

  def goToWizard(): Ui[Any]

  def goToCollection(view: View, collection: Collection): Ui[Any]

  def canRemoveCollections(): Ui[Boolean]

  def reloadAppsInDrawer(
    apps: IterableApps,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any]

  def reloadContactsInDrawer(
    contacts: IterableContacts,
    counters: Seq[TermCounter] = Seq.empty): Ui[_]

  def reloadLastCallContactsInDrawer(contacts: Seq[LastCallsContact]): Ui[Any]

}
