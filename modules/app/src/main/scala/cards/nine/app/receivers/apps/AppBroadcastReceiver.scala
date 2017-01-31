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

package cards.nine.app.receivers.apps

import android.content.Intent._
import android.content.{BroadcastReceiver, Context, Intent}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import macroid.ContextWrapper

class AppBroadcastReceiver extends BroadcastReceiver {

  override def onReceive(context: Context, intent: Intent): Unit = {
    val packageName = getPackageName(intent)
    // We don't want to change 9Cards App
    if (!context.getPackageName.equals(packageName)) {
      val action    = intent.getAction
      val replacing = intent.getBooleanExtra(EXTRA_REPLACING, false)

      implicit val contextWrapper = ContextWrapper(context)

      val jobs = new AppBroadcastJobs

      (action, replacing) match {
        case (ACTION_PACKAGE_ADDED, false) =>
          jobs.addApp(packageName).resolveAsync()
        case (ACTION_PACKAGE_REMOVED, false) =>
          jobs.deleteApp(packageName).resolveAsync()
        case (ACTION_PACKAGE_CHANGED | ACTION_PACKAGE_REPLACED, _) =>
          jobs.updateApp(packageName).resolveAsync()
        case (_, _) =>
      }
    }
  }

  private[this] def getPackageName(intent: Intent): String =
    intent.getData.toString.replace("package:", "")
}
