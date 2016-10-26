package cards.nine.process.device.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.process.device.{DeviceProcess, ImplicitsDeviceException, ResetException}
import cards.nine.services.persistence.ImplicitsPersistenceServiceExceptions

trait ResetProcessImpl extends DeviceProcess {

  self: DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsPersistenceServiceExceptions =>

  def resetSavedItems() =
    (for {
      _ <- persistenceServices.deleteAllWidgets()
      _ <- persistenceServices.deleteAllCollections()
      _ <- persistenceServices.deleteAllCards()
      _ <- persistenceServices.deleteAllDockApps()
    } yield ()).resolve[ResetException]

}
