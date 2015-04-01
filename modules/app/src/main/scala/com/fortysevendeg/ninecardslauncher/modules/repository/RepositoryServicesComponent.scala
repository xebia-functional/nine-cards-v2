package com.fortysevendeg.ninecardslauncher.modules.repository

import com.fortysevendeg.ninecardslauncher.commons.Service

trait RepositoryServices {
  def getCollections: Service[GetCollectionsRequest, GetCollectionsResponse]
}

trait RepositoryServicesComponent {
  val repositoryServices: RepositoryServices
}
