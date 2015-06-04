package com.fortysevendeg.ninecardslauncher.di

import android.accounts.AccountManager
import android.content.Context
import com.fortysevendeg.ninecardslauncher.models.AppConversions
import com.fortysevendeg.ninecardslauncher.modules.api.ApiServices
import com.fortysevendeg.ninecardslauncher.modules.api.impl.ApiServicesImpl
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.AppManagerServices
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl.AppManagerServicesImpl
import com.fortysevendeg.ninecardslauncher.modules.googleconnector.GoogleConnectorServices
import com.fortysevendeg.ninecardslauncher.modules.googleconnector.impl.GoogleConnectorServicesImpl
import com.fortysevendeg.ninecardslauncher.modules.image.ImageServices
import com.fortysevendeg.ninecardslauncher.modules.image.impl.ImageServicesImpl
import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistenceServices
import com.fortysevendeg.ninecardslauncher.modules.persistent.impl.PersistenceServicesImpl
import com.fortysevendeg.ninecardslauncher.modules.repository.RepositoryServices
import com.fortysevendeg.ninecardslauncher.modules.repository.impl.RepositoryServicesImpl
import com.fortysevendeg.ninecardslauncher.modules.user.UserService
import com.fortysevendeg.ninecardslauncher.modules.user.impl.UserServiceImpl
import com.fortysevendeg.ninecardslauncher2.R
import com.fortysevendeg.rest.client.ServiceClient
import macroid.ContextWrapper

trait Module
  extends ApiModule
  with RepositoryModule {

  lazy val persistentServices: PersistenceServices = new PersistenceServicesImpl

  private def context()(implicit contextWrapper: ContextWrapper): Context = contextWrapper.bestAvailable
  
  def createApiServices()(implicit contextWrapper: ContextWrapper): ApiServices = {
    val serviceClient: ServiceClient = createServiceClient(context.getString(R.string.api_base_url))
    new ApiServicesImpl(
      resources = context.getResources,
      repositoryServices = createRepositoryServices,
      apiUserService = createApiUserService(serviceClient),
      googlePlayService = createApiGooglePlayService(serviceClient),
      userConfigService = createApiUserConfigService(serviceClient))
  }

  def createUserServices()(implicit contextWrapper: ContextWrapper): UserService =
    new UserServiceImpl(
      apiServices = createApiServices,
      repositoryServices = createRepositoryServices)

  def createImageServices()(implicit contextWrapper: ContextWrapper): ImageServices =
    new ImageServicesImpl(
      resources = context.getResources,
      packageManager = context.getPackageManager,
      cacheDir = context.getDir("icons_apps", Context.MODE_PRIVATE))

  val GoogleKeyPreferences = "__google_auth__"

  def createRepositoryServices()(implicit contextWrapper: ContextWrapper): RepositoryServices =
    new RepositoryServicesImpl(
      cacheCategoryRepositoryClient = createCacheCategoryClient,
      collectionRepositoryClient = createCollectionClient,
      cardRepositoryClient = createCardClient,
      geoInfoRepositoryClient = createGeoInfoClient,
      cr = context.getContentResolver,
      filesDir = context.getFilesDir,
      preferences = context.getSharedPreferences(GoogleKeyPreferences, Context.MODE_PRIVATE))

  def createAppManagerServices()(implicit contextWrapper: ContextWrapper): AppManagerServices =
    new AppManagerServicesImpl(
      packageManager = context.getPackageManager,
      apiServices = createApiServices,
      imageServices = createImageServices,
      repositoryServices = createRepositoryServices)

  def createGoogleConnectorServices()(implicit contextWrapper: ContextWrapper): GoogleConnectorServices =
    new GoogleConnectorServicesImpl(
      accountManager = AccountManager.get(context),
      oAuthScopes = context.getString(R.string.oauth_scopes),
      repositoryServices = createRepositoryServices,
      userServices = createUserServices)

  def createAppConversions()(implicit contextWrapper: ContextWrapper): AppConversions =
    new AppConversions(createImageServices)

}
