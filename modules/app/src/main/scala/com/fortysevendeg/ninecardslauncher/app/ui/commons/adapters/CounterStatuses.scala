package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters

case class CounterStatuses(
  from: Int = 0,
  count: Int = 0) {

  def isActive(position: Int): Boolean = position >= from && position < (from + count)

  def reset(count: Int = 0): CounterStatuses = CounterStatuses(from = 0, count = count)

}
