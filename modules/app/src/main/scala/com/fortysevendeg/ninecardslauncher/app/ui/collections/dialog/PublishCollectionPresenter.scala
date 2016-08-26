package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog

import cats.data.Xor
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.SharedCollectionsExceptions
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.CreateSharedCollection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class PublishCollectionPresenter (actions: PublishCollectionActions)(implicit fragmentContextWrapper: ActivityContextWrapper)
  extends Presenter {

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

  def publishCollection(maybeName: Option[String], maybeDescription: Option[String], maybeCategory: Option[NineCardCategory]): Unit =
    (for {
      name <- maybeName
      description <- maybeDescription
      category <- maybeCategory
    } yield {
      Task.fork(createPublishedCollection(name, description, category).value).resolveAsyncUi(
        onPreTask = () => actions.goToPublishCollectionPublishing(),
        onResult = (sharedCollectionId: String) => actions.goToPublishCollectionEnd(sharedCollectionId),
        onException = (ex: Throwable) => {
          actions.showMessagePublishingError ~
            actions.goBackToPublishCollectionInformation(name, description, category)
        })
    }) getOrElse actions.showMessageFormFieldError.run

  private[this] def createPublishedCollection(name: String, description: String, category: NineCardCategory): TaskService[String] =
    for {
      user <- di.userProcess.getUser
      collection <- getCollection
      sharedCollection = CreateSharedCollection(
        description = description,
        author = user.userProfile.name getOrElse (user.email getOrElse resGetString(R.string.defaultUser)),
        name = name,
        packages = collection.cards flatMap (_.packageName),
        category = category,
        icon = collection.icon,
        community = false)
      sharedCollectionId <- di.sharedCollectionsProcess.createSharedCollection(sharedCollection)
      _ <- di.collectionProcess.updateSharedCollection(collection.id, sharedCollectionId)
    } yield sharedCollectionId

  private[this] def getCollection: TaskService[Collection] = statuses.collection map { col =>
    TaskService(Task(Xor.right(col)))
  } getOrElse TaskService(Task(Xor.left(SharedCollectionsExceptions("", None))))

}

case class PublishCollectionStatuses(
  collection: Option[Collection] = None)

trait PublishCollectionActions {

  def initialize(): Ui[Any]

  def goToPublishCollectionInformation(collection: Collection): Ui[Any]

  def goBackToPublishCollectionInformation(name: String, description: String, category: NineCardCategory): Ui[Any]

  def goToPublishCollectionPublishing(): Ui[Any]

  def goToPublishCollectionEnd(sharedCollectionId: String): Ui[Any]

  def showMessageCollectionError: Ui[Any]

  def showMessageFormFieldError: Ui[Any]

  def showMessagePublishingError: Ui[Any]

}