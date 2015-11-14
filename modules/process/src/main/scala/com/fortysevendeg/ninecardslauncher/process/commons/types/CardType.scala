package com.fortysevendeg.ninecardslauncher.process.types

import com.fortysevendeg.ninecardslauncher.process.commons.CardType._

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

object CardType {

  val cardTypes = Seq(AppCardType, NoInstalledAppCardType, PhoneCardType, EmailCardType, SmsCardType, ShortcutCardType, RecommendedAppCardType)

  def apply(name: String): CardType = cardTypes find (_.name == name) getOrElse
    (throw new IllegalArgumentException(s"$name not found"))

}
