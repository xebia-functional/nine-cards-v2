package com.fortysevendeg.ninecardslauncher.di

import android.accounts.AccountManager
import android.content.Context
import android.content.res.Resources
import com.fortysevendeg.ninecardslauncher.api.services._
import com.fortysevendeg.ninecardslauncher.models.AppConversions
import com.fortysevendeg.ninecardslauncher.modules.api.ApiServices
import com.fortysevendeg.ninecardslauncher.modules.api.impl.ApiServicesImpl
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.AppManagerServices
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl.AppManagerServicesImpl
import com.fortysevendeg.ninecardslauncher.modules.googleconnector.GoogleConnectorServices
import com.fortysevendeg.ninecardslauncher.modules.googleconnector.impl.{GoogleConnector, GoogleConnectorServicesImpl}
import com.fortysevendeg.ninecardslauncher.modules.image.ImageServices
import com.fortysevendeg.ninecardslauncher.modules.image.impl.ImageServicesImpl
import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistenceServices
import com.fortysevendeg.ninecardslauncher.modules.persistent.impl.PersistenceServicesImpl
import com.fortysevendeg.ninecardslauncher.modules.repository.RepositoryServices
import com.fortysevendeg.ninecardslauncher.modules.repository.impl.RepositoryServicesImpl
import com.fortysevendeg.ninecardslauncher.modules.user.UserServices
import com.fortysevendeg.ninecardslauncher.modules.user.impl.UserServicesImpl
import com.fortysevendeg.ninecardslauncher2.R
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.OkHttpClient

trait Module {

  lazy val persistentServices: PersistenceServices = new PersistenceServicesImpl

  def createServiceClient(baseUrl: String): ServiceClient =
    new ServiceClient(new OkHttpClient, baseUrl)

  def createApiUserService(serviceClient: ServiceClient) =
    new ApiUserService(serviceClient)

  def createApiUserConfigService(serviceClient: ServiceClient) =
    new ApiUserConfigService(serviceClient)

  def createApiSharedCollectionsService(serviceClient: ServiceClient) =
    new ApiSharedCollectionsService(serviceClient)

  def createApiRecommendationService(serviceClient: ServiceClient) =
    new ApiRecommendationService(serviceClient)

  def createApiGooglePlayService(serviceClient: ServiceClient) =
    new ApiGooglePlayService(serviceClient)
  
  def createApiServices(context: Context): ApiServices = {
    val serviceClient: ServiceClient = createServiceClient(context.getString(R.string.api_base_url))
    new ApiServicesImpl(
      resources = context.getResources,
      userService = createApiUserService(serviceClient),
      googlePlayService = createApiGooglePlayService(serviceClient),
      userConfigService = createApiUserConfigService(serviceClient))
  }

  def createUserServices(context: Context): UserServices =
    new UserServicesImpl(
      apiServices = createApiServices(context),
      contentResolver = context.getContentResolver,
      filesDir = context.getFilesDir)

  def createImageServices(context: Context): ImageServices =
    new ImageServicesImpl(
      resources = context.getResources,
      packageManager = context.getPackageManager,
      cacheDir = context.getDir("icons_apps", Context.MODE_PRIVATE))

  def createRepositoryServices(context: Context): RepositoryServices =
    new RepositoryServicesImpl(context.getContentResolver)

  def createAppManagerServices(context: Context): AppManagerServices =
    new AppManagerServicesImpl(
      packageManager = context.getPackageManager,
      apiServices = createApiServices(context),
      userServices = createUserServices(context),
      imageServices = createImageServices(context),
      repositoryServices = createRepositoryServices(context))

  def createGoogleConnectorServices(context: Context): GoogleConnectorServices =
    new GoogleConnectorServicesImpl(
      accountManager = AccountManager.get(context),
      resources = context.getResources,
      preferences = context.getSharedPreferences(GoogleConnector.GoogleKeyPreferences, Context.MODE_PRIVATE),
      userServices = createUserServices(context))

  def createAppConversions(context: Context): AppConversions =
    new AppConversions(createImageServices(context))

}
