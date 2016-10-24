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
import cards.nine.process.theme.models.NineCardsTheme
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

  def createSharedContentJob
    (implicit
      activityContextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      uiContext: UiContext[_]) : SharedContentJobs = {
    new SharedContentJobs(new SharedContentUiActions())
  }

}

case class SharedContentStatuses(
  theme: NineCardsTheme = AppUtils.getDefaultTheme,
  sharedContent: Option[SharedContent] = None)