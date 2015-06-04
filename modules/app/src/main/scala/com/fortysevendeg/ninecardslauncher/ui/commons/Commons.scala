package com.fortysevendeg.ninecardslauncher.ui.commons

import android.graphics.Color
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

object Constants {

  val NumSpaces = 9

  val NumInLine = 3

  val MinVelocity: Int = 250

  val MaxRatioVelocity: Int = 3000

  val MaxVelocity: Int = 700

  val SpaceVelocity: Int = MaxVelocity - MinVelocity

}

object NineCardsMoments {

  val HomeMorning: String = "home_morning"

  val Work: String = "work"

  val HomeNight: String = "home_night"

  val Transit: String = "transit"

}

object NineCategories {
  val AllApps: String = "ALL_APPS"
  val AllCategories: String = "ALL_CATEGORIES"
  val Custom: String = "CUSTOM"
  val Misc: String = "MISC"
  val Game: String = "GAME"
  val BooksAndReference: String = "BOOKS_AND_REFERENCE"
  val Business: String = "BUSINESS"
  val Comics: String = "COMICS"
  val Communication: String = "COMMUNICATION"
  val Education: String = "EDUCATION"
  val Entertainment: String = "ENTERTAINMENT"
  val Finance: String = "FINANCE"
  val HealthAndFitness: String = "HEALTH_AND_FITNESS"
  val LibrariesAndDemo: String = "LIBRARIES_AND_DEMO"
  val Lifestyle: String = "LIFESTYLE"
  val AppWallpaper: String = "APP_WALLPAPER"
  val MediaAndVideo: String = "MEDIA_AND_VIDEO"
  val Medical: String = "MEDICAL"
  val MusicAndAudio: String = "MUSIC_AND_AUDIO"
  val NewsAndMagazines: String = "NEWS_AND_MAGAZINES"
  val Personalization: String = "PERSONALIZATION"
  val Photography: String = "PHOTOGRAPHY"
  val Productivity: String = "PRODUCTIVITY"
  val Shopping: String = "SHOPPING"
  val Social: String = "SOCIAL"
  val Sports: String = "SPORTS"
  val Tools: String = "TOOLS"
  val Transportation: String = "TRANSPORTATION"
  val TravelAndLocal: String = "TRAVEL_AND_LOCAL"
  val Weather: String = "WEATHER"
  val AppWidgets: String = "APP_WIDGETS"
  val GameAction: String = "GAME_ACTION"
  val GameAdventure: String = "GAME_ADVENTURE"
  val GameRacing: String = "GAME_RACING"
  val GameCard: String = "GAME_CARD"
  val GameCasino: String = "GAME_CASINO"
  val GameCasual: String = "GAME_CASUAL"
  val GameFamily: String = "GAME_FAMILY"
  val GameSports: String = "GAME_SPORTS"
  val GameEducational: String = "GAME_EDUCATIONAL"
  val GameStrategy: String = "GAME_STRATEGY"
  val GameWallpaper: String = "GAME_WALLPAPER"
  val GameTrivia: String = "GAME_TRIVIA"
  val GameBoard: String = "GAME_BOARD"
  val GameRolePlaying: String = "GAME_ROLE_PLAYING"
  val GameMusic: String = "GAME_MUSIC"
  val GameWord: String = "GAME_WORD"
  val GamePuzzle: String = "GAME_PUZZLE"
  val GameArcade: String = "GAME_ARCADE"
  val GameSimulation: String = "GAME_SIMULATION"
  val GameWidgets: String = "GAME_WIDGETS"
}

object CollectionType {
  val Apps = "APPS"
  val Contacts = "CONTACTS"
  val HomeMorning = "HOME_MORNING"
  val HomeNight = "HOME_NIGHT"
  val Work = "WORK"
  val Transit = "TRANSIT"
  val Free = "FREE"
  val Empty = "EMPTY"
  val Discard = "DISCARD"
}

object CardType {
  val App = "APP"
  val Phone = "PHONE"
  val Email = "EMAIL"
  val Sms = "SMS"
  val Empty = "EMPTY"
  val Discard = "DISCARD"
  val Shortcut = "SHORTCUT"
  val RecommendedApp = "RECOMMENDED_APP"
  val PromotedApp = "PROMOTED_APP"
  val Sponsored = "SPONSORED"
  val GetItFree = "GET_IT_FREE"
  val GoPro = "GO_PRO"
  val NineCardsExtension = "NINE_CARDS_EXTENSION"
}

object ImageResourceNamed {

  def iconCollectionWorkspace(category: String)(implicit context: ContextWrapper): Int =
    resGetDrawableIdentifier(s"icon_collection_$category") getOrElse R.drawable.icon_collection_default

  def iconCollectionDetail(category: String)(implicit context: ContextWrapper): Int =
    resGetDrawableIdentifier(s"icon_collection_${category}_detail") getOrElse R.drawable.icon_collection_default_detail

}

object ActivityResult {

  val Wizard = 1

}

object GoogleServicesConstants {

  val AccountType = "com.google"

  val AndroidId = "android_id"

  val ContentGServices = "content://com.google.android.gsf.gservices"

}

object AppUtils {
  def getUniqueId: Int = (System.currentTimeMillis & 0xfffffff).toInt
}

object ColorsUtils {

  def getColorDark(color: Int, ratio: Float = 0.1f) = {
    var colorHsv = Array(0f, 0f, 0f)
    Color.colorToHSV(color, colorHsv)
    colorHsv.update(2, math.max(colorHsv(2) - ratio, 0))
    Color.HSVToColor(colorHsv)
  }

  def setAlpha(color: Int, alpha: Float): Int = Color.argb((255 * alpha).toInt, Color.red(color), Color.green(color), Color.blue(color))

  def interpolateColors(fraction: Float, startValue: Int, endValue: Int): Int = {
    val startInt: Int = startValue
    val startA: Int = (startInt >> 24) & 0xff
    val startR: Int = (startInt >> 16) & 0xff
    val startG: Int = (startInt >> 8) & 0xff
    val startB: Int = startInt & 0xff
    val endInt: Int = endValue
    val endA: Int = (endInt >> 24) & 0xff
    val endR: Int = (endInt >> 16) & 0xff
    val endG: Int = (endInt >> 8) & 0xff
    val endB: Int = endInt & 0xff
    ((startA + (fraction * (endA - startA)).toInt) << 24) |
      ((startR + (fraction * (endR - startR)).toInt) << 16) |
      (startG + (fraction * (endG - startG)).toInt) << 8 |
      (startB + (fraction * (endB - startB)).toInt)
  }

}

object AnimationsUtils {

  def calculateDurationByVelocity(velocity: Float, defaultVelocity: Int): Int = {
    import Constants._
    velocity match {
      case 0 => defaultVelocity
      case _ => (SpaceVelocity - ((math.min(math.abs(velocity), MaxRatioVelocity) * SpaceVelocity) / MaxRatioVelocity) + MinVelocity).toInt
    }
  }

}