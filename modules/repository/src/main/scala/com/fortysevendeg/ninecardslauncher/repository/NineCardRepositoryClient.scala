package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.commons.ContentResolverProvider
import com.fortysevendeg.ninecardslauncher.repository.repositories.{GeoInfoRepositoryClient, CacheCategoryRepositoryClient, CardRepositoryClient, CollectionRepositoryClient}

trait NineCardRepositoryClient
    extends CardRepositoryClient
    with CollectionRepositoryClient
    with CacheCategoryRepositoryClient
    with GeoInfoRepositoryClient
    with ContentResolverProvider