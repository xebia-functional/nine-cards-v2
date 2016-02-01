package com.fortysevendeg.ninecardslauncher.app.analytics

sealed trait Value {
  def value: Long
}

case object VeryLowValue extends Value {
  override def value: Long = 1
}

case object LowValue extends Value {
  override def value: Long = 2
}

case object MediumValue extends Value {
  override def value: Long = 3
}

case object HighValue extends Value {
  override def value: Long = 4
}

case object VeryHighValue extends Value {
  override def value: Long = 5
}

case class ProvideValue(v: Long) extends Value {
  override def value: Long = v
}