package cards.nine.app.ui.preferences.commons

import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop

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
  val isSupported: Boolean
}

case object CircleOpeningCollectionAnimation extends CollectionOpeningValue {
  override val value: String = "0"
  override val isSupported: Boolean = Lollipop.ifSupportedThen(()).isDefined
}

case object NoAnimationOpeningCollectionAnimation extends CollectionOpeningValue {
  override val value: String = "1"
  override val isSupported: Boolean = true
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
  val isSupported: Boolean
}

case object AppDrawerAnimationCircle extends AppDrawerAnimationValue {
  override val value: String = "0"
  override val isSupported: Boolean = Lollipop.ifSupportedThen(()).isDefined
}

case object AppDrawerAnimationFade extends AppDrawerAnimationValue {
  override val value: String = "1"
  override val isSupported: Boolean = true
}

object AppDrawerAnimationValue {

  val values = Seq(AppDrawerAnimationCircle, AppDrawerAnimationFade)

  def apply(value: String): AppDrawerAnimationValue =
    values find (_.value == value) getOrElse AppDrawerAnimationCircle

}

// Look and Feel Values

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

sealed trait GoogleLogoValue {
  val value: String
}

case object GoogleLogoTheme extends GoogleLogoValue {
  override val value: String = "0"
}

case object GoogleLogoColoured extends GoogleLogoValue {
  override val value: String = "1"
}

object GoogleLogoValue {

  val values = Seq(GoogleLogoTheme, GoogleLogoColoured)

  def apply(value: String): GoogleLogoValue =
    values find (_.value == value) getOrElse GoogleLogoTheme

}

sealed trait FontSizeValue {
  val value: String
}

case object FontSizeSmall extends FontSizeValue {
  override val value: String = "0"
}

case object FontSizeMedium extends FontSizeValue {
  override val value: String = "1"
}

case object FontSizeLarge extends FontSizeValue {
  override val value: String = "2"
}

object FontSizeValue {

  val values = Seq(FontSizeSmall, FontSizeMedium, FontSizeLarge)

  def apply(value: String): FontSizeValue =
    values find (_.value == value) getOrElse FontSizeMedium

}

sealed trait IconsSizeValue {
  val value: String
}

case object IconsSizeSmall extends IconsSizeValue {
  override val value: String = "0"
}

case object IconsSizeMedium extends IconsSizeValue {
  override val value: String = "1"
}

case object IconsSizeLarge extends IconsSizeValue {
  override val value: String = "2"
}

object IconsSizeValue {

  val values = Seq(IconsSizeSmall, IconsSizeMedium, IconsSizeLarge)

  def apply(value: String): IconsSizeValue =
    values find (_.value == value) getOrElse IconsSizeMedium

}

sealed trait CardPaddingValue {
  val value: String
}

case object CardPaddingSmall extends CardPaddingValue {
  override val value: String = "0"
}

case object CardPaddingMedium extends CardPaddingValue {
  override val value: String = "1"
}

case object CardPaddingLarge extends CardPaddingValue {
  override val value: String = "2"
}

object CardPaddingValue {

  val values = Seq(CardPaddingSmall, CardPaddingMedium, CardPaddingLarge)

  def apply(value: String): CardPaddingValue =
    values find (_.value == value) getOrElse CardPaddingMedium

}