package cards.nine.app.di

import android.content.res.Resources
import cards.nine.api.rest.client.ServiceClient
import cards.nine.api.rest.client.http.OkHttpClient
import cards.nine.app.observers.ObserverRegister
import cards.nine.app.ui.preferences.commons.{BackendV2Url, IsStethoActive, OverrideBackendV2Url}
import cards.nine.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import cards.nine.commons.contexts.ContextSupport
import cards.nine.models.types.NineCardsCategory
import cards.nine.models.types.NineCardsCategory._
import cards.nine.models.{CollectionProcessConfig, LauncherExecutorProcessConfig}
import cards.nine.process.accounts.UserAccountsProcess
import cards.nine.process.accounts.impl.UserAccountsProcessImpl
import cards.nine.process.cloud.CloudStorageProcess
import cards.nine.process.cloud.impl.CloudStorageProcessImpl
import cards.nine.process.collection.CollectionProcess
import cards.nine.process.collection.impl.CollectionProcessImpl
import cards.nine.process.device.DeviceProcess
import cards.nine.process.device.impl.DeviceProcessImpl
import cards.nine.process.intents.LauncherExecutorProcess
import cards.nine.process.intents.impl.LauncherExecutorProcessImpl
import cards.nine.process.moment.MomentProcess
import cards.nine.process.moment.impl.MomentProcessImpl
import cards.nine.process.recognition.RecognitionProcess
import cards.nine.process.recognition.impl.RecognitionProcessImpl
import cards.nine.process.recommendations.RecommendationsProcess
import cards.nine.process.recommendations.impl.RecommendationsProcessImpl
import cards.nine.process.sharedcollections.SharedCollectionsProcess
import cards.nine.process.sharedcollections.impl.SharedCollectionsProcessImpl
import cards.nine.process.social.SocialProfileProcess
import cards.nine.process.social.impl.SocialProfileProcessImpl
import cards.nine.process.theme.ThemeProcess
import cards.nine.process.theme.impl.ThemeProcessImpl
import cards.nine.process.thirdparty.ExternalServicesProcess
import cards.nine.process.trackevent.TrackEventProcess
import cards.nine.process.trackevent.impl.TrackEventProcessImpl
import cards.nine.process.user.UserProcess
import cards.nine.process.user.impl.UserProcessImpl
import cards.nine.process.userv1.UserV1Process
import cards.nine.process.userv1.impl.UserV1ProcessImpl
import cards.nine.process.widget.WidgetProcess
import cards.nine.process.widget.impl.WidgetProcessImpl
import cards.nine.repository.repositories._
import cards.nine.services.analytics.impl.AnalyticsTrackServices
import cards.nine.services.api.impl.{ApiServicesConfig, ApiServicesImpl}
import cards.nine.services.apps.impl.AppsServicesImpl
import cards.nine.services.awareness.impl.GoogleAwarenessServicesImpl
import cards.nine.services.calls.impl.CallsServicesImpl
import cards.nine.services.contacts.impl.ContactsServicesImpl
import cards.nine.services.drive.impl.DriveServicesImpl
import cards.nine.services.image.impl.ImageServicesImpl
import cards.nine.services.intents.impl.LauncherIntentServicesImpl
import cards.nine.services.permissions.impl.AndroidSupportPermissionsServices
import cards.nine.services.persistence.impl.PersistenceServicesImpl
import cards.nine.services.plus.impl.GooglePlusServicesImpl
import cards.nine.services.shortcuts.impl.ShortcutsServicesImpl
import cards.nine.services.track.impl.ConsoleTrackServices
import cards.nine.services.widgets.impl.WidgetsServicesImpl
import cards.nine.services.connectivity.impl.ConnectivityServicesImpl
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.fortysevendeg.ninecardslauncher.R
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.common.api.GoogleApiClient

trait Injector {

  def resources: Resources

  def recommendationsProcess: RecommendationsProcess

  def deviceProcess: DeviceProcess

  def collectionProcess: CollectionProcess

  def momentProcess: MomentProcess

  def userProcess: UserProcess

  def userV1Process: UserV1Process

  def widgetsProcess: WidgetProcess

  def themeProcess: ThemeProcess

  def sharedCollectionsProcess: SharedCollectionsProcess

  def recognitionProcess: RecognitionProcess

  def trackEventProcess: TrackEventProcess

  def cloudStorageProcess: CloudStorageProcess

  def socialProfileProcess: SocialProfileProcess

  def observerRegister: ObserverRegister

  def userAccountsProcess: UserAccountsProcess

  def launcherExecutorProcess: LauncherExecutorProcess

  def externalServicesProcess: ExternalServicesProcess

}

class InjectorImpl(implicit contextSupport: ContextSupport) extends Injector {

  private[this] def createHttpClient = {
    val okHttpClientBuilder = new okhttp3.OkHttpClient.Builder()
    if (IsStethoActive.readValueWith(contextSupport.context)) {
      okHttpClientBuilder.addNetworkInterceptor(new StethoInterceptor)
    }
    new OkHttpClient(okHttpClientBuilder.build())
  }

  val resources = contextSupport.getResources

  // Services

  private[this] lazy val serviceHttpClient = createHttpClient

  private[this] lazy val serviceClientV1 = new ServiceClient(
    httpClient = serviceHttpClient,
    baseUrl = resources.getString(R.string.api_base_url))

  private[this] lazy val serviceClient = {
    val backendV2Url =
      if (OverrideBackendV2Url.readValueWith(contextSupport.context)) {
        BackendV2Url.readValueWith(contextSupport.context)
      } else resources.getString(R.string.api_v2_base_url)
    new ServiceClient(httpClient = serviceHttpClient, baseUrl = backendV2Url)
  }

  private[this] lazy val apiServicesConfig = ApiServicesConfig(
    appId = resources.getString(R.string.api_app_id),
    appKey = resources.getString(R.string.api_app_key),
    localization = resources.getString(R.string.api_localization))

  private[this] lazy val apiServices = new ApiServicesImpl(
    apiServicesConfig = apiServicesConfig,
    apiService = new cards.nine.api.version2.ApiService(serviceClient),
    apiServiceV1 = new cards.nine.api.version1.ApiService(serviceClientV1))

  private[this] lazy val awarenessServices = {
    val client = new GoogleApiClient.Builder(contextSupport.context).addApi(Awareness.API).build()
    client.connect()
    new GoogleAwarenessServicesImpl(client)
  }

  private[this] lazy val contentResolverWrapper =
    new ContentResolverWrapperImpl(contextSupport.getContentResolver)

  private[this] lazy val uriCreator = new UriCreator

  private[this] lazy val persistenceServices =
    new PersistenceServicesImpl(
      appRepository = new AppRepository(contentResolverWrapper, uriCreator),
      cardRepository = new CardRepository(contentResolverWrapper, uriCreator),
      collectionRepository = new CollectionRepository(contentResolverWrapper, uriCreator),
      dockAppRepository = new DockAppRepository(contentResolverWrapper, uriCreator),
      momentRepository = new MomentRepository(contentResolverWrapper, uriCreator),
      userRepository = new UserRepository(contentResolverWrapper, uriCreator),
      widgetRepository = new WidgetRepository(contentResolverWrapper, uriCreator))

  private[this] lazy val appsServices = new AppsServicesImpl()

  private[this] lazy val shortcutsServices = new ShortcutsServicesImpl()

  private[this] lazy val contactsServices = new ContactsServicesImpl(contentResolverWrapper)

  private[this] lazy val imageServices = new ImageServicesImpl()

  private[this] lazy val widgetsServices = new WidgetsServicesImpl()

  private[this] lazy val callsServices = new CallsServicesImpl(contentResolverWrapper)

  private[this] lazy val connectivityServices = new ConnectivityServicesImpl()

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
    callsServices = callsServices,
    connectivityServices = connectivityServices)

  private[this] lazy val nameCategories: Map[NineCardsCategory, String] =
    (allCategories map { category =>
      val identifier =
        resources.getIdentifier(category.getIconResource, "string", contextSupport.getPackageName)
      (category, if (identifier != 0) resources.getString(identifier) else category.name)
    }).toMap

  private[this] lazy val collectionProcessConfig = CollectionProcessConfig(
    namesCategories = nameCategories)

  lazy val collectionProcess = new CollectionProcessImpl(
    collectionProcessConfig = collectionProcessConfig,
    persistenceServices = persistenceServices,
    contactsServices = contactsServices,
    appsServices = appsServices,
    apiServices = apiServices,
    awarenessServices = awarenessServices,
    widgetsServices = widgetsServices)

  lazy val momentProcess = new MomentProcessImpl(
    persistenceServices = persistenceServices,
    connectivityServices = connectivityServices,
    awarenessServices = awarenessServices)

  lazy val userProcess =
    new UserProcessImpl(apiServices = apiServices, persistenceServices = persistenceServices)

  lazy val userV1Process = new UserV1ProcessImpl(
    apiServices = apiServices,
    persistenceServices = persistenceServices
  )

  lazy val widgetsProcess = new WidgetProcessImpl(persistenceServices = persistenceServices)

  lazy val themeProcess = new ThemeProcessImpl()

  lazy val sharedCollectionsProcess = new SharedCollectionsProcessImpl(
    apiServices = apiServices,
    persistenceServices = persistenceServices)

  lazy val cloudStorageProcess =
    new CloudStorageProcessImpl(new DriveServicesImpl, persistenceServices)

  lazy val socialProfileProcess =
    new SocialProfileProcessImpl(new GooglePlusServicesImpl, persistenceServices)

  override def recognitionProcess: RecognitionProcess = {
    val client = new GoogleApiClient.Builder(contextSupport.context).addApi(Awareness.API).build()
    client.connect()
    new RecognitionProcessImpl(persistenceServices, new GoogleAwarenessServicesImpl(client))
  }

  override def trackEventProcess: TrackEventProcess = {
    def createService() = {
      val resources = contextSupport.getResources
      if (resources.getString(R.string.analytics_enabled).equalsIgnoreCase("true")) {
        val track = GoogleAnalytics
          .getInstance(contextSupport.context)
          .newTracker(resources.getString(R.string.ga_trackingId))
        track.setAppName(resources.getString(R.string.app_name))
        track.enableAutoActivityTracking(false)
        new AnalyticsTrackServices(track)
      } else {
        new ConsoleTrackServices
      }
    }
    new TrackEventProcessImpl(createService())
  }

  lazy val observerRegister = new ObserverRegister(uriCreator)

  lazy val userAccountsProcess: UserAccountsProcess = {
    val services = new AndroidSupportPermissionsServices
    new UserAccountsProcessImpl(services)
  }

  lazy val launcherExecutorProcess: LauncherExecutorProcess = {
    val config = LauncherExecutorProcessConfig(
      resources.getString(R.string.google_play_url),
      resources.getString(R.string.sendEmailDialogChooserTitle),
      resources.getString(R.string.sendTo))
    val services = new LauncherIntentServicesImpl
    new LauncherExecutorProcessImpl(config, services)
  }

  lazy val externalServicesProcess = new ExternalServicesProcess
}
