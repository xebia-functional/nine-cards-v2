package com.fortysevendeg.ninecardslauncher.modules.repository.impl

import android.content.ContentResolver
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.AppItem
import com.fortysevendeg.ninecardslauncher.modules.repository._
import com.fortysevendeg.ninecardslauncher.repository.NineCardRepositoryClient

import scala.concurrent.{ExecutionContext, Future}

trait RepositoryServicesComponentImpl
  extends RepositoryServicesComponent {

  self : AppContextProvider =>

  lazy val repositoryServices = new RepositoryServicesImpl

  class RepositoryServicesImpl
    extends RepositoryServices
    with NineCardRepositoryClient {

    override implicit val contentResolver: ContentResolver = appContextProvider.get.getContentResolver

    override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

    def toCard(app: AppItem) =
      Card(
        id = 1,
        position = 1,
        term = app.name,
        packageName = Some(app.packageName),
        `type` = "APP",
        intent = "",
        imagePath = app.imagePath
      )

    override def getCollections: Service[GetCollectionsRequest, GetCollectionsResponse] =
      request =>
        Future {
          GetCollectionsResponse(Seq.empty)
        }

  }

}
