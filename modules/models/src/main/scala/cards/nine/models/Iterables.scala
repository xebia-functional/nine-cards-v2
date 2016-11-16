package cards.nine.models

import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.javaNull
import cards.nine.models.types.Misc

class IterableAppCursor[T](cursor: IterableCursor[T], f: T => Application)
  extends IterableApp {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): ApplicationData = f(cursor.moveToPosition(pos)).toData

  override def close(): Unit = cursor.close()

}

class EmptyIterableApps()
  extends IterableAppCursor(javaNull, javaNull) {
  val emptyApp = ApplicationData("", "", "", Misc, 0, 0, "", installedFromGooglePlay = false)
  override def count(): Int = 0
  override def moveToPosition(pos: Int): ApplicationData = emptyApp
  override def close(): Unit = {}
}


class IterableContacts(cursor: IterableCursor[Contact])
  extends IterableCursor[Contact] {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): Contact = cursor.moveToPosition(pos)

  override def close(): Unit = cursor.close()

}
