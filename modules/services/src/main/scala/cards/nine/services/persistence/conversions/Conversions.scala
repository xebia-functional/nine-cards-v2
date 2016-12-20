package cards.nine.services.persistence.conversions

import cards.nine.models.TermCounter
import cards.nine.repository.model.{DataCounter => RepoDataCounter}

trait Conversions
    extends AppConversions
    with CardConversions
    with CollectionConversions
    with UserConversions
    with MomentConversions
    with DockAppConversions
    with WidgetConversions {

  def toDataCounterSeq(items: Seq[RepoDataCounter]): Seq[TermCounter] = items map toDataCounter

  def toDataCounter(item: RepoDataCounter): TermCounter =
    TermCounter(term = item.term, count = item.count)

}
