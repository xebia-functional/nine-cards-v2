package com.fortysevendeg.ninecardslauncher.services.persistence.models

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor.IterableCursorSeq
import com.fortysevendeg.ninecardslauncher.repository.model.{App => RepoApp, DockApp => RepoDockApp}
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.{AppConversions, DockAppConversions}

class IterableApps(cursor: IterableCursorSeq[RepoApp])
  extends IterableCursorSeq[App]
  with AppConversions {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): App = toApp(cursor.moveToPosition(pos))

  override def close(): Unit = cursor.close()

}

class IterableDockApps(cursor: IterableCursorSeq[RepoDockApp])
  extends IterableCursorSeq[DockApp]
    with DockAppConversions {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): DockApp = toDockApp(cursor.moveToPosition(pos))

  override def close(): Unit = cursor.close()

}