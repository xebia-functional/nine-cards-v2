package com.fortysevendeg.ninecardslauncher.services.image

trait Conversions {

  def toAppPackagePath(app: AppPackage, path: String) =
    AppPackagePath(
      packageName = app.packageName,
      className = app.className,
      path = path
    )

  def toAppWebsitePath(app: AppWebsite, path: String) =
    AppWebsitePath(
      packageName = app.packageName,
      url = app.url,
      path = path
    )

}
