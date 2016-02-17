package com.fortysevendeg.ninecardslauncher.app.di

import com.fortysevendeg.ninecardslauncher.api.services._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.GoogleApiClientProvider
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.cloud.impl.CloudStorageProcessImpl
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionProcessConfig
import com.fortysevendeg.ninecardslauncher.process.collection.impl.CollectionProcessImpl
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher.process.device.impl.DeviceProcessImpl
import com.fortysevendeg.ninecardslauncher.process.recommendations.impl.RecommendationsProcessImpl
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl.SharedCollectionsProcessImpl
import com.fortysevendeg.ninecardslauncher.process.theme.impl.ThemeProcessImpl
import com.fortysevendeg.ninecardslauncher.process.user.impl.UserProcessImpl
import com.fortysevendeg.ninecardslauncher.process.userconfig.impl.UserConfigProcessImpl
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.ninecardslauncher.services.api.impl.{ApiServicesConfig, ApiServicesImpl}
import com.fortysevendeg.ninecardslauncher.services.apps.impl.AppsServicesImpl
import com.fortysevendeg.ninecardslauncher.services.calls.impl.CallsServicesImpl
import com.fortysevendeg.ninecardslauncher.services.contacts.impl.ContactsServicesImpl
import com.fortysevendeg.ninecardslauncher.services.drive.impl.DriveServicesImpl
import com.fortysevendeg.ninecardslauncher.services.image.ImageServicesConfig
import com.fortysevendeg.ninecardslauncher.services.image.impl.ImageServicesImpl
import com.fortysevendeg.ninecardslauncher.services.persistence.impl.PersistenceServicesImpl
import com.fortysevendeg.ninecardslauncher.services.shortcuts.impl.ShortcutsServicesImpl
import com.fortysevendeg.ninecardslauncher.services.widgets.impl.WidgetsServicesImpl
import com.fortysevendeg.ninecardslauncher2.{BuildConfig, R}
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.OkHttpClient
import com.google.android.gms.common.api.GoogleApiClient
import com.squareup.{okhttp => okHttp}
import com.facebook.stetho.okhttp.StethoInterceptor

class Injector(implicit contextSupport: ContextSupport) {

  private[this] def createHttpClient = {
    val okHttpClient = new okHttp.OkHttpClient
    if (BuildConfig.DEBUG) {
      okHttpClient.networkInterceptors().add(new StethoInterceptor())
    }
    new OkHttpClient(okHttpClient)
  }

  val resources = contextSupport.getResources

  // Services

  private[this] lazy val serviceHttpClient = createHttpClient

  private[this] lazy val serviceClient = new ServiceClient(
    httpClient = serviceHttpClient,
    baseUrl = resources.getString(R.string.api_base_url))

  private[this] lazy val googlePlayServiceClient = new ServiceClient(
    httpClient = serviceHttpClient,
    baseUrl = resources.getString(R.string.api_google_play_url))

  private[this] lazy val apiServicesConfig = ApiServicesConfig(
    appId = resources.getString(R.string.api_app_id),
    appKey = resources.getString(R.string.api_app_key),
    localization = resources.getString(R.string.api_localization))

  private[this] lazy val apiServices = new ApiServicesImpl(
    apiServicesConfig = apiServicesConfig,
    apiUserService = new ApiUserService(serviceClient),
    googlePlayService = new ApiGooglePlayService(googlePlayServiceClient),
    userConfigService = new ApiUserConfigService(serviceClient),
    recommendationService = new ApiRecommendationService(serviceClient),
    sharedCollectionsService = new ApiSharedCollectionsService(serviceClient))

  private[this] lazy val contentResolverWrapper = new ContentResolverWrapperImpl(
    contextSupport.getContentResolver)

  private[this] lazy val uriCreator = new UriCreator

  private[this] lazy val persistenceServices = new PersistenceServicesImpl(
    appRepository = new AppRepository(contentResolverWrapper, uriCreator),
    cardRepository = new CardRepository(contentResolverWrapper, uriCreator),
    collectionRepository = new CollectionRepository(contentResolverWrapper, uriCreator),
    dockAppRepository = new DockAppRepository(contentResolverWrapper, uriCreator),
    momentRepository = new MomentRepository(contentResolverWrapper, uriCreator),
    userRepository = new UserRepository(contentResolverWrapper, uriCreator))

  private[this] lazy val appsServices = new AppsServicesImpl()

  private[this] lazy val shortcutsServices = new ShortcutsServicesImpl()

  private[this] lazy val contactsServices = new ContactsServicesImpl(contentResolverWrapper)

  private[this] lazy val imageServicesConfig = ImageServicesConfig(
    colors = List(
      resources.getColor(R.color.background_default_1),
      resources.getColor(R.color.background_default_2),
      resources.getColor(R.color.background_default_3),
      resources.getColor(R.color.background_default_4),
      resources.getColor(R.color.background_default_5)
    ))

  private[this] lazy val imageServices = new ImageServicesImpl(
    config = imageServicesConfig)

  private[this] lazy val widgetsServices = new WidgetsServicesImpl()

  private[this] lazy val callsServices = new CallsServicesImpl(contentResolverWrapper)

  // Process

  lazy val recommendationsProcess = new RecommendationsProcessImpl(
    apiServices = apiServices,
    persistenceServices = persistenceServices)

  lazy val deviceProcess = new DeviceProcessImpl(
    appsService = appsServices,
    apiServices = apiServices,
    persistenceServices = persistenceServices,
    shortcutsServices = shortcutsServices,
    contactsServices = contactsServices,
    imageServices = imageServices,
    widgetsServices = widgetsServices,
    callsServices = callsServices)

  private[this] lazy val nameCategories: Map[NineCardCategory, String] = (allCategories map {
    category =>
      val identifier = resources.getIdentifier(category.getIconResource, "string", contextSupport.getPackageName)
      (category, if (identifier != 0) resources.getString(identifier) else category.name)
  }).toMap

  private[this] lazy val collectionProcessConfig = CollectionProcessConfig(
    namesCategories = nameCategories)

  lazy val collectionProcess = new CollectionProcessImpl(
    collectionProcessConfig = collectionProcessConfig,
    persistenceServices = persistenceServices,
    contactsServices = contactsServices,
    appsServices = appsServices)

  lazy val userProcess = new UserProcessImpl(
    apiServices = apiServices,
    persistenceServices = persistenceServices)

  lazy val userConfigProcess = new UserConfigProcessImpl(
    apiServices = apiServices,
    persistenceServices = persistenceServices
  )

  lazy val themeProcess = new ThemeProcessImpl()

  lazy val sharedCollectionsProcess = new SharedCollectionsProcessImpl(
    apiServices = apiServices,
    persistenceServices = persistenceServices)

  def createCloudStorageProcess(client: GoogleApiClient, account: String) = {
    val services = new DriveServicesImpl(client)
    new CloudStorageProcessImpl(services, persistenceServices)
  }

}
