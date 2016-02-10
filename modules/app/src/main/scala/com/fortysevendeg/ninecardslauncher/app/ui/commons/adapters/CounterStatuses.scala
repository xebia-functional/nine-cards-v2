package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters

case class CounterStatuses(
  selectItems: Boolean = false,
  from: Int = 0,
  count: Int = 0) {

  def active(from: Int, count: Int): CounterStatuses = copy(selectItems = true, from = from, count = count)

  def isActive(position: Int): Boolean = position >= from && position < (from + count)

  def reset: CounterStatuses = CounterStatuses(selectItems = false)

}
