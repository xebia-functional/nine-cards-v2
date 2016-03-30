package com.fortysevendeg.ninecardslauncher.process.commons.types

import com.fortysevendeg.ninecardslauncher.process.commons.CardTypes._

sealed trait CardType{
  val name: String
}

case object AppCardType extends CardType {
  override val name: String = app
}

case object NoInstalledAppCardType extends CardType {
  override val name: String = noInstalledApp
}

case object PhoneCardType extends CardType {
  override val name: String = phone
}

case object ContactCardType extends CardType {
  override val name: String = contact
}

case object EmailCardType extends CardType {
  override val name: String = email
}

case object SmsCardType extends CardType {
  override val name: String = sms
}

case object ShortcutCardType extends CardType {
  override val name: String = shortcut
}

case object RecommendedAppCardType extends CardType {
  override val name: String = recommendedApp
}

case object NotFoundCardType extends CardType {
  override val name: String = recommendedApp
}

object CardType {

  val cardTypes = Seq(AppCardType, NoInstalledAppCardType, PhoneCardType, ContactCardType, EmailCardType, SmsCardType, ShortcutCardType, RecommendedAppCardType)

  def apply(name: String): CardType = cardTypes find (_.name == name) getOrElse NotFoundCardType

}
