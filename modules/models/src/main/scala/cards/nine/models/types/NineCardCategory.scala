package cards.nine.models.types

import cards.nine.models.NineCardCategories
import NineCardCategories._

sealed trait NineCardCategory {
  val name: String

  def getStringResource : String = name.toLowerCase
  def getIconResource : String = name.toLowerCase
  def isCustomCategory = NineCardCategory.customCategories contains this
  def isAppCategory = NineCardCategory.appsCategories contains this
  def isGameCategory = NineCardCategory.gamesCategories contains this

  def toAppCategory: NineCardCategory = this match {
    case c if c.isGameCategory => Game
    case c => c
  }
}

case object AllAppsCategory extends NineCardCategory {
  override val name: String = allApps
}

case object AllCategories extends NineCardCategory {
  override val name: String = allCategories
}

case object Custom extends NineCardCategory {
  override val name: String = custom
}

case object Misc extends NineCardCategory {
  override val name: String = misc
}

case object Game extends NineCardCategory {
  override val name: String = game
}

case object BooksAndReference extends NineCardCategory {
  override val name: String = booksAndReference
}

case object Business extends NineCardCategory {
  override val name: String = business
}

case object Comics extends NineCardCategory {
  override val name: String = comics
}

case object Communication extends NineCardCategory {
  override val name: String = communication
}

case object Education extends NineCardCategory {
  override val name: String = education
}

case object Entertainment extends NineCardCategory {
  override val name: String = entertainment
}

case object Finance extends NineCardCategory {
  override val name: String = finance
}

case object HealthAndFitness extends NineCardCategory {
  override val name: String = healthAndFitness
}

case object LibrariesAndDemo extends NineCardCategory {
  override val name: String = librariesAndDemo
}

case object Lifestyle extends NineCardCategory {
  override val name: String = lifestyle
}

case object AppWallpaper extends NineCardCategory {
  override val name: String = appWallpaper
}

case object MediaAndVideo extends NineCardCategory {
  override val name: String = mediaAndVideo
}

case object Medical extends NineCardCategory {
  override val name: String = medical
}

case object MusicAndAudio extends NineCardCategory {
  override val name: String = musicAndAudio
}

case object NewsAndMagazines extends NineCardCategory {
  override val name: String = newsAndMagazines
}

case object Personalization extends NineCardCategory {
  override val name: String = personalization
}

case object Photography extends NineCardCategory {
  override val name: String = photography
}

case object Productivity extends NineCardCategory {
  override val name: String = productivity
}

case object Shopping extends NineCardCategory {
  override val name: String = shopping
}

case object Social extends NineCardCategory {
  override val name: String = social
}

case object Sports extends NineCardCategory {
  override val name: String = sports
}

case object Tools extends NineCardCategory {
  override val name: String = tools
}

case object Transportation extends NineCardCategory {
  override val name: String = transportation
}

case object TravelAndLocal extends NineCardCategory {
  override val name: String = travelAndLocal
}

case object Weather extends NineCardCategory {
  override val name: String = weather
}

case object AppWidgetsCategory extends NineCardCategory {
  override val name: String = appWidgets
}

case object ContactsCategory extends NineCardCategory {
  override val name: String = contacts
}

case object GameAction extends NineCardCategory {
  override val name: String = gameAction
}

case object GameAdventure extends NineCardCategory {
  override val name: String = gameAdventure
}

case object GameRacing extends NineCardCategory {
  override val name: String = gameRacing
}

case object GameCard extends NineCardCategory {
  override val name: String = gameCard
}

case object GameCasino extends NineCardCategory {
  override val name: String = gameCasino
}

case object GameCasual extends NineCardCategory {
  override val name: String = gameCasual
}

case object GameFamily extends NineCardCategory {
  override val name: String = gameFamily
}

case object GameSports extends NineCardCategory {
  override val name: String = gameSports
}

case object GameEducational extends NineCardCategory {
  override val name: String = gameEducational
}

case object GameStrategy extends NineCardCategory {
  override val name: String = gameStrategy
}

case object GameWallpaper extends NineCardCategory {
  override val name: String = gameWallpaper
}

case object GameTrivia extends NineCardCategory {
  override val name: String = gameTrivia
}

case object GameBoard extends NineCardCategory {
  override val name: String = gameBoard
}

case object GameRolePlaying extends NineCardCategory {
  override val name: String = gameRolePlaying
}

case object GameMusic extends NineCardCategory {
  override val name: String = gameMusic
}

case object GameWord extends NineCardCategory {
  override val name: String = gameWord
}

case object GamePuzzle extends NineCardCategory {
  override val name: String = gamePuzzle
}

case object GameArcade extends NineCardCategory {
  override val name: String = gameArcade
}

case object GameSimulation extends NineCardCategory {
  override val name: String = gameSimulation
}

case object GameWidgets extends NineCardCategory {
  override val name: String = gameSimulation
}

object NineCardCategory {

  val customCategories = Seq(AllAppsCategory, AllCategories, Custom, Misc, ContactsCategory)

  val appsCategories = Seq(Game, BooksAndReference, Business, Comics, Communication, Education,
    Entertainment, Finance, HealthAndFitness, LibrariesAndDemo, Lifestyle, AppWallpaper,
    MediaAndVideo, Medical, MusicAndAudio, NewsAndMagazines, Personalization, Photography,
    Productivity, Shopping, Social, Sports, Tools, Transportation, TravelAndLocal, Weather, AppWidgetsCategory)

  val gamesCategories = Seq(GameAction, GameAdventure, GameRacing, GameCard, GameCasino, GameCasual, GameFamily,
    GameSports, GameEducational, GameStrategy, GameWallpaper, GameTrivia, GameBoard, GameRolePlaying, GameMusic,
    GameWord, GamePuzzle, GameArcade, GameSimulation, GameWidgets)

  val allCategories = customCategories ++ gamesCategories ++ appsCategories

  def apply(name: String): NineCardCategory = allCategories find (_.name == name) getOrElse Misc

}

