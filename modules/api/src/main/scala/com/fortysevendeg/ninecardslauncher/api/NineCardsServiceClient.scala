package com.fortysevendeg.ninecardslauncher.api

import com.fortysevendeg.ninecardslauncher.api.services._

trait NineCardsServiceClient
  extends UserConfigServiceClient
  with UserServiceClient
  with SharedCollectionsServiceClient
  with GooglePlayServiceClient
  with RecommendationServiceClient