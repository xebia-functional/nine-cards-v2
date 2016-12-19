package cards.nine.app.ui.components.models

import cards.nine.models.Collection
import cards.nine.models.types.NineCardsMoment

case class LauncherData(
    workSpaceType: WorkSpaceType,
    moment: Option[LauncherMoment] = None,
    collections: Seq[Collection] = Seq.empty,
    positionByType: Int = 0)

case class LauncherMoment(momentType: Option[NineCardsMoment], collection: Option[Collection])

sealed trait WorkSpaceType {
  val value: Int

  def isMomentWorkSpace: Boolean = this == MomentWorkSpace
}

case object MomentWorkSpace extends WorkSpaceType {
  override val value: Int = 0
}

case object CollectionsWorkSpace extends WorkSpaceType {
  override val value: Int = 1
}

object WorkSpaceType {
  def apply(value: Int): WorkSpaceType = value match {
    case MomentWorkSpace.value => MomentWorkSpace
    case _                     => CollectionsWorkSpace
  }
}
