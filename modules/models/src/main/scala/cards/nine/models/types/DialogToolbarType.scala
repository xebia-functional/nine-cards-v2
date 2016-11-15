package cards.nine.models.types

sealed trait DialogToolbarType

case object DialogToolbarTitle extends DialogToolbarType

case object DialogToolbarSearch extends DialogToolbarType