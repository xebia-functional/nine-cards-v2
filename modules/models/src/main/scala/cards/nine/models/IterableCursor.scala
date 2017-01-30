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

package cards.nine.models

import android.database.Cursor

import scala.annotation.tailrec

trait IterableCursor[T] {
  def count(): Int

  def moveToPosition(pos: Int): T

  def close(): Unit
}

object IterableCursor {

  implicit class RichCursor(c: Cursor) {
    private def implicitIterator[T](f: => T) = new IterableCursor[T] {
      override def count(): Int = if (c.isClosed) 0 else c.getCount
      override def moveToPosition(pos: Int): T = {
        c.moveToPosition(pos)
        f
      }
      override def close(): Unit = if (!c.isClosed) c.close()
    }

    private def implicitIter[T](f: => T) = implicitIterator[T](f)

    def toIterator[T](conversionFunction: Cursor => T) = implicitIter {
      conversionFunction(c)
    }

  }

  @tailrec
  def toSeq[T](iterator: IterableCursor[T], pos: Int = 0, seq: Seq[T] = Seq.empty): Seq[T] =
    if (pos >= iterator.count()) {
      seq
    } else {
      toSeq(iterator, pos + 1, seq :+ iterator.moveToPosition(pos))
    }
}
