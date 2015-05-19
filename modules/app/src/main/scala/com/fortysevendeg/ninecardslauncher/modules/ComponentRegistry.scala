package com.fortysevendeg.ninecardslauncher.modules

import com.fortysevendeg.ninecardslauncher.modules.api.ApiServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.AppManagerServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.googleconnector.GoogleConnectorServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.image.ImageServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.repository.RepositoryServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.user.UserServicesComponent

trait ComponentRegistry
  extends ImageServicesComponent
  with AppManagerServicesComponent
  with RepositoryServicesComponent
  with UserServicesComponent
  with GoogleConnectorServicesComponent
  with ApiServicesComponent
