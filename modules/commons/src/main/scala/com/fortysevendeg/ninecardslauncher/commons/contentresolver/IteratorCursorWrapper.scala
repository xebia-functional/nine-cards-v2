package com.fortysevendeg.ninecardslauncher.commons.contentresolver

import android.database.Cursor

class IteratorCursorWrapper(c: Cursor) {

  def toIterator = if (c.getColumnCount > 1) {
    throw new IllegalArgumentException("Cursor must have only 1 column")
  } else {
    new Iterator[String] {
      def hasNext = {
        val res = c.getPosition < c.getCount - 1
        if (!res) c.close()
        res
      }

      def next() = {
        c.moveToNext()
        c.getString(0)
      }
    }
  }

}
