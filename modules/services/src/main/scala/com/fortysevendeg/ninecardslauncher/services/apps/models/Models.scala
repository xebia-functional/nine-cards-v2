package com.fortysevendeg.ninecardslauncher.services.apps.models

import scala.util.{Failure, Success, Try}

case class Application(
  name: String,
  packageName: String,
  className: String)