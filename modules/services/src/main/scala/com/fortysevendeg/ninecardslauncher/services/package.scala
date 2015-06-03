package com.fortysevendeg.ninecardslauncher

import scala.concurrent.Future

package object services {

  type Service[Req, Res] = Req => Future[Res]

}
