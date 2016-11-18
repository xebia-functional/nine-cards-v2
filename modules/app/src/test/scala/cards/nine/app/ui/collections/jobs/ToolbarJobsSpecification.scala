package cards.nine.app.ui.collections.jobs

import cards.nine.app.ui.collections.jobs.uiactions.{ScrollUp, ToolbarUiActions}
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.TaskServiceSpecification
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait ToolbarJobsSpecification extends TaskServiceSpecification
  with Mockito {

  trait ToolbarJobsScope
    extends Scope {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockToolbarUiActions = mock[ToolbarUiActions]

    val toolbarJobs = new ToolbarJobs(mockToolbarUiActions)(contextWrapper)

  }

}


class ToolbarJobsSpec
  extends ToolbarJobsSpecification {

  "scrollY" should {
    "call to translationSrollY" in new ToolbarJobsScope {

      mockToolbarUiActions.translationScrollY(any) returns serviceRight(Unit)
      toolbarJobs.scrollY(scrollY).mustRightUnit
      there was one(mockToolbarUiActions).translationScrollY(scrollY)
    }
  }

  "scrollY" should {
    "call to scrollIdle" in new ToolbarJobsScope {

      mockToolbarUiActions.scrollIdle() returns serviceRight(Unit)
      toolbarJobs.scrollIdle().mustRightUnit
      there was one(mockToolbarUiActions).scrollIdle()
    }
  }

  "scrollY" should {
    "call to forceScrollType" in new ToolbarJobsScope {

      mockToolbarUiActions.forceScrollType(any) returns serviceRight(Unit)
      toolbarJobs.forceScrollType(ScrollUp).mustRightUnit
      there was one(mockToolbarUiActions).forceScrollType(ScrollUp)
    }
  }

  "pullToClose" should {
    "call to pullCloseScrollY" in new ToolbarJobsScope {

      mockToolbarUiActions.pullCloseScrollY(any, any, any) returns serviceRight(Unit)
      toolbarJobs.pullToClose(scrollY, ScrollUp, true).mustRightUnit
      there was one(mockToolbarUiActions).pullCloseScrollY(scrollY, ScrollUp, true)
    }
  }
}
