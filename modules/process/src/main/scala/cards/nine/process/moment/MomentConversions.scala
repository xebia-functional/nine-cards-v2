package cards.nine.process.moment

import cards.nine.process.commons.CommonConversions
import cards.nine.process.commons.models.{Collection, Moment, MomentWithCollection}
import cards.nine.services.persistence.models.{Moment => ServicesMoment}

trait MomentConversions extends CommonConversions {

  def toMomentSeq(servicesMomentSeq: Seq[ServicesMoment]) = servicesMomentSeq map toMoment

  def toMomentWithCollection(moment: Moment, collection: Collection): MomentWithCollection =
    MomentWithCollection(
      collection = collection,
      timeslot = moment.timeslot,
      wifi = moment.wifi,
      headphone = moment.headphone,
      momentType = moment.momentType
    )

}
