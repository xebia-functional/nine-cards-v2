package com.fortysevendeg

import org.specs2.execute.{AsResult, Result}
import org.specs2.specification.{Around, Scope}

trait BaseTestSupport extends Around with Scope {

  override def around[T: AsResult](t: => T): Result = AsResult.effectively(t)


}
