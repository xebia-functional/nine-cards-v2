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

package cards.nine.commons

import cats.syntax.either._
import monix.eval.Task

object CatchAll {

  def apply[E] = new CatchingAll[E]()

  class CatchingAll[E] {
    def apply[V](f: => V)(implicit converter: Throwable => E): Task[E Either V] =
      Task(Either.catchNonFatal(f) leftMap converter)
  }

}
