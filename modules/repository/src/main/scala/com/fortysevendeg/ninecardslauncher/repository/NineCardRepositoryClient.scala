package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.commons.ContentResolverProvider
import com.fortysevendeg.ninecardslauncher.repository.repositories.{CardRepositoryClient, CollectionRepositoryClient}

trait NineCardRepositoryClient
    extends CardRepositoryClient
    with CollectionRepositoryClient
    with ContentResolverProvider