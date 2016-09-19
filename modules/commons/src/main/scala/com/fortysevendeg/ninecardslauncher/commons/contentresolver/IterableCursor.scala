package com.fortysevendeg.ninecardslauncher.commons.contentresolver

import android.database.Cursor

trait IterableCursor[T] {
  def count(): Int
  def moveToPosition(pos: Int): T
  def close(): Unit
}

object IterableCursor {

  implicit class RichCursor(c: Cursor) {
    private def implicitIterator[T](f: => T) = new IterableCursor[T] {
      override def count(): Int = if (c.isClosed) 0 else c.getCount
      override def moveToPosition(pos: Int): T = {
        c.moveToPosition(pos)
        f
      }
      override def close(): Unit = if (!c.isClosed) c.close()
    }

    private def implicitIter[T](f: => T) = implicitIterator[T](f)

    def toIterator[T](conversionFunction: Cursor => T) = implicitIter {
      conversionFunction(c)
    }

  }

  def toSeq[T](iterator: IterableCursor[T], pos: Int = 0, seq: Seq[T] = Seq.empty): Seq[T] =
    if (pos >= iterator.count()) {
      seq
    } else {
      toSeq(iterator, pos + 1, seq :+ iterator.moveToPosition(pos))
    }

}
