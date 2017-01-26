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

package cards.nine.app.commons

import android.content._
import BroadcastDispatcher._

trait BroadcastDispatcher { dispatcher: ContextWrapper =>

  val actionsFilters: Seq[String]

  def manageCommand(action: String, command: Option[String]): Unit = {}

  def manageQuestion(action: String): Unit = {}

  lazy val broadcast = new BroadcastReceiver {
    override def onReceive(context: Context, intent: Intent): Unit =
      Option(intent) map { i =>
        (Option(i.getAction),
         Option(i.getStringExtra(keyType)),
         Option(i.getStringExtra(keyCommand)))
      } match {
        case Some((Some(action: String), Some(key: String), data)) if key == commandType =>
          manageCommand(action, data)
        case Some((Some(action: String), Some(key: String), _)) if key == questionType =>
          manageQuestion(action)
        case _ =>
      }
  }

  def registerDispatchers() = {
    val intentFilter = new IntentFilter()
    actionsFilters foreach intentFilter.addAction
    registerReceiver(broadcast, intentFilter)
  }

  def unregisterDispatcher() = unregisterReceiver(broadcast)

}

object BroadcastDispatcher {
  val keyType      = "broadcast-key-type"
  val questionType = "broadcast-question"
  val commandType  = "broadcast-command"
  val keyCommand   = "broadcast-key-command"
}
