package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.{AddCardRequest, CardException}
import com.fortysevendeg.ninecardslauncher.process.collection.models.Card
import com.fortysevendeg.ninecardslauncher.process.commons.CardType

trait CollectionDetailsTasks
  extends NineCardIntentConversions {

  def createShortcut(collectionId: Int, name: String, shortcutIntent: Intent)(implicit context: ContextSupport, di: Injector):
  ServiceDef2[Seq[Card], CardException] = {
    val addCardRequest = AddCardRequest(
      term = name,
      packageName = None,
      cardType = CardType.shortcut,
      intent = toNineCardIntent(shortcutIntent),
      imagePath = "") // TODO we have to create the image from Intent
    createCards(collectionId, Seq(addCardRequest))
  }

  def createCards(collectionId: Int, cards: Seq[AddCardRequest])(implicit context: ContextSupport, di: Injector):
  ServiceDef2[Seq[Card], CardException] =
    di.collectionProcess.addCards(collectionId, cards)

}
