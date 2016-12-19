package cards.nine.models.types

sealed trait NineCardsCategory {
  val name: String

  def getStringResource: String = name.toLowerCase
  def getIconResource: String   = name.toLowerCase
  def isCustomCategory          = NineCardsCategory.customCategories contains this
  def isAppCategory             = NineCardsCategory.appsCategories contains this
  def isGameCategory            = NineCardsCategory.gamesCategories contains this

  def toAppCategory: NineCardsCategory = this match {
    case c if c.isGameCategory => Game
    case c                     => c
  }
}

case object AllAppsCategory extends NineCardsCategory {
  override val name: String = "ALL_APPS"
}

case object AllCategories extends NineCardsCategory {
  override val name: String = "ALL_CATEGORIES"
}

case object ArtAndDesign extends NineCardsCategory {
  override val name: String = "ART_AND_DESIGN"
}

case object AutoAndVehicles extends NineCardsCategory {
  override val name: String = "AUTO_AND_VEHICLES"
}

case object Beauty extends NineCardsCategory {
  override val name: String = "BEAUTY"
}

case object BooksAndReference extends NineCardsCategory {
  override val name: String = "BOOKS_AND_REFERENCE"
}

case object Business extends NineCardsCategory {
  override val name: String = "BUSINESS"
}

case object Comics extends NineCardsCategory {
  override val name: String = "COMICS"
}

case object Communication extends NineCardsCategory {
  override val name: String = "COMMUNICATION"
}

case object ContactsCategory extends NineCardsCategory {
  override val name: String = "CONTACTS"
}

case object Dating extends NineCardsCategory {
  override val name: String = "DATING"
}

case object Education extends NineCardsCategory {
  override val name: String = "EDUCATION"
}

case object Entertainment extends NineCardsCategory {
  override val name: String = "ENTERTAINMENT"
}

case object Events extends NineCardsCategory {
  override val name: String = "EVENTS"
}

case object Finance extends NineCardsCategory {
  override val name: String = "FINANCE"
}

case object FoodAndDrink extends NineCardsCategory {
  override val name: String = "FOOD_AND_DRINK"
}

case object Game extends NineCardsCategory {
  override val name: String = "GAME"
}

case object HealthAndFitness extends NineCardsCategory {
  override val name: String = "HEALTH_AND_FITNESS"
}

case object HouseAndHome extends NineCardsCategory {
  override val name: String = "HOUSE_AND_HOME"
}

case object LibrariesAndDemo extends NineCardsCategory {
  override val name: String = "LIBRARIES_AND_DEMO"
}

case object Lifestyle extends NineCardsCategory {
  override val name: String = "LIFESTYLE"
}

case object MapsAndNavigation extends NineCardsCategory {
  override val name: String = "MAPS_AND_NAVIGATION"
}

case object Medical extends NineCardsCategory {
  override val name: String = "MEDICAL"
}

case object Misc extends NineCardsCategory {
  override val name: String = "MISC"
}

case object MusicAndAudio extends NineCardsCategory {
  override val name: String = "MUSIC_AND_AUDIO"
}

case object NewsAndMagazines extends NineCardsCategory {
  override val name: String = "NEWS_AND_MAGAZINES"
}

case object Parenting extends NineCardsCategory {
  override val name: String = "PARENTING"
}

case object Personalization extends NineCardsCategory {
  override val name: String = "PERSONALIZATION"
}

case object Photography extends NineCardsCategory {
  override val name: String = "PHOTOGRAPHY"
}

case object Productivity extends NineCardsCategory {
  override val name: String = "PRODUCTIVITY"
}

case object Shopping extends NineCardsCategory {
  override val name: String = "SHOPPING"
}

case object Social extends NineCardsCategory {
  override val name: String = "SOCIAL"
}

case object Sports extends NineCardsCategory {
  override val name: String = "SPORTS"
}

case object Tools extends NineCardsCategory {
  override val name: String = "TOOLS"
}

case object TravelAndLocal extends NineCardsCategory {
  override val name: String = "TRAVEL_AND_LOCAL"
}

case object VideoPlayers extends NineCardsCategory {
  override val name: String = "VIDEO_PLAYERS"
}

case object Weather extends NineCardsCategory {
  override val name: String = "WEATHER"
}

case object GameAction extends NineCardsCategory {
  override val name: String = "GAME_ACTION"
}

case object GameArcade extends NineCardsCategory {
  override val name: String = "GAME_ARCADE"
}

case object GameAdventure extends NineCardsCategory {
  override val name: String = "GAME_ADVENTURE"
}

case object GameBoard extends NineCardsCategory {
  override val name: String = "GAME_BOARD"
}

case object GameCard extends NineCardsCategory {
  override val name: String = "GAME_CARD"
}

case object GameCasino extends NineCardsCategory {
  override val name: String = "GAME_CASINO"
}

case object GameCasual extends NineCardsCategory {
  override val name: String = "GAME_CASUAL"
}

case object GameEducational extends NineCardsCategory {
  override val name: String = "GAME_EDUCATIONAL"
}

case object GameMusic extends NineCardsCategory {
  override val name: String = "GAME_MUSIC"
}

case object GamePuzzle extends NineCardsCategory {
  override val name: String = "GAME_PUZZLE"
}

case object GameRacing extends NineCardsCategory {
  override val name: String = "GAME_RACING"
}

case object GameRolePlaying extends NineCardsCategory {
  override val name: String = "GAME_ROLE_PLAYING"
}

case object GameSimulation extends NineCardsCategory {
  override val name: String = "GAME_SIMULATION"
}

case object GameSports extends NineCardsCategory {
  override val name: String = "GAME_SPORTS"
}

case object GameStrategy extends NineCardsCategory {
  override val name: String = "GAME_STRATEGY"
}

case object GameTrivia extends NineCardsCategory {
  override val name: String = "GAME_TRIVIA"
}

case object GameWord extends NineCardsCategory {
  override val name: String = "GAME_WORD"
}

object NineCardsCategory {

  val customCategories = Seq(AllAppsCategory, AllCategories, Misc, ContactsCategory)

  val appsCategories = Seq(
    ArtAndDesign,
    AutoAndVehicles,
    Beauty,
    BooksAndReference,
    Business,
    Comics,
    Communication,
    Dating,
    Education,
    Entertainment,
    Events,
    Finance,
    FoodAndDrink,
    Game,
    HealthAndFitness,
    HouseAndHome,
    LibrariesAndDemo,
    Lifestyle,
    MapsAndNavigation,
    Medical,
    MusicAndAudio,
    NewsAndMagazines,
    Parenting,
    Personalization,
    Photography,
    Productivity,
    Shopping,
    Social,
    Sports,
    Tools,
    TravelAndLocal,
    VideoPlayers,
    Weather)

  val gamesCategories = Seq(
    GameAction,
    GameAdventure,
    GameArcade,
    GameBoard,
    GameCard,
    GameCasino,
    GameCasual,
    GameEducational,
    GameMusic,
    GamePuzzle,
    GameRacing,
    GameRolePlaying,
    GameSimulation,
    GameSports,
    GameStrategy,
    GameTrivia,
    GameWord)

  val allCategories = customCategories ++ gamesCategories ++ appsCategories

  def apply(name: String): NineCardsCategory = allCategories find (_.name == name) getOrElse Misc

}
