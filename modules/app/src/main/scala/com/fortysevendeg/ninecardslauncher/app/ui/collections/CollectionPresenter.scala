package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.Context
import android.support.v7.widget.RecyclerView.ViewHolder
import com.fortysevendeg.ninecardslauncher.app.analytics.{NoValue, RemovedInCollectionAction, RemovedInCollectionValue, _}
import com.fortysevendeg.ninecardslauncher.app.commons.{ActivityContextSupportProvider, Conversions}
import com.fortysevendeg.ninecardslauncher.app.permissions.PermissionChecker
import com.fortysevendeg.ninecardslauncher.app.permissions.PermissionChecker.CallPhone
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{Jobs, RequestCodes}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppCardType, PhoneCardType}
import macroid.{ActivityContextWrapper, Ui}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.intents.LauncherExecutorProcessPermissionException

import scalaz.concurrent.Task

case class CollectionPresenter(
  animateCards: Boolean,
  maybeCollection: Option[Collection],
  actions: CollectionUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions
  with AnalyticDispatcher
  with ActivityContextSupportProvider { self =>

  val permissionChecker = new PermissionChecker

  var statuses = CollectionPresenterStatuses()

  override def getApplicationContext: Context = contextWrapper.application

  def initialize(sType: ScrollType): Unit = {
    val canScroll = maybeCollection exists (_.cards.length > numSpaces)
    (actions.updateStatus(canScroll, sType) ~
      (maybeCollection map { collection =>
        actions.initialize(animateCards, collection)
      } getOrElse actions.showEmptyCollection())).run
  }

  def startReorderCards(holder: ViewHolder): Unit = if (!actions.isPulling) actions.startReorder(holder).run

  def reorderCard(collectionId: Int, cardId: Int, position: Int): Unit = {
    Task.fork(di.collectionProcess.reorderCard(collectionId, cardId, position).value).resolveAsyncUi(
      onResult = (_) => actions.reloadCards()
    )
  }

  def moveToCollection(card: Card): Unit =
    Task.fork(di.collectionProcess.getCollections.value).resolveAsyncUi(
      onResult = (collections) => actions.moveToCollection(collections, card),
      onException = (_) => actions.showContactUsError())

  def editCard(collectionId: Int, cardId: Int, cardName: String): Unit = actions.editCard(collectionId, cardId, cardName)

  def addCards(cards: Seq[Card]): Unit = {
    cards foreach (card => trackCard(card, AddedToCollectionAction))
    actions.addCards(cards).run
  }

  def removeCard(card: Card): Unit = {
    trackCard(card, RemovedInCollectionAction)
    actions.removeCard(card).run
  }

  def reloadCards(cards: Seq[Card]): Unit = actions.reloadCards(cards).run

  def bindAnimatedAdapter(): Unit = maybeCollection foreach { collection =>
    actions.bindAnimatedAdapter(animateCards, collection).run
  }

  def saveEditedCard(collectionId: Int, cardId: Int, cardName: Option[String]): Unit = {

    def saveCard(collectionId: Int, cardId: Int, name: String) =
      for {
        card <- di.collectionProcess.editCard(collectionId, cardId, name)
      } yield card

    cardName match {
      case Some(name) if name.length > 0 =>
          Task.fork(saveCard(collectionId, cardId, name).value).resolveAsyncUi(
            onResult = (card) => actions.reloadCard(card),
            onException = (_) => actions.showContactUsError())
      case _ => actions.showMessageFormFieldError.run
    }
  }

  def showData(): Unit = maybeCollection foreach (c => actions.showData(c.cards.isEmpty).run)

  def launchCard(card : Card): Unit = di.launcherExecutorProcess.execute(card.intent).resolveAsync(
    onException = (throwable: Throwable) => throwable match {
      case e: LauncherExecutorProcessPermissionException if card.cardType == PhoneCardType =>
        statuses = statuses.copy(lastPhone = card.intent.extractPhone())
        actions.askForPhoneCallPermission(RequestCodes.phoneCallPermission).run
      case _ => actions.showContactUsError().run
    }
  )

  def requestPermissionsResult(
    requestCode: Int,
    permissions: Array[String],
    grantResults: Array[Int]): Unit =
    if (requestCode == RequestCodes.phoneCallPermission) {
      val result = permissionChecker.readPermissionRequestResult(permissions, grantResults)
      if (result.exists(_.hasPermission(CallPhone))) {
        statuses.lastPhone foreach { phone =>
          statuses = statuses.copy(lastPhone = None)
          di.launcherExecutorProcess.execute(phoneToNineCardIntent(phone)).resolveAsync(
            onException = _ => actions.showContactUsError().run)
        }
      } else {
        statuses.lastPhone foreach { phone =>
          statuses = statuses.copy(lastPhone = None)
          di.launcherExecutorProcess.launchDial(Some(phone)).resolveAsync(
            onException = _ => actions.showContactUsError().run)
        }
        actions.showNoPhoneCallPermissionError().run
      }
    }

  private[this] def trackCard(card: Card, action: Action): Unit = card.cardType match {
    case AppCardType =>
      for {
        collection <- actions.getCurrentCollection
        packageName <- card.packageName
        category <- collection.appsCategory map (c => Option(AppCategory(c))) getOrElse {
          collection.moment flatMap (_.momentType) map MomentCategory
        }
      } yield {
        self !>>
          TrackEvent(
            screen = CollectionDetailScreen,
            category = category,
            action = action,
            label = Some(ProvideLabel(packageName)),
            value = Some(action match {
              case OpenCardAction => OpenAppFromCollectionValue
              case AddedToCollectionAction => AddedToCollectionValue
              case RemovedInCollectionAction => RemovedInCollectionValue
              case _ => NoValue
            }))
      }
    case _ =>
  }

}

case class CollectionPresenterStatuses(
  lastPhone: Option[String] = None)

trait CollectionUiActions {

  def initialize(animateCards: Boolean, collection: Collection): Ui[Any]

  def updateStatus(canScroll: Boolean, sType: ScrollType): Ui[Any]

  def startReorder(holder: ViewHolder): Ui[Any]

  def reloadCards(): Ui[Any]

  def bindAnimatedAdapter(animateCards: Boolean, collection: Collection): Ui[Any]

  def showMessageNotImplemented(): Ui[Any]

  def showContactUsError(): Ui[Any]

  def showMessageFormFieldError: Ui[Any]

  def showNoPhoneCallPermissionError(): Ui[Any]

  def showEmptyCollection(): Ui[Any]

  def moveToCollection(collections: Seq[Collection], card: Card): Ui[Any]

  def editCard(collectionId: Int, cardId: Int, cardName: String): Unit

  def addCards(cards: Seq[Card]): Ui[Any]

  def removeCard(card: Card): Ui[Any]

  def reloadCard(card: Card): Ui[Any]

  def reloadCards(cards: Seq[Card]): Ui[Any]

  def showData(emptyCollection: Boolean): Ui[Any]

  def askForPhoneCallPermission(requestCode: Int): Ui[Any]

  def isPulling: Boolean

  def getCurrentCollection: Option[Collection]

}