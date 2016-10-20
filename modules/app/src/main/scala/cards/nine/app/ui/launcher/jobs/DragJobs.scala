package cards.nine.app.ui.launcher.jobs

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.jobs.uiactions.{DragUiActions, MainAppDrawerUiActions, NavigationUiActions}
import cards.nine.models.{ApplicationData, CardData, Contact}
import macroid.ActivityContextWrapper

class DragJobs(
  val mainAppDrawerUiActions: MainAppDrawerUiActions,
  val navigationUiActions: NavigationUiActions,
  val dragUiActions: DragUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions {

  def startAddItemToCollection(app: ApplicationData): TaskService[Unit] = startAddItemToCollection(toCardData(app))

  def startAddItemToCollection(contact: Contact): TaskService[Unit] = startAddItemToCollection(toCardData(contact))

  private[this] def startAddItemToCollection(card: CardData): TaskService[Unit] = {
    statuses = statuses.startAddItem(card)
    for {
      _ <- mainAppDrawerUiActions.close()
      _ <- navigationUiActions.goToCollectionWorkspace().resolveIf(!mainAppDrawerUiActions.dom.isCollectionWorkspace, ())
      _ <- dragUiActions.startAddItem(card.cardType)
    } yield ()
  }

}
