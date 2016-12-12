package cards.nine.app.ui.commons

import android.graphics.Color
import cards.nine.models._
import cards.nine.models.types.theme._
import com.fortysevendeg.ninecardslauncher.R

object Constants {

  val numSpaces = 9

  val numInLine = 3

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

object SyncDeviceState {
  val stateSyncing = "sync-device-state-syncing"
  val stateSuccess = "sync-device-state-success"
  val stateFailure = "sync-device-state-failure"
}

object AppUtils {

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

object Team {

  val team = Seq(
    ("Ana", R.drawable.ana),
    ("Domin", R.drawable.domin),
    ("Fede", R.drawable.fede),
    ("Javi" , R.drawable.javi_pacheco),
    ("Jorge", R.drawable.jorge_galindo),
    ("Paco", R.drawable.paco),
    ("Raúl", R.drawable.raul_raja),
    ("Diego", R.drawable.diego),
    ("Isra", R.drawable.isra),
    ("Aaron", R.drawable.aaron),
    ("Ale", R.drawable.ale),
    ("Andy", R.drawable.andy),
    ("Javi", R.drawable.javi_siloniz),
    ("Benjy", R.drawable.benjy),
    ("Fran", R.drawable.fran),
    ("John", R.drawable.john),
    ("Juan", R.drawable.juan),
    ("Juan Pedro", R.drawable.juan_pedro),
    ("Justin", R.drawable.justin),
    ("Nick", R.drawable.nick),
    ("Maureen", R.drawable.maureen),
    ("Noel", R.drawable.noel),
    ("Rafa", R.drawable.rafa))

}
