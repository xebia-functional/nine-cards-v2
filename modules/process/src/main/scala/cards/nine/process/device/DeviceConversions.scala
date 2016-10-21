package cards.nine.process.device

import cards.nine.models._
import cards.nine.models.types._
import cards.nine.process.device.models.AppsWithWidgets

trait DeviceConversions extends NineCardsIntentConversions {

  val defaultDate = 0L

  def toFetchAppOrder(orderBy: GetAppOrder): FetchAppOrder = orderBy match {
    case GetByName(_) => OrderByName
    case GetByInstallDate(_) => OrderByInstallDate
    case GetByCategory(_) => OrderByCategory
  }

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

  def toContactEmail(item: ContactEmail): ContactEmail = ContactEmail(
    address = item.address,
    category = item.category)

  def toContactPhone(item: ContactPhone): ContactPhone = ContactPhone(
    number = item.number,
    category = item.category)

  def toAppsWithWidgets(apps: Seq[Application], widgets: Seq[AppWidget]): Seq[AppsWithWidgets] = apps map { app =>
    AppsWithWidgets(
      packageName = app.packageName,
      name = app.name,
      widgets = widgets filter(_.packageName == app.packageName)
    )
  }

}
