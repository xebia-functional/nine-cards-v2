package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Statuses.LauncherPresenterStatuses
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.{NineCardException, CatsService}
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, Moment}
import com.fortysevendeg.ninecardslauncher.process.device.models.DockApp
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.user.models.User
import macroid.{ActivityContextWrapper, Ui}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.concurrent.duration._
import scalaz.concurrent.Task

trait LauncherPresenterSpecification
  extends Specification
  with Mockito
  with LauncherPresenterData {

  implicit val contextSupport = mock[ContextSupport]

  implicit val contextWrapper = mock[ActivityContextWrapper]

  case class LauncherAppsException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException

  val launcherAppsException = LauncherAppsException("", None)

  val collectionException = CollectionException("", None)

  val userException = UserException("", None)

  trait WizardPresenterScope
    extends Scope {

    val canRemoveCollections = true

    val dockAppSeq = Seq(dockApp)

    val collectionSeq = Seq(collection)

    val mockActions = mock[LauncherUiActions]
    mockActions.showLoading() returns Ui[Any]()
    mockActions.showContactUsError() returns Ui[Any]()
    mockActions.showMinimumOneCollectionMessage() returns Ui[Any]()
    mockActions.canRemoveCollections returns canRemoveCollections

    val mockStatuses = mock[LauncherPresenterStatuses]

    val presenter = new LauncherPresenter(mockActions) {
      statuses = mockStatuses
      override protected def getLauncherInfo: CatsService[(Seq[Collection], Seq[DockApp], Option[Moment])] =
        CatsService(Task(Xor.right((collectionSeq, dockAppSeq, Some(moment)))))
      override protected def getUser: CatsService[User] = CatsService(Task(Xor.right(user)))
    }

    val presenterFailed = new LauncherPresenter(mockActions) {
      statuses = mockStatuses
      override protected def getLauncherInfo: CatsService[(Seq[Collection], Seq[DockApp], Option[Moment])] =
        CatsService(Task(Xor.left(launcherAppsException)))
      override protected def getUser: CatsService[User] = CatsService(Task(Xor.left(userException)))
    }

  }

}

class LauncherPresenterSpec
  extends LauncherPresenterSpecification {

  "Select collection to remove" should {

    "show dialog returning a successful data and it can remove collections" in
      new WizardPresenterScope {
        mockStatuses.collectionReorderMode returns Some(collection)
        presenter.removeCollectionInReorderMode()
        there was after(1 seconds).no(mockActions).showMinimumOneCollectionMessage()
        there was after(1 seconds).no(mockActions).showContactUsError()
      }

    "show message returning a successful data and it can't remove collections" in
      new WizardPresenterScope {
        override val canRemoveCollections: Boolean = false
        mockStatuses.collectionReorderMode returns Some(collection)
        presenter.removeCollectionInReorderMode()
        there was after(1 seconds).one(mockActions).showMinimumOneCollectionMessage()
      }

    "show contact error if the parameter don't have a collection" in
      new WizardPresenterScope {
        mockStatuses.collectionReorderMode returns None
        presenter.removeCollectionInReorderMode()
        there was after(1 seconds).one(mockActions).showContactUsError()
      }

  }

  "Remove Collection" should {

    "show error returning a failed" in
      new WizardPresenterScope {
        presenterFailed.removeCollection(collection)
        there was after(1 seconds).one(mockActions).showContactUsError()
      }

  }

  "Load Collections and DockApps" should {

    "returning a empty list the information is loaded" in
      new WizardPresenterScope {
        presenter.loadLauncherInfo()
        there was after(1 seconds).one(mockActions).loadLauncherInfo(any, any)
      }

    "returning a empty list the information isn't loaded" in
      new WizardPresenterScope {

        override val collectionSeq: Seq[Collection] = Seq.empty

        presenter.loadLauncherInfo()
        there was after(1 seconds).no(mockActions).loadLauncherInfo(any, any)
      }

    "go to wizard returning a failed loading collections" in
      new WizardPresenterScope {
        presenterFailed.loadLauncherInfo()
        there was after(1 seconds).no(mockActions).loadLauncherInfo(any, any)
      }

  }

}
