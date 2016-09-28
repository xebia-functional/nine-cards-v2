package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.MomentPreferences
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Statuses.LauncherPresenterStatuses
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.collection.{CollectionException, CollectionProcess}
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.DeviceProcess
import com.fortysevendeg.ninecardslauncher.process.moment.MomentProcess
import macroid.{ActivityContextWrapper, Ui}
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.concurrent.duration._

trait LauncherPresenterSpecification
  extends Specification
  with Mockito
  with LauncherPresenterData {

  implicit val contextSupport = mock[ContextSupport]

  implicit val contextWrapper = mock[ActivityContextWrapper]

  val collectionException = CollectionException("", None)

  trait WizardPresenterScope
    extends Scope {

    val mockActions = mock[LauncherUiActions]
    mockActions.showLoading() returns Ui[Any]()
    mockActions.showContactUsError() returns Ui[Any]()
    mockActions.showMinimumOneCollectionMessage() returns Ui[Any]()
    mockActions.canRemoveCollections returns true

    val mockInjector = mock[Injector]

    val mockCollectionProcess = mock[CollectionProcess]

    val mockDeviceProcess = mock[DeviceProcess]

    val mockMomentProcess = mock[MomentProcess]

    val mockStatuses = mock[LauncherPresenterStatuses]

    val mockPersistMoment = mock[MomentPreferences]

    val presenter = new LauncherPresenter(mockActions) {
      override lazy val di: Injector = mockInjector
      override lazy val momentPreferences: MomentPreferences = mockPersistMoment
      statuses = mockStatuses
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

        mockActions.canRemoveCollections returns false
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


}
