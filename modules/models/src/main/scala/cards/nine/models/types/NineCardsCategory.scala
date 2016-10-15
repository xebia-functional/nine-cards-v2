package cards.nine.models.types

import cards.nine.models.types.NineCardCategories._

sealed trait NineCardsCategory {
  val name: String

  def getStringResource : String = name.toLowerCase
  def getIconResource : String = name.toLowerCase
  def isCustomCategory = NineCardsCategory.customCategories contains this
  def isAppCategory = NineCardsCategory.appsCategories contains this
  def isGameCategory = NineCardsCategory.gamesCategories contains this

  def toAppCategory: NineCardsCategory = this match {
    case c if c.isGameCategory => Game
    case c => c
  }
}

case object AllAppsCategory extends NineCardsCategory {
  override val name: String = allApps
}

case object AllCategories extends NineCardsCategory {
  override val name: String = allCategories
}

case object Custom extends NineCardsCategory {
  override val name: String = custom
}

case object Misc extends NineCardsCategory {
  override val name: String = misc
}

case object Game extends NineCardsCategory {
  override val name: String = game
}

case object BooksAndReference extends NineCardsCategory {
  override val name: String = booksAndReference
}

case object Business extends NineCardsCategory {
  override val name: String = business
}

case object Comics extends NineCardsCategory {
  override val name: String = comics
}

case object Communication extends NineCardsCategory {
  override val name: String = communication
}

case object Education extends NineCardsCategory {
  override val name: String = education
}

case object Entertainment extends NineCardsCategory {
  override val name: String = entertainment
}

case object Finance extends NineCardsCategory {
  override val name: String = finance
}

case object HealthAndFitness extends NineCardsCategory {
  override val name: String = healthAndFitness
}

case object LibrariesAndDemo extends NineCardsCategory {
  override val name: String = librariesAndDemo
}

case object Lifestyle extends NineCardsCategory {
  override val name: String = lifestyle
}

case object AppWallpaper extends NineCardsCategory {
  override val name: String = appWallpaper
}

case object MediaAndVideo extends NineCardsCategory {
  override val name: String = mediaAndVideo
}

case object Medical extends NineCardsCategory {
  override val name: String = medical
}

case object MusicAndAudio extends NineCardsCategory {
  override val name: String = musicAndAudio
}

case object NewsAndMagazines extends NineCardsCategory {
  override val name: String = newsAndMagazines
}

case object Personalization extends NineCardsCategory {
  override val name: String = personalization
}

case object Photography extends NineCardsCategory {
  override val name: String = photography
}

case object Productivity extends NineCardsCategory {
  override val name: String = productivity
}

case object Shopping extends NineCardsCategory {
  override val name: String = shopping
}

case object Social extends NineCardsCategory {
  override val name: String = social
}

case object Sports extends NineCardsCategory {
  override val name: String = sports
}

case object Tools extends NineCardsCategory {
  override val name: String = tools
}

case object Transportation extends NineCardsCategory {
  override val name: String = transportation
}

case object TravelAndLocal extends NineCardsCategory {
  override val name: String = travelAndLocal
}

case object Weather extends NineCardsCategory {
  override val name: String = weather
}

case object AppWidgetsCategory extends NineCardsCategory {
  override val name: String = appWidgets
}

case object ContactsCategory extends NineCardsCategory {
  override val name: String = contacts
}

case object GameAction extends NineCardsCategory {
  override val name: String = gameAction
}

case object GameAdventure extends NineCardsCategory {
  override val name: String = gameAdventure
}

case object GameRacing extends NineCardsCategory {
  override val name: String = gameRacing
}

case object GameCard extends NineCardsCategory {
  override val name: String = gameCard
}

case object GameCasino extends NineCardsCategory {
  override val name: String = gameCasino
}

case object GameCasual extends NineCardsCategory {
  override val name: String = gameCasual
}

case object GameFamily extends NineCardsCategory {
  override val name: String = gameFamily
}

case object GameSports extends NineCardsCategory {
  override val name: String = gameSports
}

case object GameEducational extends NineCardsCategory {
  override val name: String = gameEducational
}

case object GameStrategy extends NineCardsCategory {
  override val name: String = gameStrategy
}

case object GameWallpaper extends NineCardsCategory {
  override val name: String = gameWallpaper
}

case object GameTrivia extends NineCardsCategory {
  override val name: String = gameTrivia
}

case object GameBoard extends NineCardsCategory {
  override val name: String = gameBoard
}

case object GameRolePlaying extends NineCardsCategory {
  override val name: String = gameRolePlaying
}

case object GameMusic extends NineCardsCategory {
  override val name: String = gameMusic
}

case object GameWord extends NineCardsCategory {
  override val name: String = gameWord
}

case object GamePuzzle extends NineCardsCategory {
  override val name: String = gamePuzzle
}

case object GameArcade extends NineCardsCategory {
  override val name: String = gameArcade
}

case object GameSimulation extends NineCardsCategory {
  override val name: String = gameSimulation
}

case object GameWidgets extends NineCardsCategory {
  override val name: String = gameSimulation
}

object NineCardsCategory {

  val customCategories = Seq(AllAppsCategory, AllCategories, Custom, Misc, ContactsCategory)

  val appsCategories = Seq(Game, BooksAndReference, Business, Comics, Communication, Education,
    Entertainment, Finance, HealthAndFitness, LibrariesAndDemo, Lifestyle, AppWallpaper,
    MediaAndVideo, Medical, MusicAndAudio, NewsAndMagazines, Personalization, Photography,
    Productivity, Shopping, Social, Sports, Tools, Transportation, TravelAndLocal, Weather, AppWidgetsCategory)

  val gamesCategories = Seq(GameAction, GameAdventure, GameRacing, GameCard, GameCasino, GameCasual, GameFamily,
    GameSports, GameEducational, GameStrategy, GameWallpaper, GameTrivia, GameBoard, GameRolePlaying, GameMusic,
    GameWord, GamePuzzle, GameArcade, GameSimulation, GameWidgets)

  val allCategories = customCategories ++ gamesCategories ++ appsCategories

  def apply(name: String): NineCardsCategory = allCategories find (_.name == name) getOrElse Misc

}

