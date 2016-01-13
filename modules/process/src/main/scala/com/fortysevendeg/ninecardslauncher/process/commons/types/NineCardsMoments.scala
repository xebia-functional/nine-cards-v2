package com.fortysevendeg.ninecardslauncher.process.commons.types

import com.fortysevendeg.ninecardslauncher.process.commons.NineCardsMoments._

sealed trait NineCardsMoments{
  val name: String
}

case object HomeMorningMoment extends NineCardsMoments {
  override val name: String = homeMorningMoment
}

case object WorkMoment extends NineCardsMoments {
  override val name: String = workMoment
}

case object HomeNightMoment extends NineCardsMoments {
  override val name: String = homeNightMoment
}

case object TransitMoment extends NineCardsMoments {
  override val name: String = transitMoment
}

object NineCardsMoments {

  val cases = Seq(HomeMorningMoment, WorkMoment, HomeNightMoment, TransitMoment)

  def apply(name: String): NineCardsMoments = cases find (_.name == name) getOrElse
    (throw new IllegalArgumentException(s"$name not found"))

}
