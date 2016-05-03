package com.fortysevendeg.ninecardslauncher.services.plus.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile
import com.fortysevendeg.ninecardslauncher.services.plus.{GooglePlusProcessException, GooglePlusServices, ImplicitsGooglePlusProcessExceptions}
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.plus.People.LoadPeopleResult
import com.google.android.gms.plus.Plus
import com.google.android.gms.plus.model.people.Person
import rapture.core.{Answer, Errata}

import scala.util.{Failure, Success, Try}
import scalaz.concurrent.Task

class GooglePlusServicesImpl(googleApiClient: GoogleApiClient)
  extends GooglePlusServices
  with ImplicitsGooglePlusProcessExceptions {

  val me = "me"

  override def loadUserProfile = (for {
    loadPeopleResult <- loadPeopleApi
    person <- fetchPerson(loadPeopleResult)
    name = fetchName(person)
    avatarUrl = fetchAvatarUrl(person)
    coverUrl = fetchCoverUrl(person)
  } yield GooglePlusProfile(name, avatarUrl, coverUrl)).resolve[GooglePlusProcessException]

  private[this] def loadPeopleApi: ServiceDef2[LoadPeopleResult, GooglePlusProcessException] = Service {
    Task {
      Try(Plus.PeopleApi.load(googleApiClient, me).await()) match {
        case Success(r) => Answer(r)
        case Failure(e) => Errata(GooglePlusProcessException(message = e.getMessage, cause = Some(e)))
      }
    }
  }

  private[this] def fetchPerson(loadPeopleResult: LoadPeopleResult): ServiceDef2[Person, GooglePlusProcessException] = Service {
    Task {
      CatchAll[GooglePlusProcessException] {
        val people = notNullOrThrow(loadPeopleResult, "LoadPeopleResult is null")
        val personBuffer = notNullOrThrow(people.getPersonBuffer, "PersonBuffer on LoadPeopleResult is null")
        if (personBuffer.getCount > 0) {
          notNullOrThrow(personBuffer.get(0), "Person in PersonBuffer is null")
        } else {
          throw new IllegalStateException("There aren't any persons in the PersonBuffer")
        }
      }
    }
  }

  private[this] def fetchName(person: Person): Option[String] = {
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

  private[this] def fetchAvatarUrl(person: Person): Option[String] = {
    Option(person.getImage) flatMap { image =>
      nonEmpty(image.getUrl)
    }
  }

  private[this] def fetchCoverUrl(person: Person): Option[String] =
    for {
      cover <- Option(person.getCover)
      coverPhoto <- Option(cover.getCoverPhoto)
      coverUrl <- nonEmpty(coverPhoto.getUrl)
    } yield coverUrl

  private[this] def notNullOrThrow[T](value: T, message: String): T = Option(value) match {
    case Some(v) => v
    case None => throw new IllegalStateException(message)
  }

  private[this] def nonEmpty(string: String): Option[String] =
    Option(string).find(_.nonEmpty)

}
