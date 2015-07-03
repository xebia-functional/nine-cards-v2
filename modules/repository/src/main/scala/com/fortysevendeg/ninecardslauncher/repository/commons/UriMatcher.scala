package com.fortysevendeg.ninecardslauncher.repository.commons

sealed trait NineCardsUri

case object CacheCategoryUri extends NineCardsUri
case object CardUri extends NineCardsUri
case object CollectionUri extends NineCardsUri
case object GeoInfoUri extends NineCardsUri
