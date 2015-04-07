package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.repository.repositories.{CardRepositoryClient, CollectionRepositoryClient}

trait NineCardRepositoryClient
    extends CollectionRepositoryClient
    with CardRepositoryClient
    with AppContextProvider