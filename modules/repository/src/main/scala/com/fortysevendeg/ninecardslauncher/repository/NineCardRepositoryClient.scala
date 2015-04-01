package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.repository.repositories.CollectionRepositoryClient
import com.fortysevendeg.macroid.extras.AppContextProvider

trait NineCardRepositoryClient
    extends CollectionRepositoryClient
    with AppContextProvider