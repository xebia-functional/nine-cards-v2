package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevices, UserPermissions}
import macroid.Ui

trait WizardActions {

  def onResultLoadUser(account: Account): Ui[Any]

  def onExceptionLoadUser(): Ui[Any]

  def onResultLoadAccount(userPermissions: UserPermissions): Ui[Any]

  def onExceptionLoadAccount(exception: Throwable): Ui[Any]

  def onResultLoadDevices(devices: UserCloudDevices): Ui[Any]

  def onExceptionLoadDevices(exception: Throwable): Ui[Any]

  def onResultStoreCurrentDevice(unit: Unit): Ui[Any]

  def onExceptionStoreCurrentDevice(exception: Throwable): Ui[Any]

  def onResultWizard(): Ui[Any]

}
