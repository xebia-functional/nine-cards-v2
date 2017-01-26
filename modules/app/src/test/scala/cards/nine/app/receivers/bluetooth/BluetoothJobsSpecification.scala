/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.receivers.bluetooth

import cards.nine.app.ui.commons.BroadAction
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
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
      override def sendBroadCastTask(broadAction: BroadAction): TaskService[Unit] =
        TaskService.empty
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

    "call to remove all devices connected in Context Support" in new BluetoothJobsScope {
      val device = "My Bluetooth Device"

      bluetoothJobs.removeAllBluetoothDevices().mustRightUnit

      there was one(mockContextSupport).clearBluetoothDevices()

    }

  }

}
