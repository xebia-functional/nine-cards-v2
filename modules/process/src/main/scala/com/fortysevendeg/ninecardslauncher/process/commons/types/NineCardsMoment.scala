package com.fortysevendeg.ninecardslauncher.process.commons.types

import com.fortysevendeg.ninecardslauncher.process.commons.NineCardsMoments._

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

object NineCardsMoment {

  val moments = Seq(HomeMorningMoment, WorkMoment, HomeNightMoment)

  def apply(name: String): NineCardsMoment = moments find (_.name == name) getOrElse
    (throw new IllegalArgumentException(s"$name not found"))

}
