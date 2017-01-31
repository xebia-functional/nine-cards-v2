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

package cards.nine.commons.contentresolver

import android.database.Cursor

import scala.annotation.tailrec

object Conversions {

  def getEntityFromCursor[T](conversionFunction: Cursor => T)(cursor: Cursor): Option[T] = {
    val entity = cursor.moveToFirst() match {
      case true => Some(conversionFunction(cursor))
      case _    => None
    }

    cursor.close()
    entity
  }

  def getListFromCursor[T](conversionFunction: Cursor => T)(cursor: Cursor): Seq[T] = {
    @tailrec
    def getListFromEntityLoop(cursor: Cursor, result: Seq[T]): Seq[T] =
      cursor match {
        case validCursor if validCursor.isAfterLast => result
        case _ =>
          val entity = conversionFunction(cursor)
          cursor.moveToNext
          getListFromEntityLoop(cursor, result :+ entity)
      }

    val list = cursor.moveToFirst() match {
      case true => getListFromEntityLoop(cursor, Seq.empty[T])
      case _    => Seq.empty[T]
    }

    cursor.close()
    list
  }
}
