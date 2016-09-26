package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

import scala.util.Random

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

  val cardAdded = 16

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
    styles = Seq(
      ThemeStyle(DockPressedColor, Color.parseColor("#ffd5f2fa")),
      ThemeStyle(CardLayoutBackgroundColor, Color.parseColor("#eeeeee")),
      ThemeStyle(CardTextColor, Color.parseColor("#000000")),
      ThemeStyle(CardBackgroundColor, Color.parseColor("#ffffff")),
      ThemeStyle(CardBackgroundPressedColor, Color.parseColor("#000000")),
      ThemeStyle(CollectionDetailTextTabSelectedColor, Color.parseColor("#ffffff")),
      ThemeStyle(CollectionDetailTextTabDefaultColor, Color.parseColor("#80ffffff")),
      ThemeStyle(DrawerTabsBackgroundColor, Color.parseColor("#16000000")),
      ThemeStyle(DrawerBackgroundColor, Color.parseColor("#ffffff")),
      ThemeStyle(DrawerTextColor, Color.parseColor("#ffffff")),
      ThemeStyle(SearchBackgroundColor, Color.parseColor("#ffffff")),
      ThemeStyle(SearchPressedColor, Color.parseColor("#ff59afdd")),
      ThemeStyle(SearchGoogleColor, Color.parseColor("#a3a3a3")),
      ThemeStyle(SearchIconsColor, Color.parseColor("#646464")),
      ThemeStyle(SearchTextColor, Color.parseColor("#646464")),
      ThemeStyle(DrawerIconColor, Color.parseColor("#000000"))))

  // TODO We should move this colors to theme
  def getIndexColor(index: Int): Int = index match {
    case 0 => R.color.collection_group_1
    case 1 => R.color.collection_group_2
    case 2 => R.color.collection_group_3
    case 3 => R.color.collection_group_4
    case 4 => R.color.collection_group_5
    case 5 => R.color.collection_group_6
    case 6 => R.color.collection_group_7
    case 7 => R.color.collection_group_8
    case _ => R.color.collection_group_9
  }

  def getRandomIndexColor: Int = getIndexColor(Random.nextInt(numSpaces))
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