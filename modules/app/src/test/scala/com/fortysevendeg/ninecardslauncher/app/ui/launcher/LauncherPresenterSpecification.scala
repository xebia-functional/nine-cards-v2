package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.collection.{CollectionException, CollectionExceptionImpl}
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.DockAppException
import com.fortysevendeg.ninecardslauncher.process.device.models.DockApp
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.user.models.User
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

  val launcherAppsException = LauncherAppsException("", None)

  val collectionException = CollectionExceptionImpl("", None)

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
    mockActions.showDialogForRemoveCollection(collection) returns Ui[Any]()
    mockActions.goToWizard() returns Ui[Any]()
    mockActions.addCollection(collection) returns Ui[Any]()
    mockActions.loadUserProfile(user)

    val mockStatuses = mock[LauncherViewStatuses]
    mockStatuses.canRemoveCollections returns canRemoveCollections

    val presenter = new LauncherPresenter(mockActions, mockStatuses) {
      override protected def deleteCollection(id: Int): ServiceDef2[Unit, CollectionException] =
        Service(Task(Answer(())))
      override protected def getLauncherApps: ServiceDef2[(Seq[Collection], Seq[DockApp]), CollectionException with DockAppException] =
        Service(Task(Answer((collectionSeq, dockAppSeq))))
      override protected def getUser(): ServiceDef2[User, UserException] = Service(Task(Answer(user)))
    }

    val presenterFailed = new LauncherPresenter(mockActions, mockStatuses) {
      override protected def deleteCollection(id: Int): ServiceDef2[Unit, CollectionException] =
        Service(Task(Errata(collectionException)))
      override protected def getLauncherApps: ServiceDef2[(Seq[Collection], Seq[DockApp]), CollectionException with DockAppException] =
        Service(Task(Errata(launcherAppsException)))
      override protected def getUser(): ServiceDef2[User, UserException] = Service(Task(Errata(userException)))
    }

  }

}

class LauncherPresenterSpec
  extends LauncherPresenterSpecification {

  "Add Collection" should {

    "return a successful connecting account" in
      new WizardPresenterScope {
        presenter.addCollection(collection)
        there was after(1 seconds).one(mockActions).addCollection(collection)
      }

  }

  "Select collection to remove" should {

    "show dialog returning a successful data and it can remove collections" in
      new WizardPresenterScope {
        presenter.removeCollection(Some(collection))
        there was after(1 seconds).one(mockActions).showDialogForRemoveCollection(collection)
      }

    "show message returning a successful data and it can't remove collections" in
      new WizardPresenterScope {

        override val canRemoveCollections: Boolean = false

        presenter.removeCollection(Some(collection))
        there was after(1 seconds).one(mockActions).showMinimumOneCollectionMessage()

      }

    "show contact error if the parameter don't have a collection" in
      new WizardPresenterScope {
        presenter.removeCollection(None)
        there was after(1 seconds).one(mockActions).showContactUsError()
      }

  }

  "Remove Collection" should {

    "remove collection returning a successful data" in
      new WizardPresenterScope {
        presenter.removeCollection(collection)
        there was after(1 seconds).one(mockActions).removeCollection(collection)
      }

    "show error returning a failed" in
      new WizardPresenterScope {
        presenterFailed.removeCollection(collection)
        there was after(1 seconds).one(mockActions).showContactUsError()
      }

  }

  "Load Collections and DockApps" should {

    "load the list of collections and dock apps returning a successful data" in
      new WizardPresenterScope {
        presenter.loadCollectionsAndDockApps()
        there was after(1 seconds).one(mockActions).loadUserProfile(user)
        there was after(1 seconds).one(mockActions).loadCollections(collectionSeq, dockAppSeq)
      }

    "go to wizard returning a empty list" in
      new WizardPresenterScope {

        override val collectionSeq: Seq[Collection] = Seq.empty

        presenter.loadCollectionsAndDockApps()
        there was after(1 seconds).one(mockActions).goToWizard()
      }

    "go to wizard returning a failed loading collections" in
      new WizardPresenterScope {
        presenterFailed.loadCollectionsAndDockApps()
        there was after(1 seconds).one(mockActions).goToWizard()
      }

  }

}
