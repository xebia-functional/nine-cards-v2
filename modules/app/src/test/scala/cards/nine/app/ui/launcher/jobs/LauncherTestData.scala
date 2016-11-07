package cards.nine.app.ui.launcher.jobs

import cards.nine.app.ui.components.models.{LauncherMoment, LauncherData, MomentWorkSpace}
import cards.nine.models.types.NineCardsMoment

trait LauncherTestData {

  val idWidget = 1
  val appWidgetId = 1

  val launcherMoment = LauncherMoment(momentType = Option(NineCardsMoment.defaultMoment), collection = None)
  val launcherData =
    LauncherData(
      workSpaceType = MomentWorkSpace,
      moment = Option(launcherMoment),
      collections = Seq.empty,
      positionByType = 0)

  val numberPhone = "123456789"
  val packageName = "packageName"
}
