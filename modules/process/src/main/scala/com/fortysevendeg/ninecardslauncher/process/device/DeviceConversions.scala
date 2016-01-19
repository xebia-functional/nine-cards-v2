package com.fortysevendeg.ninecardslauncher.process.device

import android.content.{ComponentName, Intent}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.Dimensions._
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.process.commons.types.{DockType, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.device.types.{CallType, WidgetResizeMode}
import com.fortysevendeg.ninecardslauncher.services.api.models.{GooglePlayApp, GooglePlayPackage}
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.calls.models.{Call => CallServices}
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact => ContactServices, ContactEmail => ContactEmailServices, ContactInfo => ContactInfoServices, ContactPhone => ContactPhoneServices}
import com.fortysevendeg.ninecardslauncher.services.image.{AppPackage, AppWebsite}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{App => AppPersistence, DockApp => DockAppPersistence}
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.{Shortcut => ShortcutServices}
import com.fortysevendeg.ninecardslauncher.services.widgets.models.{Widget => WidgetServices}

import scala.util.Try

trait DeviceConversions extends NineCardIntentConversions {

  val defaultDate = 0L

  def toFetchAppOrder(orderBy: GetAppOrder): FetchAppOrder = orderBy match {
    case GetByName(_) => OrderByName
    case GetByInstallDate(_) => OrderByInstallDate
    case GetByCategory(_) => OrderByCategory
  }

  def toApp(app: AppPersistence): App =
    App(
      name = app.name,
      packageName = app.packageName,
      className = app.className,
      category = NineCardCategory(app.category),
      imagePath = app.imagePath,
      colorPrimary = app.colorPrimary,
      dateInstalled = app.dateInstalled,
      dateUpdate = app.dateUpdate,
      version = app.version,
      installedFromGooglePlay = app.installedFromGooglePlay)

  def toAddAppRequest(item: Application, category: NineCardCategory, imagePath: String): AddAppRequest =
      AddAppRequest(
        name = item.name,
        packageName = item.packageName,
        className = item.className,
        category = category.name,
        imagePath = imagePath,
        colorPrimary = item.colorPrimary,
        dateInstalled = item.dateInstalled,
        dateUpdate = item.dateUpdate,
        version = item.version,
        installedFromGooglePlay = item.installedFromGooglePlay)

  def toUpdateAppRequest(id: Int, item: Application, category: NineCardCategory, imagePath: String): UpdateAppRequest =
      UpdateAppRequest(
        id = id,
        name = item.name,
        packageName = item.packageName,
        className = item.className,
        category = category.name,
        imagePath = imagePath,
        colorPrimary = item.colorPrimary,
        dateInstalled = item.dateInstalled,
        dateUpdate = item.dateUpdate,
        version = item.version,
        installedFromGooglePlay = item.installedFromGooglePlay)

  def toAppWebSiteSeq(googlePlayPackages: Seq[GooglePlayPackage]): Seq[AppWebsite] = googlePlayPackages flatMap {
    case GooglePlayPackage(GooglePlayApp(docid, title, _, _, Some(icon), _, _, _, _, _, _)) =>
      Some(AppWebsite(
        packageName = docid,
        url = icon,
        name = title))
    case _ => None
  }

  def toAppPackageSeq(items: Seq[Application]): Seq[AppPackage] = items map toAppPackage

  def toAppPackage(item: Application): AppPackage =
    AppPackage(
      packageName = item.packageName,
      className = item.className,
      name = item.name,
      icon = item.resourceIcon)

  def toCreateOrUpdateDockAppRequest(name: String, dockType: DockType, intent: NineCardIntent, imagePath: String, position: Int): CreateOrUpdateDockAppRequest =
    CreateOrUpdateDockAppRequest(
      name = name,
      dockType = dockType.name,
      intent = nineCardIntentToJson(intent),
      imagePath = imagePath,
      position = position)

  def toDockApp(app: DockAppPersistence): DockApp = DockApp(
    name = app.name,
    dockType = DockType(app.dockType),
    intent = jsonToNineCardIntent(app.intent),
    imagePath = app.imagePath,
    position = app.position
  )

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

  def toSimpleLastCallsContact(number: String, calls: Seq[CallServices]): LastCallsContact = {
    val (hasContact, name, date) = calls.headOption map { call =>
      (call.name.isDefined, call.name getOrElse number, call.date)
    } getOrElse (false, number, defaultDate)
    LastCallsContact(
      hasContact = hasContact,
      number = number,
      title = name,
      lastCallDate = date,
      calls = calls map toCallData)
  }

  def toCallData(item: CallServices): CallData =
    CallData(
      date = item.date,
      callType = CallType(item.callType))

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

  def toWidget(item: WidgetServices): Widget = Widget(
    userHashCode = item.userHashCode,
    autoAdvanceViewId = item.autoAdvanceViewId,
    initialLayout = item.initialLayout,
    dimensions = toWidgetDimensions(item.minHeight, item.minResizeHeight, item.minResizeWidth, item.minWidth),
    className = item.className,
    packageName = item.packageName,
    resizeMode = WidgetResizeMode(item.resizeMode),
    updatePeriodMillis = item.updatePeriodMillis,
    label = item.label,
    icon = item.icon,
    preview = item.preview)

  def toWidgetDimensions(minDPHeight: Int, minResizeDPHeight: Int, minResizeDPWidth: Int, minDPWidth: Int): WidgetDimensions =
    WidgetDimensions(
      minCellHeight = toCell(minDPHeight),
      minResizeCellHeight = toCell(minResizeDPHeight),
      minResizeCellWidth = toCell(minResizeDPWidth),
      minCellWidth = toCell(minDPWidth))

  private[this] def toCell(size: Int): Int = (size + margins) / cellSize

}
