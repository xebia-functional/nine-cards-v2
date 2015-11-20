package com.fortysevendeg.ninecardslauncher.repository.provider

object NineCardsUri {

  val authorityPart = "com.fortysevendeg.ninecardslauncher2"

  val contentPrefix = "content://"

  val appUriString = s"$contentPrefix$authorityPart/${AppEntity.table}"

  val cardUriString = s"$contentPrefix$authorityPart/${CardEntity.table}"

  val collectionUriString = s"$contentPrefix$authorityPart/${CollectionEntity.table}"

  val geoInfoUriString = s"$contentPrefix$authorityPart/${GeoInfoEntity.table}"

  val userUriString = s"$contentPrefix$authorityPart/${UserEntity.table}"

  val dockAppUriString = s"$contentPrefix$authorityPart/${DockAppEntity.table}"
}
