package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{App => RepoApp, AppData => RepoAppData}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.App
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddAppRequest, UpdateAppRequest}

trait AppConversions {

  def toAppSeq(cache: Seq[RepoApp]): Seq[App] = cache map toApp

  def toApp(app: RepoApp): App =
    App(
      id = app.id,
      name = app.data.name,
      packageName = app.data.packageName,
      className = app.data.className,
      category = app.data.category,
      imagePath = app.data.imagePath,
      colorPrimary = app.data.colorPrimary,
      dateInstalled = app.data.dateInstalled,
      dateUpdate = app.data.dateUpdate,
      version = app.data.version,
      installedFromGooglePlay = app.data.installedFromGooglePlay)

  def toRepositoryApp(request: UpdateAppRequest): RepoApp =
    RepoApp(
      id = request.id,
      data = RepoAppData(
        name = request.name,
        packageName = request.packageName,
        className = request.className,
        category = request.category,
        imagePath = request.imagePath,
        colorPrimary = request.colorPrimary,
        dateInstalled = request.dateInstalled,
        dateUpdate = request.dateUpdate,
        version = request.version,
        installedFromGooglePlay = request.installedFromGooglePlay)
    )

  def toRepositoryAppData(request: AddAppRequest): RepoAppData =
    RepoAppData(
      name = request.name,
      packageName = request.packageName,
      className = request.className,
      category = request.category,
      imagePath = request.imagePath,
      colorPrimary = request.colorPrimary,
      dateInstalled = request.dateInstalled,
      dateUpdate = request.dateUpdate,
      version = request.version,
      installedFromGooglePlay = request.installedFromGooglePlay)
}
