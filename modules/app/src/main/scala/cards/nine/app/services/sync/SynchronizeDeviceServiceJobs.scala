package cards.nine.app.services.sync

import android.app.Service
import android.content.Context
import cards.nine.app.observers.NineCardsObserver._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.action_filters.{SyncAnswerActionFilter, SyncStateActionFilter}
import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{AppCardType, PublishedByMe}
import cards.nine.models.{Collection, User}
import macroid.ContextWrapper
import monix.eval.Task

class SynchronizeDeviceServiceJobs(implicit contextWrapper: ContextWrapper)
    extends SynchronizeDeviceJobs
    with ImplicitsJobExceptions {

  import SynchronizeDeviceService._

  lazy val preferences =
    contextSupport.context.getSharedPreferences(notificationPreferences, Context.MODE_PRIVATE)

  def synchronizeCollections(): TaskService[Unit] = {

    def updateCollections() = {

      def updateCollection(collectionId: Int) = {

        def updateSharedCollection(collection: Collection): TaskService[Option[String]] =
          (collection.publicCollectionStatus, collection.sharedCollectionId) match {
            case (PublishedByMe, Some(sharedCollectionId)) =>
              di.sharedCollectionsProcess
                .updateSharedCollection(
                  sharedCollectionId = sharedCollectionId,
                  name = collection.name,
                  packages =
                    collection.cards.filter(_.cardType == AppCardType).flatMap(_.packageName))
                .map(Option(_))
            case _ => TaskService.right(None)
          }

        for {
          collection <- di.collectionProcess
            .getCollectionById(collectionId)
            .resolveOption(s"Can't find the collection with id $collectionId")
          _ <- updateSharedCollection(collection)
        } yield ()
      }

      val ids            = preferences.getString(collectionIdsKey, "").split(",").toSeq
      val updateServices = ids filterNot (_.isEmpty) map (id => updateCollection(id.toInt).value)
      preferences.edit().remove(collectionIdsKey).apply()

      TaskService {
        Task.gatherUnordered(updateServices) map (_ => Right((): Unit))
      }

    }

    for {
      _ <- di.deviceProcess.synchronizeInstalledApps
      _ <- updateCollections()
    } yield ()
  }

  def cancelAlarm(): TaskService[Unit] = TaskService {
    CatchAll[JobException] {
      contextSupport.getAlarmManager foreach (_.cancel(SynchronizeDeviceService.pendingIntent))
    }
  }

  def sendActualAnswer(): TaskService[Unit] =
    sendBroadCastTask(BroadAction(SyncAnswerActionFilter.action, statuses.currentState))

  def sendActualState(): TaskService[Unit] =
    sendBroadCastTask(BroadAction(SyncStateActionFilter.action, statuses.currentState))

  def connectGoogleApiClient(): TaskService[Unit] = {

    def connectUser(user: User): TaskService[Unit] =
      user.email match {
        case Some(email) =>
          for {
            apiClient <- di.cloudStorageProcess.createCloudStorageClient(email)
            _         <- TaskService.right(statuses = statuses.copy(apiClient = Some(apiClient)))
            _         <- TaskService(CatchAll[JobException](apiClient.connect()))
          } yield ()
        case None =>
          TaskService.left(JobException("User without email, can't sync"))
      }

    for {
      user   <- di.userProcess.getUser
      client <- connectUser(user)
    } yield ()
  }

  def finalizeService(service: Service): TaskService[Unit] = {

    def secure(f: => Unit): TaskService[Unit] =
      TaskService(CatchAll[JobException](f)).resolveLeftTo((): Unit)

    for {
      _ <- statuses.apiClient map (client =>
                                     secure(client.disconnect())) getOrElse TaskService.empty
      _ <- secure(service.stopForeground(true))
      _ <- secure(service.stopSelf())
    } yield ()
  }

}
