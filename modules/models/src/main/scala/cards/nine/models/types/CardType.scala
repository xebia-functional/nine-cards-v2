package cards.nine.models.types

sealed trait CardType {
  val name: String
  def isContact: Boolean =
    this == PhoneCardType ||
      this == EmailCardType ||
      this == ContactCardType ||
      this == SmsCardType
}

case object AppCardType extends CardType {
  override val name: String = "APP"
}

case object NoInstalledAppCardType extends CardType {
  override val name: String = "NO_INSTALLED_APP"
}

case object PhoneCardType extends CardType {
  override val name: String = "PHONE"
}

case object ContactCardType extends CardType {
  override val name: String = "CONTACT"
}

case object EmailCardType extends CardType {
  override val name: String = "EMAIL"
}

case object SmsCardType extends CardType {
  override val name: String = "SMS"
}

case object ShortcutCardType extends CardType {
  override val name: String = "SHORTCUT"
}

case object RecommendedAppCardType extends CardType {
  override val name: String = "RECOMMENDED_APP"
}

case object NotFoundCardType extends CardType {
  override val name: String = "RECOMMENDED_APP"
}

object CardType {

  val cardTypes = Seq(
    AppCardType,
    NoInstalledAppCardType,
    PhoneCardType,
    ContactCardType,
    EmailCardType,
    SmsCardType,
    ShortcutCardType,
    RecommendedAppCardType)

  def apply(name: String): CardType = cardTypes find (_.name == name) getOrElse NotFoundCardType

}
