package cards.nine.app.ui.collections.jobs

import cards.nine.app.ui.collections.jobs.uiactions.ToolbarUiActions
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CollectionValues._
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

  "pullToClose" should {
    "call to pullCloseScrollY" in new ToolbarJobsScope {

      mockToolbarUiActions.pullCloseScrollY(any, any) returns serviceRight(Unit)
      toolbarJobs.pullToClose(scrollY, close = true).mustRightUnit
      there was one(mockToolbarUiActions).pullCloseScrollY(scrollY, true)
    }
  }
}
