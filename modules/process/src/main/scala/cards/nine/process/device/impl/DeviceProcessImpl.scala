package cards.nine.process.device.impl

import cards.nine.commons.contexts.ContextSupport
import cards.nine.process.device._
import cards.nine.services.api._
import cards.nine.services.apps.AppsServices
import cards.nine.services.calls.CallsServices
import cards.nine.services.contacts.ContactsServices
import cards.nine.services.image._
import cards.nine.services.persistence.{ImplicitsPersistenceServiceExceptions, PersistenceServices}
import cards.nine.services.shortcuts.ShortcutsServices
import cards.nine.services.widgets.WidgetsServices
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService.{NineCardException, TaskService}
import cards.nine.services.connectivity.ConnectivityServices
import cats.data.EitherT
import monix.eval.Task

class DeviceProcessImpl(
    val appsServices: AppsServices,
    val apiServices: ApiServices,
    val persistenceServices: PersistenceServices,
    val shortcutsServices: ShortcutsServices,
    val contactsServices: ContactsServices,
    val imageServices: ImageServices,
    val widgetsServices: WidgetsServices,
    val callsServices: CallsServices,
    val connectivityServices: ConnectivityServices)
    extends DeviceProcess
    with DeviceProcessDependencies
    with AppsDeviceProcessImpl
    with ContactsDeviceProcessImpl
    with DockAppsDeviceProcessImpl
    with LastCallsDeviceProcessImpl
    with ResetProcessImpl
    with ShortcutsDeviceProcessImpl
    with WidgetsDeviceProcessImpl
    with ImplicitsDeviceException
    with ImplicitsImageExceptions
    with ImplicitsPersistenceServiceExceptions {

  def getConfiguredNetworks(implicit context: ContextSupport): TaskService[Seq[String]] =
    connectivityServices.getConfiguredNetworks.resolve[DeviceException]

}
