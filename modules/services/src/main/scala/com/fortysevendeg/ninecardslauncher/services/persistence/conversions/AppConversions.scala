package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{App => RepositoryApp, AppData => RepositoryAppData}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.App
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddAppRequest, UpdateAppRequest}

trait AppConversions {

  def toApp(app: RepositoryApp): App =
    App(
      id = app.id,
      name = app.data.name,
      packageName = app.data.packageName,
      className = app.data.className,
      category = app.data.category,
      imagePath = app.data.imagePath,
      dateInstalled = app.data.dateInstalled,
      dateUpdate = app.data.dateUpdate,
      version = app.data.version,
      installedFromGooglePlay = app.data.installedFromGooglePlay)

  def toRepositoryApp(request: UpdateAppRequest): RepositoryApp =
    RepositoryApp(
      id = request.id,
      data = RepositoryAppData(
        name = request.name,
        packageName = request.packageName,
        className = request.className,
        category = request.category,
        imagePath = request.imagePath,
        dateInstalled = request.dateInstalled,
        dateUpdate = request.dateUpdate,
        version = request.version,
        installedFromGooglePlay = request.installedFromGooglePlay)
    )

  def toRepositoryAppData(request: AddAppRequest): RepositoryAppData =
    RepositoryAppData(
      name = request.name,
      packageName = request.packageName,
      className = request.className,
      category = request.category,
      imagePath = request.imagePath,
      dateInstalled = request.dateInstalled,
      dateUpdate = request.dateUpdate,
      version = request.version,
      installedFromGooglePlay = request.installedFromGooglePlay)
}
