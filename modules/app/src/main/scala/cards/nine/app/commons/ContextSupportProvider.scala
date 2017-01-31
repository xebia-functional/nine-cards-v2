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

package cards.nine.app.commons

import java.io.File

import android.accounts.AccountManager
import android.app.{Activity, AlarmManager, Application}
import android.content.pm.PackageManager
import android.content.res.{AssetManager, Resources}
import android.content.{ContentResolver, Context, Intent}
import cards.nine.commons.contexts.{ActivityContextSupport, ContextSupport}
import macroid.{ActivityContextWrapper, ContextWrapper}

import scala.ref.WeakReference

trait ContextSupportImpl extends ContextSupport {

  override def getContentResolver: ContentResolver = context.getContentResolver

  override def getPackageManager: PackageManager = context.getPackageManager

  override def getResources: Resources = context.getResources

  override def getFilesDir: File = context.getFilesDir

  override def getAssets: AssetManager = context.getAssets

  override def getPackageName: String = context.getPackageName

  override def getAccountManager: AccountManager = AccountManager.get(context)

  override def createIntent(classOf: Class[_]): Intent =
    new Intent(context, classOf)

  override def getAlarmManager: Option[AlarmManager] =
    context.getSystemService(Context.ALARM_SERVICE) match {
      case a: AlarmManager => Some(a)
      case _               => None
    }
}

trait ContextSupportProvider {

  implicit def contextSupport(implicit ctx: ContextWrapper): ContextSupport =
    new ContextSupportImpl with ContextSupportPreferences {

      override def application: Application =
        ctx.application.asInstanceOf[Application]

      override def context: Context = ctx.bestAvailable

      override def getOriginal: WeakReference[Context] = ctx.original
    }

  implicit def activityContextSupport(
      implicit ctx: ActivityContextWrapper): ActivityContextSupport =
    new ContextSupportImpl with ActivityContextSupport with ContextSupportPreferences {

      override def application: Application =
        ctx.application.asInstanceOf[Application]

      override def context: Context = ctx.bestAvailable

      override def getActivity: Option[Activity] = ctx.original.get

      override def getOriginal: WeakReference[Context] = ctx.original
    }

}
