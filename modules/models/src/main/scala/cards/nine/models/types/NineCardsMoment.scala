package cards.nine.models.types

import cards.nine.models.{CloudStorageMoment, Moment, MomentData}

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

case object SportsMoment extends NineCardsMoment {
  override val name: String = "SPORTS"
}

case object OutAndAboutMoment extends NineCardsMoment {
  override val name: String = "OUT_AND_ABOUT"
  override val isDefault: Boolean = true
}

case class UnknownMoment(name: String) extends NineCardsMoment

object NineCardsMoment {

  val activityMoments = Seq(CarMoment)

  val hourlyMoments = Seq(HomeMorningMoment, WorkMoment, HomeNightMoment, StudyMoment, SportsMoment)

  val defaultMoment = OutAndAboutMoment

  val moments = hourlyMoments ++ Seq(MusicMoment, defaultMoment) ++ activityMoments

  def apply(name: String): NineCardsMoment = moments find (_.name == name) getOrElse UnknownMoment(name)

  def apply(maybeName: Option[String]): NineCardsMoment = maybeName map apply getOrElse UnknownMoment(maybeName.getOrElse(""))

}

object LegacyMoments {

  val walkMoment = "WALK"

  val runningMoment = "RUNNING"

  val bikeMoment = "BIKE"

  private[this] val pfFix: PartialFunction[Moment, Moment] = {
    case moment if moment.momentType.name == walkMoment => moment.copy(momentType = OutAndAboutMoment)
    case moment if moment.momentType.name == runningMoment => moment.copy(momentType = SportsMoment)
    case moment if moment.momentType.name != bikeMoment => moment
  }

  def fixLegacyMomentSeq(moments: Seq[Moment]): Seq[Moment] = moments collect pfFix

  def fixLegacyMoment(moment: Option[Moment]): Option[Moment] = moment collect pfFix

  def fixLegacyMomentDataSeq(moments: Seq[MomentData]): Seq[MomentData] = moments collect {
    case moment if moment.momentType.name == walkMoment => moment.copy(momentType = OutAndAboutMoment)
    case moment if moment.momentType.name == runningMoment => moment.copy(momentType = SportsMoment)
    case moment if moment.momentType.name != bikeMoment => moment
  }

  def fixLegacyCloudMomentSeq(moments: Seq[CloudStorageMoment]): Seq[CloudStorageMoment] = moments collect {
    case moment if moment.momentType.name == walkMoment => moment.copy(momentType = OutAndAboutMoment)
    case moment if moment.momentType.name == runningMoment => moment.copy(momentType = SportsMoment)
    case moment if moment.momentType.name != bikeMoment => moment
  }

}
