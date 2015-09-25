package com.fortysevendeg.ninecardslauncher.app.ui.drawer

sealed trait AppsMenuOption

case object AppsAlphabetical extends AppsMenuOption

case object AppsByCategories extends AppsMenuOption

case object AppsByLastInstall extends AppsMenuOption

sealed trait ContactsMenuOption

case object ContactsAlphabetical extends ContactsMenuOption

case object ContactsFavorites extends ContactsMenuOption

case object ContactsByLastCall extends ContactsMenuOption
