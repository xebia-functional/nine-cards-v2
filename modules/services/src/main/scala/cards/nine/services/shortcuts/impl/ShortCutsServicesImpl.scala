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

package cards.nine.services.shortcuts.impl

import android.content.{ComponentName, Intent}
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.models.Shortcut
import cards.nine.services.shortcuts.{
  ImplicitsShortcutsExceptions,
  ShortcutServicesException,
  ShortcutsServices
}

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

class ShortcutsServicesImpl extends ShortcutsServices with ImplicitsShortcutsExceptions {

  override def getShortcuts(implicit context: ContextSupport) =
    TaskService {
      CatchAll[ShortcutServicesException] {
        val packageManager = context.getPackageManager

        val shortcuts =
          packageManager.queryIntentActivities(new Intent(Intent.ACTION_CREATE_SHORTCUT), 0).toSeq

        shortcuts map { resolveInfo =>
          val activityInfo = resolveInfo.activityInfo
          val componentName =
            new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name)
          val drawable = Try(context.getPackageManager.getActivityIcon(componentName)) match {
            case Success(result) => Option(result)
            case Failure(e)      => None
          }
          val intent = new Intent(Intent.ACTION_CREATE_SHORTCUT)
          intent.addCategory(Intent.CATEGORY_DEFAULT)
          intent.setComponent(componentName)
          Shortcut(
            title = resolveInfo.loadLabel(packageManager).toString,
            icon = drawable,
            intent = intent)
        } sortBy (_.title)
      }
    }

}
