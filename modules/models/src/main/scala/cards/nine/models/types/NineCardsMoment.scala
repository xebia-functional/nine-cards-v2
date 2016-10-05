package cards.nine.models.types

import cards.nine.models.NineCardsMoments
import NineCardsMoments._

sealed trait NineCardsMoment{
  val name: String

  def getIconResource : String = name.toLowerCase
  def getStringResource : String = name.toLowerCase
}

case object HomeMorningMoment extends NineCardsMoment {
  override val name: String = homeMorningMoment
}

case object WorkMoment extends NineCardsMoment {
  override val name: String = workMoment
}

case object HomeNightMoment extends NineCardsMoment {
  override val name: String = homeNightMoment
}

case object TransitMoment extends NineCardsMoment {
  override val name: String = transitMoment
}

object NineCardsMoment {

  val moments = Seq(HomeMorningMoment, WorkMoment, HomeNightMoment, TransitMoment)

  def apply(name: String): NineCardsMoment = moments find (_.name == name) getOrElse
    (throw new IllegalArgumentException(s"$name not found"))

}
