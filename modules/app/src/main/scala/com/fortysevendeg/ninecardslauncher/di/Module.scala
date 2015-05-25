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

trait Module
  extends ApiModule {

  lazy val persistentServices: PersistenceServices = new PersistenceServicesImpl
  
  def createApiServices(context: Context): ApiServices = {
    val serviceClient: ServiceClient = createServiceClient(context.getString(R.string.api_base_url))
    new ApiServicesImpl(
      resources = context.getResources,
      repositoryServices = createRepositoryServices(context),
      apiUserService = createApiUserService(serviceClient),
      googlePlayService = createApiGooglePlayService(serviceClient),
      userConfigService = createApiUserConfigService(serviceClient))
  }

  def createUserServices(context: Context): UserService =
    new UserServiceImpl(
      apiServices = createApiServices(context),
      repositoryServices = createRepositoryServices(context))

  def createImageServices(context: Context): ImageServices =
    new ImageServicesImpl(
      resources = context.getResources,
      packageManager = context.getPackageManager,
      cacheDir = context.getDir("icons_apps", Context.MODE_PRIVATE))

  val GoogleKeyPreferences = "__google_auth__"

  def createRepositoryServices(context: Context): RepositoryServices =
    new RepositoryServicesImpl(
      cr = context.getContentResolver,
      filesDir = context.getFilesDir,
      preferences = context.getSharedPreferences(GoogleKeyPreferences, Context.MODE_PRIVATE))

  def createAppManagerServices(context: Context): AppManagerServices =
    new AppManagerServicesImpl(
      packageManager = context.getPackageManager,
      apiServices = createApiServices(context),
      imageServices = createImageServices(context),
      repositoryServices = createRepositoryServices(context))

  def createGoogleConnectorServices(context: Context): GoogleConnectorServices =
    new GoogleConnectorServicesImpl(
      accountManager = AccountManager.get(context),
      oAuthScopes = context.getString(R.string.oauth_scopes),
      repositoryServices = createRepositoryServices(context),
      userServices = createUserServices(context))

  def createAppConversions(context: Context): AppConversions =
    new AppConversions(createImageServices(context))

}
