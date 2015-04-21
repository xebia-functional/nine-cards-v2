package com.fortysevendeg.ninecardslauncher.api

import com.fortysevendeg.ninecardslauncher.api.services.{GooglePlayServiceClient, UserConfigServiceClient, SharedCollectionsServiceClient}

trait NineCardsServiceClient
  extends UserConfigServiceClient
  with SharedCollectionsServiceClient
  with GooglePlayServiceClient