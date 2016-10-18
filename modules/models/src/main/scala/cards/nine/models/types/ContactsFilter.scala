package cards.nine.models.types

sealed trait ContactsFilter

case object AllContacts extends ContactsFilter

case object FavoriteContacts extends ContactsFilter

case object ContactsWithPhoneNumber extends ContactsFilter