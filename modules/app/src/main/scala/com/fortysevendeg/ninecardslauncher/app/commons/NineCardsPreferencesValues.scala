package com.fortysevendeg.ninecardslauncher.app.commons

// Animations Values

sealed trait SpeedAnimationValue {
  val value: String
}

case object SlowAnimation extends SpeedAnimationValue {
  override val value: String = "0"
}

case object NormalAnimation extends SpeedAnimationValue {
  override val value: String = "1"
}

case object FastAnimation extends SpeedAnimationValue {
  override val value: String = "2"
}

object SpeedAnimationValue {

  val values = Seq(SlowAnimation, NormalAnimation, FastAnimation)

  def apply(value: String): SpeedAnimationValue =
    values find (_.value == value) getOrElse NormalAnimation

}

sealed trait CollectionOpeningValue {
  val value: String
}

case object CircleOpeningCollectionAnimation extends CollectionOpeningValue {
  override val value: String = "0"
}

case object NoAnimationOpeningCollectionAnimation extends CollectionOpeningValue {
  override val value: String = "1"
}

object CollectionOpeningValue {

  val values = Seq(CircleOpeningCollectionAnimation, NoAnimationOpeningCollectionAnimation)

  def apply(value: String): CollectionOpeningValue =
    values find (_.value == value) getOrElse CircleOpeningCollectionAnimation

}

sealed trait WorkspaceAnimationValue {
  val value: String
}

case object HorizontalSlideWorkspaceAnimation extends WorkspaceAnimationValue {
  override val value: String = "0"
}

case object AppearBehindWorkspaceAnimation extends WorkspaceAnimationValue {
  override val value: String = "1"
}

object WorkspaceAnimationValue {

  val values = Seq(HorizontalSlideWorkspaceAnimation, AppearBehindWorkspaceAnimation)

  def apply(value: String): WorkspaceAnimationValue =
    values find (_.value == value) getOrElse HorizontalSlideWorkspaceAnimation

}

// App Drawer Values

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

// Theme Values

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