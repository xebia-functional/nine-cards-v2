package cards.nine.app.ui.commons

import android.graphics.Color
import android.view.View
import cards.nine.app.ui.commons.Constants._
import cards.nine.process.theme.models._

object Constants {

  val numSpaces = 9

  val numInLine = 3

  val minVelocity: Int = 250

  val maxRatioVelocity: Int = 3000

  val maxVelocity: Int = 700

  val spaceVelocity: Int = maxVelocity - minVelocity

}

object RequestCodes {

  val shortcutAdded = 1

  val selectInfoContact = 2

  val selectInfoIcon = 3

  val selectInfoColor = 4

  val resolveGooglePlayConnection = 5

  val resolveConnectedUser = 6

  val goToProfile = 7

  val goToCollectionDetails = 8

  val goToPreferences = 9

  val goToWidgets = 10

  val goToConfigureWidgets = 11

  val selectInfoWifi = 12

  val contactsPermission = 13

  val callLogPermission = 14

  val phoneCallPermission = 15

  val locationPermission = 16

  val wizardPermissions = 17

  val selectAccount = 18

}

object ResultCodes {

  val logoutSuccessful = 10

  val preferencesChanged = 20

}

object ResultData {

  val preferencesResultData = "preferences-result-data"

}

object WizardState {
  val stateCreatingCollections = "wizard-state-creating-collections"
  val stateSuccess = "wizard-state-success"
  val stateFailure = "wizard-state-failure"
  val stateCloudIdNotSend = "wizard-state-cloud-id-not-send"
  val stateUserCloudIdPresent = "wizard-state-user-cloud-id-present"
}

object SyncDeviceState {
  val stateSyncing = "sync-device-state-syncing"
  val stateSuccess = "sync-device-state-success"
  val stateFailure = "sync-device-state-failure"
}

object AppUtils {
  def getUniqueId: Int = (System.currentTimeMillis & 0xfffffff).toInt

  def getDefaultTheme = NineCardsTheme(
    name = "light",
    parent = ThemeLight,
    styles = Seq(
      ThemeStyle(PrimaryColor, Color.parseColor("#3F51B5")),
      ThemeStyle(SearchBackgroundColor, Color.parseColor("#ffffff")),
      ThemeStyle(SearchPressedColor, Color.parseColor("#ff59afdd")),
      ThemeStyle(SearchGoogleColor, Color.parseColor("#a3a3a3")),
      ThemeStyle(SearchIconsColor, Color.parseColor("#646464")),
      ThemeStyle(SearchTextColor, Color.parseColor("#646464")),
      ThemeStyle(DrawerTabsBackgroundColor, Color.parseColor("#16000000")),
      ThemeStyle(DrawerBackgroundColor, Color.parseColor("#ffffff")),
      ThemeStyle(DrawerTextColor, Color.parseColor("#cc000000")),
      ThemeStyle(DrawerIconColor, Color.parseColor("#99000000")),
      ThemeStyle(DockPressedColor, Color.parseColor("#ffd5f2fa")),
      ThemeStyle(CardLayoutBackgroundColor, Color.parseColor("#eeeeee")),
      ThemeStyle(CardTextColor, Color.parseColor("#000000")),
      ThemeStyle(CardBackgroundColor, Color.parseColor("#ffffff")),
      ThemeStyle(CardBackgroundPressedColor, Color.parseColor("#000000")),
      ThemeStyle(CollectionDetailTextTabSelectedColor, Color.parseColor("#ffffff")),
      ThemeStyle(CollectionDetailTextTabDefaultColor, Color.parseColor("#80ffffff"))),
    themeColors = ThemeColors(Color.parseColor("#FF9800"), Seq.empty))
}

object AnimationsUtils {

  def calculateDurationByVelocity(velocity: Float, defaultVelocity: Int): Int = {
    velocity match {
      case 0 => defaultVelocity
      case _ => (spaceVelocity - ((math.min(math.abs(velocity), maxRatioVelocity) * spaceVelocity) / maxRatioVelocity) + minVelocity).toInt
    }
  }

}

@deprecated("We should use ViewOps")
object PositionsUtils {

  def calculateAnchorViewPosition(view: View): (Int, Int) = {
    val loc = new Array[Int](2)
    view.getLocationOnScreen(loc)
    (loc(0), loc(1))
  }

  def projectionScreenPositionInView(view: View, x: Int, y: Int): (Int, Int) = {
    val loc = new Array[Int](2)
    view.getLocationOnScreen(loc)
    (x - loc(0), y - loc(1))
  }

}