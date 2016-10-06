package cards.nine.models.types

import cards.nine.models.types.NineCardsMoments._

sealed trait NineCardsMoment{
  val name: String

  def getIconResource : String = name.toLowerCase
  def getStringResource : String = name.toLowerCase
}

case object HomeMorningMoment extends NineCardsMoment {
  override val name: String = "HOME"
}

case object WorkMoment extends NineCardsMoment {
  override val name: String = "WORK"
}

case object HomeNightMoment extends NineCardsMoment {
  override val name: String = "NIGHT"
}

case object StudyMoment extends NineCardsMoment {
  override val name: String = "STUDY"
}

case object MusicMoment extends NineCardsMoment {
  override val name: String = "MUSIC"
}

case object CarMoment extends NineCardsMoment {
  override val name: String = "CAR"
}

case object RunningMoment extends NineCardsMoment {
  override val name: String = "RUNNING"
}

case object BikeMoment extends NineCardsMoment {
  override val name: String = "BIKE"
}

case object WalkMoment extends NineCardsMoment {
  override val name: String = "WALK"
}

object NineCardsMoment {

  val moments = Seq(
    HomeMorningMoment,
    WorkMoment,
    HomeNightMoment,
    StudyMoment,
    MusicMoment,
    CarMoment,
    RunningMoment,
    BikeMoment,
    WalkMoment)

  def apply(name: String): NineCardsMoment = moments find (_.name == name) getOrElse WalkMoment

}
