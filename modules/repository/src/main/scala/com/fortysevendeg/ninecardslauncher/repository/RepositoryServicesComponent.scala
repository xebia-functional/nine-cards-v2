package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.service.Service

trait RepositoryServices {
  def addCollection: Service[AddCollectionRequest, AddCollectionResponse]
  def deleteCollection: Service[DeleteCollectionRequest, DeleteCollectionResponse]
  def getCollectionById: Service[GetCollectionByIdRequest, GetCollectionByIdResponse]
  def getCollectionByPosition: Service[GetCollectionByPositionRequest, GetCollectionByPositionResponse]
  def getCollectionByOriginalSharedCollectionId:
  Service[GetCollectionByOriginalSharedCollectionIdRequest, GetCollectionByOriginalSharedCollectionIdResponse]
  def updateCollection: Service[UpdateCollectionRequest, UpdateCollectionResponse]
}

trait RepositoryServicesComponent {
  val repositoryServices: RepositoryServices
}
