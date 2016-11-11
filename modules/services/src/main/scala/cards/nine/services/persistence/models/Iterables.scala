package cards.nine.services.persistence.models

import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.models.Application
import cards.nine.repository.model.{App => RepositoryApp}
import cards.nine.services.persistence.conversions.AppConversions

class IterableApps(cursor: IterableCursor[RepositoryApp])
  extends IterableCursor[Application]
  with AppConversions {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): Application = toApp(cursor.moveToPosition(pos))

  override def close(): Unit = cursor.close()

}