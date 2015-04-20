package com.fortysevendeg.ninecardslauncher.api

import com.fortysevendeg.ninecardslauncher.api.services.{UserConfigServiceClient, SharedCollectionsServiceClient}

trait NineCardsServiceClient
  extends UserConfigServiceClient
  with SharedCollectionsServiceClient