package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.commons.{ContentResolverWrapperComponentImpl, ContentResolverProvider}
import com.fortysevendeg.ninecardslauncher.repository.repositories._

trait NineCardRepositoryClient
    extends CacheCategoryRepositoryClient
    with CardRepositoryClient
    with CollectionRepositoryClient
    with GeoInfoRepositoryClient
    with ContentResolverWrapperComponentImpl
    with ContentResolverProvider