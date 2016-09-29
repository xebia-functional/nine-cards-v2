package com.fortysevendeg.ninecardslauncher.repository

import cards.nine.commons.services.TaskService.NineCardException

case class RepositoryException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

trait ImplicitsRepositoryExceptions {
  implicit def repositoryException = (t: Throwable) => RepositoryException(t.getMessage, Option(t))
}