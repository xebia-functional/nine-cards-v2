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

package cards.nine.app.services.sharedcollections

import android.app.{IntentService, Service}
import android.content.Intent
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.ui.commons.AppLog._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import macroid.Contexts

class UpdateSharedCollectionService
    extends IntentService("updateSharedCollectionService")
    with Contexts[Service]
    with ContextSupportProvider
    with UpdateSharedCollectionUiActions {

  lazy val jobs = new UpdateSharedCollectionJobs(this)

  override def onHandleIntent(intent: Intent): Unit =
    jobs.handleIntent(intent).resolveAsync(onException = e => printErrorMessage(e))

}

object UpdateSharedCollectionService {

  val intentExtraCollectionId = "_collectionId_"

  val intentExtraSharedCollectionId = "_sharedCollectionId_"

  val intentExtraPackages = "_addedPackages_"

  val actionUnsubscribe = "unsubscribeCollection"

  val actionSync = "syncCollection"

  val notificationId: Int = 2101

}
