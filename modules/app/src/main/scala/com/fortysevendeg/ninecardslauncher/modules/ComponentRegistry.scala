package com.fortysevendeg.ninecardslauncher.modules

import com.fortysevendeg.ninecardslauncher.modules.api.ApiServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.AppManagerServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.googleconnector.GoogleConnectorServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.image.ImageServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistentServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.repository.cacheCategory.CacheCategoryRepositoryServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.repository.card.CardRepositoryServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.repository.collection.CollectionRepositoryServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.repository.geoInfo.GeoInfoRepositoryServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.user.UserServicesComponent

trait ComponentRegistry
    extends ImageServicesComponent
    with AppManagerServicesComponent
    with CacheCategoryRepositoryServicesComponent
    with CardRepositoryServicesComponent
    with CollectionRepositoryServicesComponent
    with GeoInfoRepositoryServicesComponent
    with PersistentServicesComponent
    with UserServicesComponent
    with GoogleConnectorServicesComponent
    with ApiServicesComponent
