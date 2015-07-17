package com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl

import com.fortysevendeg.ninecardslauncher.api.services._
import com.fortysevendeg.ninecardslauncher.commons.{ContextWrapperProvider, Service}
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.image._
import com.fortysevendeg.ninecardslauncher.modules.user.UserServicesComponent
import com.fortysevendeg.ninecardslauncher.repository.commons.ContentResolverWrapperImpl
import com.fortysevendeg.ninecardslauncher.services.api.impl.{ApiServicesConfig, ApiServicesImpl}
import com.fortysevendeg.ninecardslauncher.services.api.models._
import com.fortysevendeg.ninecardslauncher.services.api.{GooglePlayPackagesResponse, RequestConfig}
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.OkHttpClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AppManagerServicesComponentImpl
  extends AppManagerServicesComponent {

  self: ContextWrapperProvider
    with ImageServicesComponent
    with UserServicesComponent =>

  lazy val appManagerServices = new AppManagerServicesImpl

  class AppManagerServicesImpl
    extends AppManagerServices {

    val packageManager = contextProvider.application.getPackageManager

    private lazy val serviceClient = new ServiceClient(
      httpClient = new OkHttpClient(),
      baseUrl = contextProvider.application.getString(com.fortysevendeg.ninecardslauncher2.R.string.api_base_url))

    private lazy val apiServices = new ApiServicesImpl(
      ApiServicesConfig(
        contextProvider.application.getString(com.fortysevendeg.ninecardslauncher2.R.string.api_app_id),
        contextProvider.application.getString(com.fortysevendeg.ninecardslauncher2.R.string.api_app_key),
        contextProvider.application.getString(com.fortysevendeg.ninecardslauncher2.R.string.api_localization)),
      new ApiUserService(serviceClient),
      new ApiGooglePlayService(serviceClient),
      new ApiUserConfigService(serviceClient))

    private lazy val contentResolverWrapper = new ContentResolverWrapperImpl(
      contextProvider.application.getContentResolver)

    override def createBitmapsForNoPackagesInstalled: Service[IntentsRequest, PackagesResponse] =
      request => {
        val packagesNoFound = request.intents flatMap {
          intent =>
            if (Option(packageManager.resolveActivity(intent, 0)).isEmpty) intent.extractPackageName()
            else None
        }
        (for {
          GooglePlayPackagesResponse(_, packages) <- googlePlayPackages(packagesNoFound)
          storeImageResponses <- storeImages(packages)
        } yield {
            PackagesResponse(storeImageResponses flatMap (_.packageName))
          }).recover {
          case _ => PackagesResponse(Seq.empty)
        }
      }

    private def storeImages(packages: Seq[GooglePlayPackage]): Future[Seq[StoreImageAppResponse]] =
      Future.sequence(packages map {
        p =>
          (p.app.docid, p.app.icon)
      } flatMap {
        case (packageName, maybeIcon) => maybeIcon map {
          icon =>
            imageServices.storeImageApp(StoreImageAppRequest(packageName, icon))
        }
      })

    private def googlePlayPackages(packages: Seq[String]): Future[GooglePlayPackagesResponse] =
      (for {
        user <- userServices.getUser
        token <- user.sessionToken
        androidId <- userServices.getAndroidId
      } yield {
          apiServices.googlePlayPackages(packages)(RequestConfig(androidId, token))
        }) getOrElse (throw new RuntimeException("User not found"))


  }

}
