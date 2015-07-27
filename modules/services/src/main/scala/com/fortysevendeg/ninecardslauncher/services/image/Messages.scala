package com.fortysevendeg.ninecardslauncher.services.image

case class AppPackage(
  packageName: String,
  className: String,
  name: String,
  icon: Int
  )

case class AppPackagePath(
  packageName: String,
  className: String,
  path: String
  )

case class AppWebsite(
  packageName: String,
  url: String,
  name: String
  )

case class AppWebsitePath(
  packageName: String,
  url: String,
  path: String
  )

case class ImageServicesConfig(
  colors: List[Int]
  )
