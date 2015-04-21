package com.fortysevendeg.ninecardslauncher.api

import com.fortysevendeg.ninecardslauncher.api.services.{UserConfigServiceClient, SharedCollectionsServiceClient}
import com.fortysevendeg.ninecardslauncher.api.services.{UserClient, UserConfigServiceClient, SharedCollectionsServiceClient}
import com.fortysevendeg.rest.client.ServiceClient

trait NineCardsServiceClient
  extends UserConfigServiceClient
  with UserClient
  with SharedCollectionsServiceClient