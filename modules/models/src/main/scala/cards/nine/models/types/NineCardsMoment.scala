package cards.nine.models.types

sealed trait NineCardsMoment{
  val name: String
  val isDefault: Boolean = false

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

case object OutAndAboutMoment extends NineCardsMoment {
  override val name: String = "OUT_AND_ABOUT"
  override val isDefault: Boolean = true
}

object NineCardsMoment {

  val activityMoments = Seq(CarMoment, RunningMoment, BikeMoment)

  val hourlyMoments = Seq(HomeMorningMoment, WorkMoment, HomeNightMoment, StudyMoment)

  val defaultMoment = OutAndAboutMoment

  val moments = hourlyMoments ++ Seq(MusicMoment, defaultMoment) ++ activityMoments

  def apply(name: String): NineCardsMoment = moments find (_.name == name) getOrElse OutAndAboutMoment

  def apply(maybeName: Option[String]): NineCardsMoment = maybeName map apply getOrElse OutAndAboutMoment

}
