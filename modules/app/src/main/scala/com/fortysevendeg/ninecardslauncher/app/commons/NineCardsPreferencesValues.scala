package com.fortysevendeg.ninecardslauncher.app.commons

sealed trait AppDrawerLongPressActionValue {
  val value: String
}

case object AppDrawerLongPressActionOpenKeyboard extends AppDrawerLongPressActionValue {
  override val value: String = "0"
}

case object AppDrawerLongPressActionOpenContacts extends AppDrawerLongPressActionValue {
  override val value: String = "1"
}

object AppDrawerLongPressActionValue {

  val values = Seq(AppDrawerLongPressActionOpenKeyboard, AppDrawerLongPressActionOpenContacts)

  def apply(value: String): AppDrawerLongPressActionValue =
    values find (_.value == value) getOrElse AppDrawerLongPressActionOpenKeyboard

}

sealed trait AppDrawerAnimationValue {
  val value: String
}

case object AppDrawerAnimationCircle extends AppDrawerAnimationValue {
  override val value: String = "0"
}

case object AppDrawerAnimationFade extends AppDrawerAnimationValue {
  override val value: String = "1"
}

object AppDrawerAnimationValue {

  val values = Seq(AppDrawerAnimationCircle, AppDrawerAnimationFade)

  def apply(value: String): AppDrawerAnimationValue =
    values find (_.value == value) getOrElse AppDrawerAnimationCircle

}

sealed trait ThemeValue {
  val value: String
}

case object ThemeDark extends ThemeValue {
  override val value: String = "0"
}

case object ThemeLight extends ThemeValue {
  override val value: String = "1"
}

object ThemeValue {

  val values = Seq(ThemeDark, ThemeLight)

  def apply(value: String): ThemeValue =
    values find (_.value == value) getOrElse ThemeLight

}