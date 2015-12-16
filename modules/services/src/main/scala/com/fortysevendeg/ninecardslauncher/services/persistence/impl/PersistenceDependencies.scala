package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.repository.repositories._

trait PersistenceDependencies {
  val appRepository: AppRepository
  val cardRepository: CardRepository
  val collectionRepository: CollectionRepository
  val dockAppRepository: DockAppRepository
  val geoInfoRepository: GeoInfoRepository
  val userRepository: UserRepository
}
