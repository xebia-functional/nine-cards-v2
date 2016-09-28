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
