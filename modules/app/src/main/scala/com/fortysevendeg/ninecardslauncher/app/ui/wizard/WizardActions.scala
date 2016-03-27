package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevices, UserPermissions}
import com.google.android.gms.common.api.GoogleApiClient
import macroid.Ui

trait WizardActions {

  def showLoading(): Ui[Any]

  def showErrorConnectingGoogle(): Ui[Any]

  def showErrorLoginUser(): Ui[Any]

  def createGoogleApiClient(account: Account): Ui[GoogleApiClient]

  def connectGoogleApiClient(userPermissions: UserPermissions): Ui[Any]

  def showDevices(devices: UserCloudDevices): Ui[Any]

  def showDiveIn(): Ui[Any]

  def startCreateCollectionsService(maybeKey: Option[String]): Ui[Any]

  def navigateToLauncher(): Ui[Any]

  def navigateToWizard(): Ui[Any]

}
