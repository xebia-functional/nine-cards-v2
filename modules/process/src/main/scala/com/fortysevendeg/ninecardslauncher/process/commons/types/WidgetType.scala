package com.fortysevendeg.ninecardslauncher.process.commons.types

import com.fortysevendeg.ninecardslauncher.process.commons.WidgetTypes._

sealed trait WidgetType {
  val name: String
}

case object AppWidgetType extends WidgetType {
  override val name: String = app
}

object WidgetType {

  val widgetTypes = Seq(AppWidgetType)

  def apply(name: String): WidgetType = widgetTypes find (_.name == name) getOrElse
    (throw new IllegalArgumentException(s"$name not found"))

}



