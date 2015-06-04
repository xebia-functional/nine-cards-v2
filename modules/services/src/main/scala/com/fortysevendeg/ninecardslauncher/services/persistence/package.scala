package com.fortysevendeg.ninecardslauncher.services

import scala.concurrent.Future

package object persistence {

  type Service[Req, Res] = Req => Future[Res]

}
