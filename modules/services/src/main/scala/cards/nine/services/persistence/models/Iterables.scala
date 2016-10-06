package cards.nine.services.persistence.models

import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.models.{Application, PersistenceDockApp}
import cards.nine.repository.model.{App => RepositoryApp, DockApp => RepositoryDockApp}
import cards.nine.services.persistence.conversions.{AppConversions, DockAppConversions}

class IterableApps(cursor: IterableCursor[RepositoryApp])
  extends IterableCursor[Application]
  with AppConversions {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): Application = toApp(cursor.moveToPosition(pos))

  override def close(): Unit = cursor.close()

}

class IterableDockApps(cursor: IterableCursor[RepositoryDockApp])
  extends IterableCursor[PersistenceDockApp]
    with DockAppConversions {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): PersistenceDockApp = toDockApp(cursor.moveToPosition(pos))

  override def close(): Unit = cursor.close()

}