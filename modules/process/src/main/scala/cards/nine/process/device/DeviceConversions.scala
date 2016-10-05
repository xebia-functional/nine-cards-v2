package cards.nine.process.device

import android.content.{ComponentName, Intent}
import cards.nine.commons.contexts.ContextSupport
import cards.nine.models.{Application, ApplicationData}
import cards.nine.models.types._
import cards.nine.process.commons.NineCardIntentConversions
import cards.nine.process.commons.models.NineCardIntent
import cards.nine.process.device.models._
import cards.nine.process.device.types.{CallType, WidgetResizeMode}
import cards.nine.services.calls.models.{Call => ServicesCall}
import cards.nine.services.contacts.models.{Contact => ServicesContact, ContactCounter, ContactEmail => ServicesContactEmail, ContactInfo => ServicesContactInfo, ContactPhone => ServicesContactPhone}
import cards.nine.services.image.{AppPackage, BitmapResize}
import cards.nine.services.persistence._
import cards.nine.services.persistence.models.{DataCounter => ServicesDataCounter, DockApp => ServicesDockApp}
import cards.nine.services.shortcuts.models.{Shortcut => ServicesShortcut}
import cards.nine.services.widgets.models.{Widget => ServicesWidget}

import scala.util.Try

trait DeviceConversions extends NineCardIntentConversions {

  val defaultDate = 0L

  def toFetchAppOrder(orderBy: GetAppOrder): FetchAppOrder = orderBy match {
    case GetByName(_) => OrderByName
    case GetByInstallDate(_) => OrderByInstallDate
    case GetByCategory(_) => OrderByCategory
  }

  def toApplication(app: Application): ApplicationData =
    ApplicationData(
      name = app.name,
      packageName = app.packageName,
      className = app.className,
      category = app.category,
      dateInstalled = app.dateInstalled,
      dateUpdate = app.dateUpdate,
      version = app.version,
      installedFromGooglePlay = app.installedFromGooglePlay)

  def toAddAppRequest(item: ApplicationData, category: NineCardCategory): AddAppRequest =
      AddAppRequest(
        name = item.name,
        packageName = item.packageName,
        className = item.className,
        category = category.name,
        dateInstalled = item.dateInstalled,
        dateUpdate = item.dateUpdate,
        version = item.version,
        installedFromGooglePlay = item.installedFromGooglePlay)

  def toUpdateAppRequest(id: Int, item: ApplicationData, category: NineCardCategory): UpdateAppRequest =
      UpdateAppRequest(
        id = id,
        name = item.name,
        packageName = item.packageName,
        className = item.className,
        category = category.name,
        dateInstalled = item.dateInstalled,
        dateUpdate = item.dateUpdate,
        version = item.version,
        installedFromGooglePlay = item.installedFromGooglePlay)

  def toAppPackageSeq(items: Seq[ApplicationData]): Seq[AppPackage] = items map toAppPackage

  def toAppPackage(item: ApplicationData): AppPackage =
    AppPackage(
      packageName = item.packageName,
      className = item.className,
      name = item.name)

  def toCreateOrUpdateDockAppRequest(name: String, dockType: DockType, intent: NineCardIntent, imagePath: String, position: Int): CreateOrUpdateDockAppRequest =
    CreateOrUpdateDockAppRequest(
      name = name,
      dockType = dockType.name,
      intent = nineCardIntentToJson(intent),
      imagePath = imagePath,
      position = position)

  def toCreateOrUpdateDockAppRequest(dockApp: SaveDockAppRequest): CreateOrUpdateDockAppRequest =
    CreateOrUpdateDockAppRequest(
      name = dockApp.name,
      dockType = dockApp.dockType.name,
      intent = dockApp.intent,
      imagePath = dockApp.imagePath,
      position = dockApp.position)

  def toDockApp(app: ServicesDockApp): DockApp = DockApp(
    name = app.name,
    dockType = DockType(app.dockType),
    intent = jsonToNineCardIntent(app.intent),
    imagePath = app.imagePath,
    position = app.position
  )

  def toDockApp(app: ApplicationData, position: Int, imagePath: String)(implicit context: ContextSupport): DockApp = DockApp(
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
    category = item.category)

  def toContactPhone(item: ServicesContactPhone): ContactPhone = ContactPhone(
    number = item.number,
    category = item.category)

  def toAppsWithWidgets(apps: Seq[Application], widgets: Seq[ServicesWidget]): Seq[AppsWithWidgets] = apps map { app =>
    AppsWithWidgets(
      packageName = app.packageName,
      name = app.name,
      widgets = widgets filter(_.packageName == app.packageName) map toWidget
    )
  }

  def toWidget(item: ServicesWidget): Widget = Widget(
    userHashCode = item.userHashCode,
    autoAdvanceViewId = item.autoAdvanceViewId,
    initialLayout = item.initialLayout,
    minWidth = item.minWidth,
    minHeight = item.minHeight,
    minResizeWidth = item.minResizeWidth,
    minResizeHeight = item.minResizeHeight,
    className = item.className,
    packageName = item.packageName,
    resizeMode = WidgetResizeMode(item.resizeMode),
    updatePeriodMillis = item.updatePeriodMillis,
    label = item.label,
    preview = item.preview)

  def toBitmapResize(iconResize: IconResize) =
    BitmapResize(
      width = iconResize.width,
      height = iconResize.height)

}
