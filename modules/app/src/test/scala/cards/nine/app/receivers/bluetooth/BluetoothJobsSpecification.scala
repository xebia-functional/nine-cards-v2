package cards.nine.app.receivers.bluetooth

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.test.TaskServiceSpecification
import macroid.ContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait BluetoothJobsSpecification extends TaskServiceSpecification with Mockito {

  trait BluetoothJobsScope extends Scope {

    implicit val contextWrapper = mock[ContextWrapper]

    lazy implicit val mockContextSupport = mock[BluetoothContextSupport]

    val bluetoothJobs = new BluetoothJobs {
      override implicit def contextSupport(implicit ctx: ContextWrapper): ContextSupport =
        mockContextSupport
    }

  }

}

class BluetoothJobsSpec extends BluetoothJobsSpecification {

  "BluetoothJobs" should {

    "call to add bluetooth device in Context Support" in new BluetoothJobsScope {

      val device = "My Bluetooth Device"

      bluetoothJobs.addBluetoothDevice(device).mustRightUnit

      there was one(mockContextSupport).addBluetoothDevice(device)

    }

    "call to remove bluetooth device in Context Support" in new BluetoothJobsScope {

      val device = "My Bluetooth Device"

      bluetoothJobs.removeBluetoothDevice(device).mustRightUnit

      there was one(mockContextSupport).removeBluetoothDevice(device)

    }

  }

}
