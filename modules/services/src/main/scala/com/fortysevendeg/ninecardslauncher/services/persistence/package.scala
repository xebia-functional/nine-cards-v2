package com.fortysevendeg.ninecardslauncher.services

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

package object persistence {

  type Service[Req, Res] = Req => Future[Res]

}
