package com.fortysevendeg.ninecardslauncher.process.device

import android.content.{ComponentName, Intent}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.commons.Dimensions._
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.device.types.{CallType, WidgetResizeMode}
import com.fortysevendeg.ninecardslauncher.services.api.models.{GooglePlayApp, GooglePlayPackage}
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.calls.models.{Call => ServicesCall}
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact => ServicesContact, ContactCounter, ContactEmail => ServicesContactEmail, ContactInfo => ServicesContactInfo, ContactPhone => ServicesContactPhone}
import com.fortysevendeg.ninecardslauncher.services.image.{AppPackage, AppWebsite}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{App => ServicesApp, DataCounter => ServicesDataCounter, DockApp => ServicesDockApp}
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.{Shortcut => ServicesShortcut}
import com.fortysevendeg.ninecardslauncher.services.widgets.models.{Widget => ServicesWidget}

import scala.util.Try

trait DeviceConversions extends NineCardIntentConversions {

  val defaultDate = 0L

  def toFetchAppOrder(orderBy: GetAppOrder): FetchAppOrder = orderBy match {
    case GetByName(_) => OrderByName
    case GetByInstallDate(_) => OrderByInstallDate
    case GetByCategory(_) => OrderByCategory
  }

  def toApp(app: ServicesApp): App =
    App(
      name = app.name,
      packageName = app.packageName,
      className = app.className,
      category = NineCardCategory(app.category),
      imagePath = app.imagePath,
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

  def toDockApp(app: ServicesDockApp): DockApp = DockApp(
    name = app.name,
    dockType = DockType(app.dockType),
    intent = jsonToNineCardIntent(app.intent),
    imagePath = app.imagePath,
    position = app.position
  )

  def toDockApp(app: Application, position: Int, imagePath: String)(implicit context: ContextSupport): DockApp = DockApp(
    name = app.packageName,
    dockType = AppDockType,
    intent = toNineCardIntent(app),
    imagePath = imagePath,
    position = position)

  def toShortcutSeq(items: Seq[ServicesShortcut])(implicit context: ContextSupport): Seq[Shortcut] = items map toShortcut

  def toShortcut(item: ServicesShortcut)(implicit context: ContextSupport): Shortcut = {
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

  def toSimpleLastCallsContact(number: String, calls: Seq[ServicesCall]): LastCallsContact = {
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

  def toCallData(item: ServicesCall): CallData =
    CallData(
      date = item.date,
      callType = CallType(item.callType))

  def toTermCounter(item: ContactCounter): TermCounter = TermCounter(
    term = item.term,
    count = item.count)

  def toTermCounter(item: ServicesDataCounter): TermCounter = TermCounter(
    term = item.term,
    count = item.count)

  def toContactSeq(items: Seq[ServicesContact]): Seq[Contact] = items map toContact

  def toContact(item: ServicesContact): Contact = Contact(
      name = item.name,
      lookupKey = item.lookupKey,
      photoUri = item.photoUri,
      hasPhone = item.hasPhone,
      favorite = item.favorite,
      info = item.info map toContactInfo)

  def toContactInfo(item: ServicesContactInfo): ContactInfo = ContactInfo(
    emails = item.emails map toContactEmail,
    phones = item.phones map toContactPhone)

  def toContactEmail(item: ServicesContactEmail): ContactEmail = ContactEmail(
    address = item.address,
    category = EmailCategory(item.category))

  def toContactPhone(item: ServicesContactPhone): ContactPhone = ContactPhone(
    number = item.number,
    category = PhoneCategory(item.category))

  def toWidget(item: ServicesWidget): Widget = Widget(
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
