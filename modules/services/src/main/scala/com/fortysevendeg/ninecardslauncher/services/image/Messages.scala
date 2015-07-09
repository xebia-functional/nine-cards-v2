package com.fortysevendeg.ninecardslauncher.services.image

case class AppPackage(
    packageName: String,
    className: String,
    name: String,
    icon: Int)

case class AppWebsite(
    packageName: String,
    url: String,
    name: String)
