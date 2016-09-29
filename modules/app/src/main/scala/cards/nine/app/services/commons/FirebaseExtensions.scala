package cards.nine.app.services.commons

import cards.nine.app.ui.commons.AppLog._
import cards.nine.commons._
import com.google.firebase.iid.FirebaseInstanceId

import scala.util.{Failure, Success, Try}

object FirebaseExtensions {

  def readToken: Option[String] = Try(FirebaseInstanceId.getInstance()) match {
    case Success(instance) if instance != javaNull => Option(instance.getToken)
    case Success(_) =>
      printErrorMessage(new NullPointerException("Firebase instance null"))
      None
    case Failure(ex) =>
      printErrorMessage(ex)
      None
  }

}
