package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{DataCounter => RepoDataCounter}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.DataCounter

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