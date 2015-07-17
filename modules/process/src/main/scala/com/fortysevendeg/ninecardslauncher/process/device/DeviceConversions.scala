package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.services.api.models.{GooglePlaySimplePackage, GooglePlayPackage, GooglePlayApp}
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.image.{AppPackage, AppWebsite}
import com.fortysevendeg.ninecardslauncher.services.persistence.AddCacheCategoryRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory

trait DeviceConversions {

  def copyCacheCategory(app: AppCategorized, cacheCategory: Option[CacheCategory]): AppCategorized = app.copy(
    category = cacheCategory map (_.category),
    starRating = cacheCategory map (_.starRating),
    numDownloads = cacheCategory map (_.numDownloads),
    ratingsCount = cacheCategory map (_.ratingsCount),
    commentCount = cacheCategory map (_.commentCount)
  )

  def toAppWebSiteSeq(googlePlayPackages: Seq[GooglePlayPackage]): Seq[AppWebsite] = googlePlayPackages map {
    case GooglePlayPackage(GooglePlayApp(docid, title, _, _, Some(icon), _)) =>
      AppWebsite(
        packageName = docid,
        url = icon,
        name = title)
  }

  def toAddCacheCategoryRequestSeq(items: Seq[GooglePlaySimplePackage]): Seq[AddCacheCategoryRequest] = items map {
    item =>
      AddCacheCategoryRequest(
        packageName = item.packageName,
        category = item.appCategory,
        starRating = item.starRating,
        numDownloads = item.numDownloads,
        ratingsCount = item.ratingCount,
        commentCount = item.commentCount
      )
  }

  def toAppPackageSeq(items: Seq[Application]): Seq[AppPackage] = items map {
    item =>
      AppPackage(
        packageName = item.packageName,
        className = item.className,
        name = item.name,
        icon = item.icon
      )
  }

}
