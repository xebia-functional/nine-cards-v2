package cards.nine.services.persistence.conversions

import cards.nine.models.DataCounter
import cards.nine.repository.model.{DataCounter => RepoDataCounter}

trait Conversions
  extends AppConversions
  with CardConversions
  with CollectionConversions
  with UserConversions
  with MomentConversions
  with DockAppConversions
  with WidgetConversions {

  def toDataCounterSeq(items: Seq[RepoDataCounter]): Seq[DataCounter] = items map toDataCounter

  def toDataCounter(item: RepoDataCounter): DataCounter =
    DataCounter(
      term = item.term,
      count = item.count)

}