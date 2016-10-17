package cards.nine.models.types

sealed trait FetchAppOrder

case object OrderByName extends FetchAppOrder

case object OrderByInstallDate extends FetchAppOrder

case object OrderByCategory extends FetchAppOrder
