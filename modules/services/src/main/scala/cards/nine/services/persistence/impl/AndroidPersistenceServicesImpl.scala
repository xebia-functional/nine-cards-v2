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

package cards.nine.services.persistence.impl

import android.database.Cursor
import android.net.Uri
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.javaNull
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.services.persistence.{AndroidIdNotFoundException, PersistenceServices}
import monix.eval.Task

trait AndroidPersistenceServicesImpl extends PersistenceServices {

  val androidId = "android_id"

  val contentGServices = "content://com.google.android.gsf.gservices"

  def getAndroidId(implicit context: ContextSupport): TaskService[String] =
    TaskService {
      Task {
        val cursor: Option[Cursor] = Option(
          context.getContentResolver
            .query(Uri.parse(contentGServices), javaNull, javaNull, Array(androidId), javaNull))
        val result: Option[String] = cursor filter (c =>
                                                      c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(
            1).toHexString.toUpperCase)
        cursor foreach (_.close())
        result match {
          case Some(r) => Right(r)
          case _       => Left(AndroidIdNotFoundException("Android Id not found"))
        }
      }
    }
}
