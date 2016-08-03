package com.fortysevendeg.ninecardslauncher.app.di

import android.content.res.Resources
import android.support.v4.content.ContextCompat
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.fortysevendeg.ninecardslauncher.api.services._
import com.fortysevendeg.ninecardslauncher.app.observers.ObserverRegister
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcess
import com.fortysevendeg.ninecardslauncher.process.cloud.impl.CloudStorageProcessImpl
import com.fortysevendeg.ninecardslauncher.process.collection.{CollectionProcess, CollectionProcessConfig}
import com.fortysevendeg.ninecardslauncher.process.collection.impl.CollectionProcessImpl
import com.fortysevendeg.ninecardslauncher.process.commons.types.{NineCardCategory, NineCardsMoment}
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment._
import com.fortysevendeg.ninecardslauncher.process.device.DeviceProcess
import com.fortysevendeg.ninecardslauncher.process.device.impl.DeviceProcessImpl
import com.fortysevendeg.ninecardslauncher.process.moment.{MomentProcess, MomentProcessConfig}
import com.fortysevendeg.ninecardslauncher.process.moment.impl.MomentProcessImpl
import com.fortysevendeg.ninecardslauncher.process.social.SocialProfileProcess
import com.fortysevendeg.ninecardslauncher.process.social.impl.SocialProfileProcessImpl
import com.fortysevendeg.ninecardslauncher.process.recommendations.RecommendationsProcess
import com.fortysevendeg.ninecardslauncher.process.recommendations.impl.RecommendationsProcessImpl
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.SharedCollectionsProcess
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl.SharedCollectionsProcessImpl
import com.fortysevendeg.ninecardslauncher.process.theme.ThemeProcess
import com.fortysevendeg.ninecardslauncher.process.theme.impl.ThemeProcessImpl
import com.fortysevendeg.ninecardslauncher.process.user.UserProcess
import com.fortysevendeg.ninecardslauncher.process.user.impl.UserProcessImpl
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigProcess
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
import com.fortysevendeg.ninecardslauncher.services.plus.impl.GooglePlusServicesImpl
import com.fortysevendeg.ninecardslauncher.services.shortcuts.impl.ShortcutsServicesImpl
import com.fortysevendeg.ninecardslauncher.services.widgets.impl.WidgetsServicesImpl
import com.fortysevendeg.ninecardslauncher.services.wifi.impl.WifiServicesImpl
import com.fortysevendeg.ninecardslauncher2.{BuildConfig, R}
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.OkHttpClient
import com.google.android.gms.common.api.GoogleApiClient

trait Injector {

  def resources: Resources

  def recommendationsProcess: RecommendationsProcess

  def deviceProcess: DeviceProcess

  def collectionProcess: CollectionProcess

  def momentProcess: MomentProcess

  def userProcess: UserProcess

  def userConfigProcess: UserConfigProcess

  def themeProcess: ThemeProcess

  def sharedCollectionsProcess: SharedCollectionsProcess

  def createCloudStorageProcess(client: GoogleApiClient): CloudStorageProcess

  def createGooglePlusProcess(client: GoogleApiClient): SocialProfileProcess

  def observerRegister: ObserverRegister

}

class InjectorImpl(implicit contextSupport: ContextSupport) extends Injector {

  private[this] def createHttpClient = {
    val okHttpClientBuilder = new okhttp3.OkHttpClient.Builder()
    if (BuildConfig.DEBUG) {
      okHttpClientBuilder.addInterceptor(new StethoInterceptor)
    }
    new OkHttpClient(okHttpClientBuilder.build())
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

  private[this] lazy val imageServicesConfig = {

    def getColor(colorResource: Int): Int = ContextCompat.getColor(contextSupport.context, colorResource)

    ImageServicesConfig(
      colors = List(
        getColor(R.color.background_default_1),
        getColor(R.color.background_default_2),
        getColor(R.color.background_default_3),
        getColor(R.color.background_default_4),
        getColor(R.color.background_default_5)
      ))
  }

  private[this] lazy val imageServices = new ImageServicesImpl(
    config = imageServicesConfig)

  private[this] lazy val widgetsServices = new WidgetsServicesImpl()

  private[this] lazy val callsServices = new CallsServicesImpl(contentResolverWrapper)

  // Process

  lazy val recommendationsProcess = new RecommendationsProcessImpl(
    apiServices = apiServices,
    persistenceServices = persistenceServices)

  lazy val deviceProcess = new DeviceProcessImpl(
    appsServices = appsServices,
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

  private[this] lazy val namesMoments: Map[NineCardsMoment, String] = (moments map {
    moment =>
      val identifier = resources.getIdentifier(moment.getStringResource, "string", contextSupport.getPackageName)
      (moment, if (identifier != 0) resources.getString(identifier) else moment.name)
  }).toMap

  private[this] lazy val momentProcessConfig = MomentProcessConfig(
    namesMoments = namesMoments)

  private[this] lazy val wifiServices = new WifiServicesImpl()

  lazy val momentProcess = new MomentProcessImpl(
    momentProcessConfig = momentProcessConfig,
    persistenceServices = persistenceServices,
    wifiServices = wifiServices)

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

  def createCloudStorageProcess(client: GoogleApiClient): CloudStorageProcess = {
    val services = new DriveServicesImpl(client)
    new CloudStorageProcessImpl(services, persistenceServices)
  }

  override def createGooglePlusProcess(client: GoogleApiClient): SocialProfileProcess = {
    val services = new GooglePlusServicesImpl(client)
    new SocialProfileProcessImpl(services, persistenceServices)
  }

  lazy val observerRegister = new ObserverRegister(uriCreator)

}
