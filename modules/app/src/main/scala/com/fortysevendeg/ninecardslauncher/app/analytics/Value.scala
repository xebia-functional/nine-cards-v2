package com.fortysevendeg.ninecardslauncher.app.analytics

sealed trait Value {
  def value: Long
}

case object NoValue extends Value {
  override def value: Long = 0
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

// Values related to manage apps

case object OpenAppFromCollectionValue extends Value {
  override def value: Long = 3
}

case object OpenAppFromAppDrawerValue extends Value {
  override def value: Long = 1
}

case object OpenAppFromDockValue extends Value {
  override def value: Long = 1
}

case object AddedToCollectionValue extends Value {
  override def value: Long = 10
}

case object RemovedInCollectionValue extends Value {
  override def value: Long = -3
}

case object OpenMomentFromWorkspaceValue extends Value {
  override def value: Long = 3
}