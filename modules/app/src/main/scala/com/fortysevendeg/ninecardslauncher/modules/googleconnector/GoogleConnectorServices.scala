package com.fortysevendeg.ninecardslauncher.modules.googleconnector

import android.app.Activity

import scala.concurrent.{Future, ExecutionContext}

trait GoogleConnectorServices {

  def requestToken(activity: Activity, username: String)(implicit executionContext: ExecutionContext): Future[Unit]

}
