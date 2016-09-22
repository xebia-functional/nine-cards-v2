package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog

import cats.syntax.either._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsPagerPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{AppLog, Jobs}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.CreateSharedCollection
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.{SharedCollectionsConfigurationException, SharedCollectionsException}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContextWrapper, Ui}
import monix.eval.Task

class PublishCollectionPresenter(actions: PublishCollectionActions)(implicit val collectionsPagerPresenter: CollectionsPagerPresenter, contextWrapper: ActivityContextWrapper)
  extends Jobs {

  var statuses = PublishCollectionStatuses()

  def initialize(collection: Collection): Unit = {
    statuses = statuses.copy(collection = Some(collection))
    actions.initialize().run
  }

  def showCollectionInformation(): Unit = {
    statuses.collection match {
      case Some(collection) => actions.goToPublishCollectionInformation(collection).run
      case None => actions.showMessageCollectionError.run
    }
  }

  def publishCollection(maybeName: Option[String], maybeCategory: Option[NineCardCategory]): Unit = {

    def getCollection: TaskService[Collection] = statuses.collection map { col =>
      TaskService(Task(Either.right(col)))
    } getOrElse TaskService(Task(Either.left(SharedCollectionsException("", None))))

    def createPublishedCollection(name: String, category: NineCardCategory): TaskService[String] =
      for {
        user <- di.userProcess.getUser
        collection <- getCollection
        sharedCollection = CreateSharedCollection(
          author = user.userProfile.name getOrElse (user.email getOrElse resGetString(R.string.defaultUser)),
          name = name,
          packages = collection.cards flatMap (_.packageName),
          category = category,
          icon = collection.icon,
          community = false)
        sharedCollectionId <- di.sharedCollectionsProcess.createSharedCollection(sharedCollection)
        _ <- di.collectionProcess.updateSharedCollection(collection.id, sharedCollectionId)
      } yield sharedCollectionId

    def errorUi(name: String, category: NineCardCategory) =
      actions.showMessagePublishingError ~ actions.goBackToPublishCollectionInformation(name, category)

    (for {
      name <- maybeName
      category <- maybeCategory
    } yield {
      createPublishedCollection(name, category).resolveAsyncUi2(
        onPreTask = () => actions.goToPublishCollectionPublishing(),
        onResult = (sharedCollectionId: String) => actions.goToPublishCollectionEnd(sharedCollectionId),
        onException = (e: Throwable) => e match {
          case e: SharedCollectionsConfigurationException =>
            AppLog.invalidConfigurationV2
            errorUi(name, category)
          case _ => errorUi(name, category)
        })
    }) getOrElse actions.showMessageFormFieldError.run
  }

  def launchShareCollection(sharedCollectionId: String): Unit =
    di.launcherExecutorProcess
      .launchShare(resGetString(R.string.shared_collection_url, sharedCollectionId))
      .resolveAsyncUi2(onException = _ => actions.showContactUsError)

}

case class PublishCollectionStatuses(
  collection: Option[Collection] = None)

trait PublishCollectionActions {

  def initialize(): Ui[Any]

  def goToPublishCollectionInformation(collection: Collection): Ui[Any]

  def goBackToPublishCollectionInformation(name: String, category: NineCardCategory): Ui[Any]

  def goToPublishCollectionPublishing(): Ui[Any]

  def goToPublishCollectionEnd(sharedCollectionId: String): Ui[Any]

  def showMessageCollectionError: Ui[Any]

  def showMessageFormFieldError: Ui[Any]

  def showMessagePublishingError: Ui[Any]

  def showContactUsError: Ui[Any]

}