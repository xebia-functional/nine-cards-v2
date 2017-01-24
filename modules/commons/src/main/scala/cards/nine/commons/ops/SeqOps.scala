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

package cards.nine.commons.ops

object SeqOps {

  implicit class SeqCursor[T](seq: Seq[T]) {
    def reorder(from: Int, to: Int) = {
      val range1 = math.min(from, to)
      val range2 = math.max(from, to)

      val header       = seq.take(range1)
      val tail         = seq.drop(range2 + 1)
      val updatedRange = reorderRange(from, to)

      header ++ updatedRange ++ tail
    }

    def reorderRange(from: Int, to: Int) = {
      val range1 = math.min(from, to)
      val range2 = math.max(from, to)

      val range = seq.slice(range1, range2 + 1)
      val updatedRange = if (from < to) {
        range.drop(1) ++ range.take(1)
      } else {
        range.takeRight(1) ++ range.dropRight(1)
      }
      updatedRange
    }
  }

}
