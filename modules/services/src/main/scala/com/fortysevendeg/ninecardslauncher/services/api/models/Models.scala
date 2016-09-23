package com.fortysevendeg.ninecardslauncher.services.api.models

case class PackagesByCategory(
  category: String,
  packages: Seq[String])