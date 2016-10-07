package cards.nine.process.moment

import cards.nine.models.{MomentWithCollection, Collection, Moment}
import cards.nine.process.commons.CommonConversions

trait MomentConversions extends CommonConversions {

  def toMomentWithCollection(moment: Moment, collection: Collection): MomentWithCollection =
    MomentWithCollection(
      collection = collection,
      timeslot = moment.timeslot,
      wifi = moment.wifi,
      headphone = moment.headphone,
      momentType = moment.momentType
    )

}
