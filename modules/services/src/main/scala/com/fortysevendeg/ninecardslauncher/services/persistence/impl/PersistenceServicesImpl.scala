package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions

class PersistenceServicesImpl(
  val appRepository: AppRepository,
  val cardRepository: CardRepository,
  val collectionRepository: CollectionRepository,
  val dockAppRepository: DockAppRepository,
  val momentRepository: MomentRepository,
  val userRepository: UserRepository)
  extends PersistenceServices
  with Conversions
  with PersistenceDependencies
  with AppPersistenceServicesImpl
  with CardPersistenceServicesImpl
  with CollectionPersistenceServicesImpl
  with DockAppPersistenceServicesImpl
  with MomentPersistenceServicesImpl
  with UserPersistenceServicesImpl
  with AndroidPersistenceServicesImpl
  with ImplicitsPersistenceServiceExceptions