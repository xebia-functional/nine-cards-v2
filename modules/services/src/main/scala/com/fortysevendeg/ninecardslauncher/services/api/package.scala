package com.fortysevendeg.ninecardslauncher.services

import scala.concurrent.Future

package object api {

  type Service[Req, Res] = Req => Future[Res]

}
