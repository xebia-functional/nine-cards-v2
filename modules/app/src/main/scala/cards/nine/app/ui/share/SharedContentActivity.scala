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

package cards.nine.app.ui.share

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.ui.commons.{ActivityUiContext, AppUtils, UiContext}
import cards.nine.app.ui.share.SharedContentActivity._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.commons.services.TaskService._
import cards.nine.app.ui.share.models.SharedContent
import cards.nine.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.TypedFindView
import macroid.{ActivityContextWrapper, Contexts, FragmentManagerContext}

class SharedContentActivity
    extends AppCompatActivity
    with Contexts[AppCompatActivity]
    with ContextSupportProvider
    with TypedFindView { self =>

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(self)

  lazy val sharedContentJobs: SharedContentJobs = createSharedContentJob

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    (for {
      _ <- sharedContentJobs.initialize()
      _ <- sharedContentJobs.receivedIntent(getIntent)
    } yield ()).resolveAsync()
  }
}

object SharedContentActivity {

  var statuses = SharedContentStatuses()

  def createSharedContentJob(
      implicit activityContextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      uiContext: UiContext[_]): SharedContentJobs =
    new SharedContentJobs(new SharedContentUiActions())

}

case class SharedContentStatuses(
    theme: NineCardsTheme = AppUtils.getDefaultTheme,
    sharedContent: Option[SharedContent] = None)
