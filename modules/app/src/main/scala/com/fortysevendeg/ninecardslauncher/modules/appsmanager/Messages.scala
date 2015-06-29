package com.fortysevendeg.ninecardslauncher.modules.appsmanager

import com.fortysevendeg.ninecardslauncher.services.api.models.NineCardIntent

case class IntentsRequest(intents: Seq[NineCardIntent])

case class PackagesResponse(packages: Seq[String])