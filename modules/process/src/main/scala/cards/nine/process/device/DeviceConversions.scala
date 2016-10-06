package cards.nine.process.device

import cards.nine.commons.contexts.ContextSupport
import cards.nine.models._
import cards.nine.models.types._
import cards.nine.process.commons.NineCardIntentConversions
import cards.nine.process.commons.models.NineCardIntent
import cards.nine.process.device.models.{ContactEmail, ContactPhone, _}
import cards.nine.services.persistence._
import cards.nine.services.persistence.models.{DataCounter => ServicesDataCounter, DockApp => ServicesDockApp}

trait DeviceConversions extends NineCardIntentConversions {

  val defaultDate = 0L

  def toFetchAppOrder(orderBy: GetAppOrder): FetchAppOrder = orderBy match {
    case GetByName(_) => OrderByName
    case GetByInstallDate(_) => OrderByInstallDate
    case GetByCategory(_) => OrderByCategory
  }

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

  def toSimpleLastCallsContact(number: String, calls: Seq[Call]): LastCallsContact = {
    val (hasContact, name, date) = calls.headOption map { call =>
      (call.name.isDefined, call.name getOrElse number, call.date)
    } getOrElse (false, number, defaultDate)
    LastCallsContact(
      hasContact = hasContact,
      number = number,
      title = name,
      lastCallDate = date,
      calls = calls)
  }

  def toTermCounter(item: ContactCounter): TermCounter = TermCounter(
    term = item.term,
    count = item.count)

  def toTermCounter(item: ServicesDataCounter): TermCounter = TermCounter(
    term = item.term,
    count = item.count)

  def toContactEmail(item: ContactEmail): ContactEmail = ContactEmail(
    address = item.address,
    category = item.category)

  def toContactPhone(item: ContactPhone): ContactPhone = ContactPhone(
    number = item.number,
    category = item.category)

  def toAppsWithWidgets(apps: Seq[Application], widgets: Seq[Widget]): Seq[AppsWithWidgets] = apps map { app =>
    AppsWithWidgets(
      packageName = app.packageName,
      name = app.name,
      widgets = widgets filter(_.packageName == app.packageName)
    )
  }

}
