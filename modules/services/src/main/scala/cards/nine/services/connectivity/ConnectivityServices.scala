package cards.nine.services.connectivity

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.BluetoothDevice

trait ConnectivityServices {

  /**
   * Get the current SSID if it is available
   *
   * @return an Option[String] that contains the name of the SSID
   * @throws WifiServicesException if exist some problem to get the current SSID
   */
  def getCurrentSSID(implicit contextSupport: ContextSupport): TaskService[Option[String]]

  /**
   * Get all configured networks sorted by name. The Wifi must be connected
   *
   * @return Seq[String] that contains all SSIDs
   * @throws WifiServicesException if exist some problem getting the information
   */
  def getConfiguredNetworks(implicit contextSupport: ContextSupport): TaskService[Seq[String]]

  /**
   * Get all paired devices by Bluetooth. The Bluetooth must be connected
   *
   * @return Seq[BluetoothDevice] list of devices
   * @throws BluetoothServicesException if exist some problem getting the information
   */
  def getPairedDevices(implicit contextSupport: ContextSupport): TaskService[Seq[BluetoothDevice]]

}
