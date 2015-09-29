package com.fortysevendeg.ninecardslauncher.process.device

import android.content.{Intent, ComponentName}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.services.api.models.{GooglePlaySimplePackage, GooglePlayPackage, GooglePlayApp}
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact => ContactServices, ContactInfo => ContactInfoServices,
  ContactEmail => ContactEmailServices, ContactPhone => ContactPhoneServices}
import com.fortysevendeg.ninecardslauncher.services.image.{AppPackage, AppWebsite}
import com.fortysevendeg.ninecardslauncher.services.persistence.{UpdateAppRequest, AddAppRequest, AddCacheCategoryRequest}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{App => AppPersistence, AppData}
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.{Shortcut => ShortcutServices}

import scala.util.Try

trait DeviceConversions {

  def toApp(app: AppPersistence): App =
    App(
      name = app.name,
      packageName = app.packageName,
      className = app.className,
      category = app.category,
      imagePath = app.imagePath,
      colorPrimary = app.colorPrimary,
      dateInstalled = app.dateInstalled,
      dateUpdate = app.dateUpdate,
      version = app.version,
      installedFromGooglePlay = app.installedFromGooglePlay)

  def toAddAppRequest(item: Application, category: String, imagePath: String): AddAppRequest =
      AddAppRequest(
        name = item.name,
        packageName = item.packageName,
        className = item.packageName,
        category = category,
        imagePath = imagePath,
        colorPrimary = item.colorPrimary,
        dateInstalled = item.dateInstalled,
        dateUpdate = item.dateUpdate,
        version = item.version,
        installedFromGooglePlay = item.installedFromGooglePlay)

  def toUpdateAppRequest(id: Int, appData: AppData, category: String, imagePath: String): UpdateAppRequest =
      UpdateAppRequest(
        id = id,
        name = appData.name,
        packageName = appData.packageName,
        className = appData.packageName,
        category = category,
        imagePath = imagePath,
        colorPrimary = appData.colorPrimary,
        dateInstalled = appData.dateInstalled,
        dateUpdate = appData.dateUpdate,
        version = appData.version,
        installedFromGooglePlay = appData.installedFromGooglePlay)

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
        commentCount = item.commentCount)
  }

  def toAppPackageSeq(items: Seq[Application]): Seq[AppPackage] = items map toAppPackage

  def toAppPackage(item: Application): AppPackage =
    AppPackage(
      packageName = item.packageName,
      className = item.className,
      name = item.name,
      icon = item.resourceIcon)

  def toAppPackageByAppData(item: AppData): AppPackage =
    AppPackage(
      packageName = item.packageName,
      className = item.className,
      name = item.name,
      icon = item.resourceIcon)

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
      intent = intent)
  }

  def toContactSeq(items: Seq[ContactServices]): Seq[Contact] = items map toContact

  def toContact(item: ContactServices): Contact = Contact(
      name = item.name,
      lookupKey = item.lookupKey,
      photoUri = item.photoUri,
      info = item.info map toContactInfo)

  def toContactInfo(item: ContactInfoServices): ContactInfo = ContactInfo(
    emails = item.emails map toContactEmail,
    phones = item.phones map toContactPhone)

  def toContactEmail(item: ContactEmailServices): ContactEmail = ContactEmail(
    address = item.address,
    category = item.category.toString)

  def toContactPhone(item: ContactPhoneServices): ContactPhone = ContactPhone(
    number = item.number,
    category = item.category.toString)

}
