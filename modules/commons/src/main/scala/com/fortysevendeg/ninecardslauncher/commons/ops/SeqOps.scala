package com.fortysevendeg.ninecardslauncher.commons.ops

object SeqOps {

  implicit class SeqCursor[T](seq: Seq[T]) {
    def reorder(from: Int, to: Int) = {
      val range1 = math.min(from, to)
      val range2 = math.max(from, to)

      val header = seq.take(range1)
      val tail = seq.drop(range2 + 1)
      val updatedRange = reorderRange(from, to)

      header ++ updatedRange ++ tail
    }

    def reorderRange(from: Int, to: Int) = {
      val range1 = math.min(from, to)
      val range2 = math.max(from, to)

      val range = seq.slice(range1, range2 + 1)
      val updatedRange = if (from < to) {
        range.drop(1) ++ range.take(1)
      } else {
        range.takeRight(1) ++ range.dropRight(1)
      }
      updatedRange
    }
  }

}
