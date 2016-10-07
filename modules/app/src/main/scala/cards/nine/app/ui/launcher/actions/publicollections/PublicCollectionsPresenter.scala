package cards.nine.app.ui.launcher.actions.publicollections

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.collections.tasks.CollectionJobs
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{AppLog, Jobs}
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{Communication, NineCardsCategory}
import cards.nine.process.commons.models.Collection
import cards.nine.process.sharedcollections.models.SharedCollection
import cards.nine.process.sharedcollections.{SharedCollectionsConfigurationException, TopSharedCollection, TypeSharedCollection}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContextWrapper, Ui}

class PublicCollectionsPresenter(actions: PublicCollectionsUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions
  with CollectionJobs {

  protected var statuses = PublicCollectionStatuses(Communication, TopSharedCollection)

  def initialize(): Unit = {
    loadPublicCollections()
    actions.initialize().run
  }

  def loadPublicCollectionsByCategory(category: NineCardsCategory): Unit = {
    statuses = statuses.copy(category = category)
    actions.updateCategory(category).run
    loadPublicCollections()
  }

  def loadPublicCollectionsByTypeSharedCollection(typeSharedCollection: TypeSharedCollection): Unit = {
    statuses = statuses.copy(typeSharedCollection = typeSharedCollection)
    actions.updateTypeCollection(typeSharedCollection).run
    loadPublicCollections()
  }

  def saveSharedCollection(sharedCollection: SharedCollection): Unit = {
    addSharedCollection(sharedCollection).resolveAsyncUi2(
      onResult = (c) => actions.addCollection(c) ~ actions.close(),
      onException = (ex) => actions.showErrorSavingCollectionInScreen())
  }

  def shareCollection(sharedCollection: SharedCollection): Unit =
    di.launcherExecutorProcess.launchShare(resGetString(R.string.shared_collection_url, sharedCollection.id))
      .resolveAsyncUi2(onException = _ => actions.showContactUsError)

  def loadPublicCollections(): Unit = {

    def getSharedCollections(
      category: NineCardsCategory,
      typeSharedCollection: TypeSharedCollection): TaskService[Seq[SharedCollection]] =
      di.sharedCollectionsProcess.getSharedCollectionsByCategory(category, typeSharedCollection)

    getSharedCollections(statuses.category, statuses.typeSharedCollection).resolveAsyncUi2(
      onPreTask = () => actions.showLoading(),
      onResult = (sharedCollections: Seq[SharedCollection]) => {
        if (sharedCollections.isEmpty) {
          actions.showEmptyMessageInScreen()
        } else {
          actions.loadPublicCollections(sharedCollections, saveSharedCollection, shareCollection)
        }
      },
      onException = (e: Throwable) => e match {
        case e: SharedCollectionsConfigurationException =>
          AppLog.invalidConfigurationV2
          actions.showErrorLoadingCollectionInScreen()
        case _ => actions.showErrorLoadingCollectionInScreen()
      })
  }

}

trait PublicCollectionsUiActions {

  def initialize(): Ui[Any]

  def showLoading(): Ui[Any]

  def showErrorLoadingCollectionInScreen(): Ui[Any]

  def showErrorSavingCollectionInScreen(): Ui[Any]

  def showEmptyMessageInScreen(): Ui[Any]

  def showContactUsError: Ui[Any]

  def addCollection(collection: Collection): Ui[Any]

  def loadPublicCollections(
    sharedCollections: Seq[SharedCollection],
    onAddCollection: (SharedCollection) => Unit,
    onShareCollection: (SharedCollection) => Unit): Ui[Any]

  def updateCategory(category: NineCardsCategory): Ui[Any]

  def updateTypeCollection(typeSharedCollection: TypeSharedCollection): Ui[Any]

  def close(): Ui[Any]

}

case class PublicCollectionStatuses(category: NineCardsCategory, typeSharedCollection: TypeSharedCollection)