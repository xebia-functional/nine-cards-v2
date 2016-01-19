package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.Intent
import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Card
import com.fortysevendeg.ninecardslauncher.process.collection.{AddCardRequest, CardException}
import com.fortysevendeg.ninecardslauncher.process.device.ShortcutException
import com.fortysevendeg.ninecardslauncher.process.commons.types.{ShortcutCardType, CardType}
import rapture.core.Result

import scala.util.Random
import scalaz.concurrent.Task

trait CollectionDetailsTasks
  extends NineCardIntentConversions {

  def createShortcut(collectionId: Int, name: String, shortcutIntent: Intent, bitmap: Option[Bitmap])(implicit context: ContextSupport, di: Injector):
  ServiceDef2[Seq[Card], ShortcutException with CardException] = for {
    path <- saveShortcutIcon(bitmap)
    addCardRequest = AddCardRequest(
      term = name,
      packageName = None,
      cardType = ShortcutCardType,
      intent = toNineCardIntent(shortcutIntent),
      imagePath = path)
    cards <- createCards(collectionId, Seq(addCardRequest))
  } yield cards

  def createCards(collectionId: Int, cards: Seq[AddCardRequest])(implicit context: ContextSupport, di: Injector):
  ServiceDef2[Seq[Card], CardException] =
    di.collectionProcess.addCards(collectionId, cards)

  def removeCard(collectionId: Int, cardId: Int)(implicit context: ContextSupport, di: Injector):
  ServiceDef2[Unit, CardException] =
    di.collectionProcess.deleteCard(collectionId, cardId)

  private[this] def saveShortcutIcon(bitmap: Option[Bitmap])(implicit context: ContextSupport, di: Injector):
  ServiceDef2[String, ShortcutException] = bitmap map { b =>
    di.deviceProcess.saveShortcutIcon(Random.nextString(10), b) // Name is not important here
  } getOrElse Service(Task(Result.answer(""))) // We use a empty string because the UI will generate an image

}
