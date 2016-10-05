package cards.nine.models.types

sealed trait EmailCategory

case object EmailHome extends EmailCategory

case object EmailWork extends EmailCategory

case object EmailOther extends EmailCategory
