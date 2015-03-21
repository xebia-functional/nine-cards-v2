package com.fortysevendeg.ninecardslauncher.modules.repository.impl

import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.repository._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RepositoryServicesComponentImpl
  extends RepositoryServicesComponent {

  lazy val repositoryServices = new RepositoryServicesImpl

  class RepositoryServicesImpl
    extends RepositoryServices {

    override def getCollections: Service[GetCollectionsRequest, GetCollectionsResponse] =
      request =>
        Future {
          GetCollectionsResponse(Seq(
            Collection(
              id = 0,
              position = 0,
              name = "Home",
              `type` = "HOME_MORNING",
              icon = "icon",
              themedColorIndex = 1,
              sharedCollectionSubscribed = false,
              cards = Seq(
                Card(
                  id = 1,
                  position = 1,
                  term = "Gmail",
                  packageName = Some("com.google.android.gm"),
                  `type` = "APP",
                  intent = "{\nclassName: null,\ndataExtra: null,\nintentExtras: {\npackage_name: \"com.google.android.gm\"\n},\npackageName: null,\ncategories: null,\naction: \"com.fortysevendeg.ninecardslauncher.OPEN_APP\",\nclipData: null,\ndataString: null,\nextras: {\nclassLoader: {\nparent: null\n},\npairValue: \"com.google.android.gm\",\nempty: false,\nparcelled: false\n},\nflags: 0,\npackage: null,\nscheme: null,\nselector: null,\nsourceBounds: null,\ntype: null,\nexcludingStopped: false\n}",                  imagePath = ""
                )
              )
            )
          ))
        }

  }

}
