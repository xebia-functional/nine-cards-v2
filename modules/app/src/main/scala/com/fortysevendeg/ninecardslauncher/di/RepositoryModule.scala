package com.fortysevendeg.ninecardslauncher.di

import android.content.Context
import com.fortysevendeg.ninecardslauncher.commons.ContentResolverWrapperImpl
import com.fortysevendeg.ninecardslauncher.repository.repositories.{GeoInfoRepositoryClient, CollectionRepositoryClient, CardRepositoryClient, CacheCategoryRepositoryClient}

trait RepositoryModule {

  def createContextResolverWrapper(context: Context) =
    new ContentResolverWrapperImpl(context.getContentResolver)

  def createCacheCategoryClient(context: Context) =
    new CacheCategoryRepositoryClient(createContextResolverWrapper(context))

  def createCardClient(context: Context) =
    new CardRepositoryClient(createContextResolverWrapper(context))

  def createCollectionClient(context: Context) =
    new CollectionRepositoryClient(createContextResolverWrapper(context))

  def createGeoInfoClient(context: Context) =
    new GeoInfoRepositoryClient(createContextResolverWrapper(context))

}
