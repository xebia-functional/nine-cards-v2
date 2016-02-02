package com.fortysevendeg.ninecardslauncher.app.analytics

sealed trait Label {
  def name: String
}

case class ProvideLabel(label: String) extends Label {
  override def name: String = label
}