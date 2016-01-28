package com.fortysevendeg.ninecardslauncher.app.analytics

sealed trait Value {
  def value: Long
}

case class VeryLowValue() extends Value {
  override def value: Long = 1
}

case class LowValue() extends Value {
  override def value: Long = 2
}

case class MediumValue() extends Value {
  override def value: Long = 3
}

case class HighValue() extends Value {
  override def value: Long = 4
}

case class VeryHighValue() extends Value {
  override def value: Long = 5
}

case class ProvideValue(v: Long) extends Value {
  override def value: Long = v
}