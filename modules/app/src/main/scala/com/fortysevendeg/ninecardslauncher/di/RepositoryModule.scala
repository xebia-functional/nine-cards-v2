package com.fortysevendeg.ninecardslauncher.di

import android.content.Context
import com.fortysevendeg.ninecardslauncher.commons.ContentResolverWrapperImpl
import com.fortysevendeg.ninecardslauncher.repository.repositories.{GeoInfoRepositoryClient, CollectionRepositoryClient, CardRepositoryClient, CacheCategoryRepositoryClient}
import macroid.ContextWrapper

trait RepositoryModule {

  private def context()(implicit contextWrapper: ContextWrapper): Context = contextWrapper.bestAvailable

  def createContextResolverWrapper()(implicit contextWrapper: ContextWrapper) =
    new ContentResolverWrapperImpl(context.getContentResolver)

  def createCacheCategoryClient()(implicit contextWrapper: ContextWrapper) =
    new CacheCategoryRepositoryClient(createContextResolverWrapper)

  def createCardClient()(implicit contextWrapper: ContextWrapper) =
    new CardRepositoryClient(createContextResolverWrapper)

  def createCollectionClient()(implicit contextWrapper: ContextWrapper) =
    new CollectionRepositoryClient(createContextResolverWrapper)

  def createGeoInfoClient()(implicit contextWrapper: ContextWrapper) =
    new GeoInfoRepositoryClient(createContextResolverWrapper)

}
