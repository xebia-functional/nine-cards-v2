package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.PersistMoment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Statuses.LauncherPresenterStatuses
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.collection.{CollectionException, CollectionExceptionImpl, CollectionProcess}
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.{DeviceProcess, DockAppException}
import com.fortysevendeg.ninecardslauncher.process.moment.{MomentException, MomentProcess}
import macroid.{ActivityContextWrapper, Ui}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

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
    with CollectionException
    with DockAppException
    with MomentException

  val collectionException = CollectionExceptionImpl("", None)

  trait WizardPresenterScope
    extends Scope {

    val mockActions = mock[LauncherUiActions]
    mockActions.showLoading() returns Ui[Any]()
    mockActions.showContactUsError() returns Ui[Any]()
    mockActions.showMinimumOneCollectionMessage() returns Ui[Any]()
    mockActions.canRemoveCollections returns true

    val mockInjector = mock[Injector]

    val mockCollectionProcess = mock[CollectionProcess]
    mockCollectionProcess.getCollections returns Service(Task(Answer(Seq(collection))))

    val mockDeviceProcess = mock[DeviceProcess]
    mockDeviceProcess.getDockApps returns Service(Task(Answer(Seq(dockApp))))

    val mockMomentProcess = mock[MomentProcess]
    mockMomentProcess.getBestAvailableMoment(any[ContextSupport]) returns Service(Task(Answer(Some(moment))))

    mockInjector.collectionProcess returns mockCollectionProcess
    mockInjector.deviceProcess returns mockDeviceProcess
    mockInjector.momentProcess returns mockMomentProcess

    val mockStatuses = mock[LauncherPresenterStatuses]

    val mockPersistMoment = mock[PersistMoment]
    mockPersistMoment.getPersistMoment returns None

    val presenter = new LauncherPresenter(mockActions) {
      override lazy val di: Injector = mockInjector
      override lazy val persistMoment: PersistMoment = mockPersistMoment
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

  "Remove Collection" should {

    "show error returning a failed" in
      new WizardPresenterScope {
        presenter.removeCollection(collection)
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

        mockCollectionProcess.getCollections returns Service(Task(Answer(Seq.empty[Collection])))

        presenter.loadLauncherInfo()
        there was after(1 seconds).no(mockActions).loadLauncherInfo(any, any)
      }

    "go to wizard returning a failed loading collections" in
      new WizardPresenterScope {

        mockCollectionProcess.getCollections returns Service(Task(Errata(collectionException)))

        presenter.loadLauncherInfo()
        there was after(1 seconds).no(mockActions).loadLauncherInfo(any, any)
      }

  }

}
