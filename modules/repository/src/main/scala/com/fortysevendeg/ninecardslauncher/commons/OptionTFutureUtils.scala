package com.fortysevendeg.ninecardslauncher.commons

import scalaz._
import scalaz.OptionT._
import scala.concurrent.{ExecutionContext, Future}

import com.fortysevendeg.ninecardslauncher.concurrent._

/**
 * Allows mixing expressions in for comprehensions without the need for nested flatmaps or nested for - yield.
 * Uses Scalaz OptionT to resolve Future[Option] expressions into their flattened values. Originally based on
 * https://github.com/47deg/scala-commons-47
 * e.g.
 *
 * import com.angieslist.common.util.OptionTFutureConversion._
 *
 * {{{
 *
 * def test = for {
 *  a <- ? <~ Future(Option(1))
 *  b <- optT <~ Some(1)
 *  c <- optT <~ Some(List(1, 2, 3))
 *  d <- optT <* List(1, 2, 3)
 * } yield (a, b, c, d)
 *
 * test.run
 *
 * }}}
 *
 * <* wraps any expression into an async Future(exp) then OptionT[Future, A] . Useful when using blocking APIs
 * <~ wraps any expression into its OptionT[Future, A]
 *
 *
 * ? is a an alias for optT
 *
 */
trait OptionTFutureConversionOps {

  type Result[A] = OptionT[Future, A]

  /** Future as a Monad **/
  implicit def futureMonad(implicit ec: ExecutionContext) = scalaz.std.scalaFuture.futureInstance

  implicit class RichOptionTFuture[A](f: Future[Option[A]]) {

    /** Resolves a Future[Option[A]] into a Future[A] throwing an exception if the Option is None
     *
     * @return Future[A]
     */
    def flatten(implicit ec: ExecutionContext): Future[A] =
      flattenOr(new RuntimeException("There was an error when trying to flatten a future of None"))

    /** Resolves a Future[Option[A]] into a Future[A] throwing an exception if the Option is None
     *
     * @param e the exception to be thrown if the Option was None
     * @return Future[A]
     */
    def flattenOr(e: => Exception)(implicit ec: ExecutionContext): Future[A] = f map (_ getOrElse (throw e))

    /** Resolves a Future[Option[A]] into a Future[A] returning an alternative if the Option is None
     *
     * Note: The DummyImplicit exists only to prevent type erasure errors. Without it, this method has
     * the same signature as flattenOr(e: => Exception) after type erasure.
     *
     * @param alternative the back-up item to return if the Option was None
     * @return Future[A]
     */
    def flattenOr[B >: A](alternative: => B)(implicit ec: ExecutionContext, d1: DummyImplicit): Future[B] =
      f map (_ getOrElse alternative )

    /** Alternatively choose another Future[Option[A]] if the Option is None
      *
      * @param alternative the op to run the Option was None
      * @return Future[Option(A)]
      */
    def orElse[B >: A](alternative: => Future[Option[B]])(implicit ec: ExecutionContext): Future[Option[B]] =
      f flatMap (_.fold(alternative)(a => Future.successful(Some(a))))
  }

  implicit class RichResult[A](f: Result[A]) {

    /**
     * Raises an exception if the inner future returns None.
     *
     * @param e the exception to be thrown if the Option was None
     * @return Option[Future,A]
     */
    def orThrow(e: => Exception)(implicit ec: ExecutionContext): Result[A] =
      optionT(f.run map (_ orElse (throw e)))
  }

  /**
   * Takes an `Option[A]` and a function `A => Future[B]`, optionally applies the function
   * and converts the result type (`Option[Future[B]]`) to `Future[Option[B]]`
   */
  def toFutureOption[A, B](option: Option[A], func: A => Future[B])(implicit ec: ExecutionContext): Future[Option[B]] =
      option map func map ( _ map ( Option(_) ) ) getOrElse Future.successful(None)

  /**
   * This typeclass represents the capability of converting values from type `S` to `Future[Option[T]]`.  
   * Users of this typeclass can chose the function used to lift values into `Future`s.  
   */
  trait FutureOptionView[S,T]{
    def apply(fut: Option[T] => Future[Option[T]]): S => Future[Option[T]]
  }

  trait LowerLowerPriorityImplicits{

    /**
     * Helper function to create instances of the `FutureOptionView` typeclass which 
     * actually use the lifting function. 
     */ 
    def withF[S,T](opt: S => Option[T]) = new FutureOptionView[S,T]{
      def apply(fut: Option[T] => Future[Option[T]]) = (s: S) => fut(opt(s))
    }

    /** 
     * Helper function which creates instances that ignore the lifting function. 
     */
    def withoutF[S,T](_fut: S => Future[Option[T]]) = new FutureOptionView[S,T]{
      def apply(fut: Option[T] => Future[Option[T]]) = (s: S) => _fut(s)
    }

    implicit def T_View[T]: FutureOptionView[T,T] = 
      withF( (s: T) => Some(s) )

  }

  trait LowerPriorityImplicits extends LowerLowerPriorityImplicits{
    
    implicit def FT_View[T](implicit ex: ExecutionContext): FutureOptionView[Future[T],T] = 
      withoutF( (s: Future[T]) => s map (Some(_)) )

  }

  object FutureOptionView extends LowerPriorityImplicits{

    implicit def OptT_View[T]: FutureOptionView[Option[T],T] = 
      withF( (s: Option[T]) => s )

    implicit def FOptT_View[T]: FutureOptionView[Future[Option[T]],T] = 
      withoutF( (s: Future[Option[T]]) => s )

  }

  val ? = optT

  object optT{

    /* This method uses as lifting function the standard Scala `Future` object */
    def <*[S, T](s: => S)(implicit view: FutureOptionView[S,T], ec: ExecutionContext): OptionT[Future,T] = 
      optionT(Future(s) flatMap view(Future(_)))

    /* This method uses the AL `now` function instead */
    def <~[S, T](s: => S)(implicit view: FutureOptionView[S,T], ec: ExecutionContext): OptionT[Future,T] = 
      optionT(Future(s) flatMap view(now(_)(ec)))

    def <<*[T](s: => Option[T])(implicit ec: ExecutionContext): OptionT[Future,Option[T]] =
      optionT[Future](Future(Some(s)))

    def <<~[T](s: => Option[T])(implicit ec: ExecutionContext): OptionT[Future,Option[T]] =
      optionT[Future](now(Some(s)))

    def <<~[T](s: Future[Option[T]])(implicit ec: ExecutionContext): OptionT[Future,Option[T]] =
      optionT[Future](s map { Some(_) })
    
  }

}

object OptionTFutureConversion extends OptionTFutureConversionOps {
  implicit final class func2FutureOption[A, B](val func: A => Future[B]) extends AnyVal {
    def <~(option: Option[A])(implicit ec: ExecutionContext) = toFutureOption(option, func)
  }
}
