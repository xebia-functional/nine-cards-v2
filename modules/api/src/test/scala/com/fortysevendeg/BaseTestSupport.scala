package com.fortysevendeg

import org.specs2.execute.{Result, AsResult}
import org.specs2.specification.{Scope, Around}

trait BaseTestSupport extends Around with Scope {

  override def around[T: AsResult](t: => T): Result = AsResult.effectively(t)


}
