package com.fortysevendeg.ninecardslauncher.process.device

import android.content.{Intent, ComponentName}
import android.util.Log
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.device.models.{ContactInfo, Contact, AppCategorized, Shortcut}
import com.fortysevendeg.ninecardslauncher.services.api.models.{GooglePlaySimplePackage, GooglePlayPackage, GooglePlayApp}
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact => ContactServices, ContactInfo => ContactInfoServices}
import com.fortysevendeg.ninecardslauncher.services.image.{AppPackage, AppWebsite}
import com.fortysevendeg.ninecardslauncher.services.persistence.AddCacheCategoryRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.{Shortcut => ShortcutServices}

import scala.util.{Failure, Success, Try}

trait DeviceConversions {

  def copyCacheCategory(app: AppCategorized, cacheCategory: Option[CacheCategory]): AppCategorized = app.copy(
    category = cacheCategory map (_.category),
    starRating = cacheCategory map (_.starRating),
    numDownloads = cacheCategory map (_.numDownloads),
    ratingsCount = cacheCategory map (_.ratingsCount),
    commentCount = cacheCategory map (_.commentCount)
  )

  def toAppWebSiteSeq(googlePlayPackages: Seq[GooglePlayPackage]): Seq[AppWebsite] = googlePlayPackages map {
    case GooglePlayPackage(GooglePlayApp(docid, title, _, _, Some(icon), _, _, _, _, _, _)) =>
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

  def toShortcutSeq(items: Seq[ShortcutServices])(implicit context: ContextSupport): Seq[Shortcut] = items map toShortcut

  def toShortcut(item: ShortcutServices)(implicit context: ContextSupport): Shortcut = {
    val componentName = new ComponentName(item.packageName, item.name)
    val drawable = Try(context.getPackageManager.getActivityIcon(componentName)).toOption
    val intent = new Intent(Intent.ACTION_CREATE_SHORTCUT)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.setComponent(componentName)
    Shortcut(
      title = item.title,
      icon = drawable,
      intent = intent
    )
  }

  def toContactSeq(items: Seq[ContactServices]): Seq[Contact] = items map toContact

  def toContact(item: ContactServices): Contact = Contact(
      name = item.name,
      photoUri = item.photoUri,
      info = item.info map toContactInfo)

  def toContactInfo(item: ContactInfoServices): ContactInfo = ContactInfo(
    emails = item.emails map (_.address),
    phones = item.phones map (_.number))
}
