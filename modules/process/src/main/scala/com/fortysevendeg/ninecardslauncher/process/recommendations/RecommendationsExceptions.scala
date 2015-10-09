package com.fortysevendeg.ninecardslauncher.process.recommendations

import scalaz.Scalaz._

case class RecommendedAppsException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsRecommendationsException {
  implicit def recommendedAppException = (t: Throwable) => RecommendedAppsException(t.getMessage, t.some)
}