package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevices, UserPermissions}
import macroid.Ui

trait WizardActions {

  def onResultLoadUser(account: Account): Ui[_]

  def onExceptionLoadUser(): Ui[_]

  def onResultLoadAccount(userPermissions: UserPermissions): Ui[_]

  def onExceptionLoadAccount(exception: Throwable): Ui[_]

  def onResultLoadDevices(devices: UserCloudDevices): Ui[_]

  def onExceptionLoadDevices(exception: Throwable): Ui[_]

  def onResultStoreCurrentDevice(unit: Unit): Ui[_]

  def onExceptionStoreCurrentDevice(exception: Throwable): Ui[_]

  def onResultWizard(): Ui[_]

}
