package com.fortysevendeg.ninecardslauncher.repository.commons

sealed trait NineCardsUri



case object CardUri extends NineCardsUri

case object CollectionUri extends NineCardsUri

case object GeoInfoUri extends NineCardsUri

case object UserUri extends NineCardsUri