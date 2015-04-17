package com.fortysevendeg.ninecardslauncher.api

import com.fortysevendeg.ninecardslauncher.api.services.{UserClient, UserConfigServiceClient, SharedCollectionsServiceClient}
import com.fortysevendeg.rest.client.ServiceClient

trait NineCardsServiceClient
  extends ServiceClient
  with UserClient
  with UserConfigServiceClient
  with SharedCollectionsServiceClient