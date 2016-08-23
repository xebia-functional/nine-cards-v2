package com.fortysevendeg.ninecardslauncher.app.commons

sealed trait AppDrawerLongPressActionValue {
  val name: String
}

case object AppDrawerLongPressActionOpenKeyboard extends AppDrawerLongPressActionValue {
  override val name: String = "0"
}

case object AppDrawerLongPressActionOpenContacts extends AppDrawerLongPressActionValue {
  override val name: String = "1"
}

object AppDrawerLongPressActionValue {

  def apply(name: String): AppDrawerLongPressActionValue = name match {
    case "0" => AppDrawerLongPressActionOpenKeyboard
    case "1" => AppDrawerLongPressActionOpenContacts
  }

}

sealed trait AppDrawerAnimationValue {
  val name: String
}

case object AppDrawerAnimationCircle extends AppDrawerAnimationValue {
  override val name: String = "0"
}

case object AppDrawerAnimationFade extends AppDrawerAnimationValue {
  override val name: String = "1"
}

object AppDrawerAnimationValue {

  def apply(name: String): AppDrawerAnimationValue = name match {
    case "0" => AppDrawerAnimationCircle
    case "1" => AppDrawerAnimationFade
  }

}