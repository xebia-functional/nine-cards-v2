package com.fortysevendeg.ninecardslauncher.services.plus.impl

import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile
import com.fortysevendeg.ninecardslauncher.services.plus.{GooglePlusServices, GooglePlusServicesException, ImplicitsGooglePlusProcessExceptions}
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.{CommonStatusCodes, GoogleApiClient, ResultCallback}
import com.google.android.gms.plus.People.LoadPeopleResult
import com.google.android.gms.plus.Plus
import com.google.android.gms.plus.model.people.Person
import monix.eval.Task
import monix.execution.Cancelable

import scala.util.{Failure, Success, Try}

class GooglePlusServicesImpl
  extends GooglePlusServices
    with ImplicitsGooglePlusProcessExceptions {

  val me = "me"

  val recoverableStatusCodes: Seq[Int] = Seq(
    CommonStatusCodes.API_NOT_CONNECTED,
    CommonStatusCodes.CANCELED,
    CommonStatusCodes.INTERRUPTED,
    CommonStatusCodes.INVALID_ACCOUNT,
    CommonStatusCodes.RESOLUTION_REQUIRED,
    CommonStatusCodes.SIGN_IN_REQUIRED,
    GoogleSignInStatusCodes.SIGN_IN_CANCELLED,
    GoogleSignInStatusCodes.SIGN_IN_FAILED)

  val validCodes: Seq[Int] = Seq(
    CommonStatusCodes.SUCCESS,
    CommonStatusCodes.SUCCESS_CACHE)

  override def loadUserProfile(client: GoogleApiClient) = {

    def resultCallback(result: LoadPeopleResult): Either[GooglePlusServicesException, LoadPeopleResult] =
      Option(result) match {
        case Some(r) if validCodes.contains(r.getStatus.getStatusCode) =>
          Right(result)
        case Some(r) =>
          val message = Option(r.getStatus.getStatusMessage) getOrElse "Unknown error with Google API"
          Left(GooglePlusServicesException(
            message = message,
            recoverable = recoverableStatusCodes.contains(r.getStatus.getStatusCode)))
        case _ =>
          Left(GooglePlusServicesException(
            message = "Received null on the result",
            recoverable = false))
      }

    def loadPeopleApi: TaskService[LoadPeopleResult] = TaskService {
      Task.async[GooglePlusServicesException Either LoadPeopleResult] { (scheduler, callback) =>
        Try(Plus.PeopleApi.load(client, me)) match {
          case Success(pr) =>
            pr.setResultCallback(new ResultCallback[LoadPeopleResult] {
              override def onResult(r: LoadPeopleResult): Unit = callback(Success(resultCallback(r)))
            })
          case Failure(ex) =>
            callback(Success(Left(GooglePlusServicesException(
              message = Option(ex.getMessage) getOrElse "Error loading people API",
              cause = Some(ex),
              recoverable = false))))
        }
        Cancelable.empty
      }
    }

    def notNullOrThrow[T](value: T, message: String): T = Option(value) match {
      case Some(v) => v
      case None => throw new IllegalStateException(message)
    }

    def nonEmpty(string: String): Option[String] =
      Option(string).find(_.nonEmpty)

    def fetchPerson(loadPeopleResult: LoadPeopleResult): TaskService[Person] = TaskService {
      CatchAll[GooglePlusServicesException] {
        val people = notNullOrThrow(loadPeopleResult, "LoadPeopleResult is null")
        val personBuffer = notNullOrThrow(people.getPersonBuffer, "PersonBuffer on LoadPeopleResult is null")
        if (personBuffer.getCount > 0) {
          notNullOrThrow(personBuffer.get(0), "Person in PersonBuffer is null")
        } else {
          throw new IllegalStateException("There aren't any persons in the PersonBuffer")
        }
      }
    }

    def fetchName(person: Person): Option[String] = {
      val directNames = List(
        nonEmpty(person.getNickname),
        nonEmpty(person.getDisplayName))
      val personNames = Option(person.getName).toList flatMap { name =>
        List(
          nonEmpty(name.getGivenName),
          nonEmpty(name.getFamilyName))
      }
      (directNames ++ personNames).flatten.headOption
    }

    def fetchAvatarUrl(person: Person): Option[String] = {
      Option(person.getImage) flatMap { image =>
        nonEmpty(image.getUrl)
      }
    }

    def fetchCoverUrl(person: Person): Option[String] =
      for {
        cover <- Option(person.getCover)
        coverPhoto <- Option(cover.getCoverPhoto)
        coverUrl <- nonEmpty(coverPhoto.getUrl)
      } yield coverUrl

    (for {
      loadPeopleResult <- loadPeopleApi
      person <- fetchPerson(loadPeopleResult)
      name = fetchName(person)
      avatarUrl = fetchAvatarUrl(person)
      coverUrl = fetchCoverUrl(person)
    } yield GooglePlusProfile(name, avatarUrl, coverUrl)).resolve[GooglePlusServicesException]
  }

}
