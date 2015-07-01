package com.fortysevendeg.ninecardslauncher.commons

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

trait ContextSupportProvider {

  implicit val contextSupport: ContextSupport

}
