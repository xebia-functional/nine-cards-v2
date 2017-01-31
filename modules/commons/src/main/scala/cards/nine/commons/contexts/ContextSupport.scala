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

package cards.nine.commons.contexts

import java.io.File

import android.accounts.AccountManager
import android.app.{Activity, AlarmManager, Application}
import android.content.{ContentResolver, Context, Intent, SharedPreferences}
import android.content.pm.PackageManager
import android.content.res.{AssetManager, Resources}

import scala.ref.WeakReference

trait ContextSupport {
  def application: Application
  def context: Context
  def getOriginal: WeakReference[Context]
  def getPackageManager: PackageManager
  def getResources: Resources
  def getContentResolver: ContentResolver
  def getFilesDir: File
  def getAppIconsDir: File
  def getAssets: AssetManager
  def getPackageName: String
  def getSharedPreferences: SharedPreferences
  def getActiveUserId: Option[Int]
  def setActiveUserId(id: Int): Unit
  def addBluetoothDevice(device: String): Unit
  def removeBluetoothDevice(device: String): Unit
  def clearBluetoothDevices(): Unit
  def getBluetoothDevicesConnected: Set[String]
  def getAccountManager: AccountManager
  def createIntent(classOf: Class[_]): Intent
  def getAlarmManager: Option[AlarmManager]
}

trait ActivityContextSupport extends ContextSupport {
  def getActivity: Option[Activity]
}
