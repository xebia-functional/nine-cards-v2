package com.fortysevendeg.ninecardslauncher.app.services.commons

import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import com.fortysevendeg.ninecardslauncher.commons._
import com.google.firebase.iid.FirebaseInstanceId

import scala.util.{Failure, Success, Try}

object FirebaseExtensions {

  def readToken: Option[String] = Try(FirebaseInstanceId.getInstance()) match {
    case Success(instance) if instance != javaNull => Option(instance.getToken)
    case Success(_) =>
      printErrorMessage(new NullPointerException("Firebase token null"))
      None
    case Failure(ex) =>
      printErrorMessage(ex)
      None
  }

}
