package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog

import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CollectionOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.SharedCollectionsExceptions
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.CreateSharedCollection
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContextWrapper, Ui}
import rapture.core.{Answer, Errata}

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
      Task.fork(createPublishedCollection(name, description, category).run).resolveAsyncUi(
        onPreTask = () => actions.goToPublishCollectionPublishing(),
        onResult = (shareLink: String) => actions.goToPublishCollectionEnd(shareLink),
        onException = (ex: Throwable) => {
          actions.showMessagePublishingError ~
            actions.goBackToPublishCollectionInformation(name, description, category)
        })
    }) getOrElse actions.showMessageFormFieldError.run

  private[this] def createPublishedCollection(name: String, description: String, category: NineCardCategory): ServiceDef2[String, UserException with SharedCollectionsExceptions with CollectionException] =
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
      createdCollection <- di.sharedCollectionsProcess.createSharedCollection(sharedCollection)
      _ <- di.collectionProcess.updateSharedCollection(collection.id, createdCollection.sharedCollectionId)
    } yield createdCollection.getUrlSharedCollection

  private[this] def getCollection: ServiceDef2[Collection, SharedCollectionsExceptions] = statuses.collection map { col =>
    Service(Task(Answer[Collection, SharedCollectionsExceptions](col)))
  } getOrElse Service(Task(Errata(SharedCollectionsExceptions("", None))))

}

case class PublishCollectionStatuses(
  collection: Option[Collection] = None)

trait PublishCollectionActions {

  def initialize(): Ui[Any]

  def goToPublishCollectionInformation(collection: Collection): Ui[Any]

  def goBackToPublishCollectionInformation(name: String, description: String, category: NineCardCategory): Ui[Any]

  def goToPublishCollectionPublishing(): Ui[Any]

  def goToPublishCollectionEnd(shareLink: String): Ui[Any]

  def showMessageCollectionError: Ui[Any]

  def showMessageFormFieldError: Ui[Any]

  def showMessagePublishingError: Ui[Any]

}