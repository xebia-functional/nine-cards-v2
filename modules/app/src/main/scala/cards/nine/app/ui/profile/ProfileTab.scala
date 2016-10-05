package cards.nine.app.ui.profile

sealed trait ProfileTab

case object PublicationsTab extends ProfileTab

case object SubscriptionsTab extends ProfileTab

case object AccountsTab extends ProfileTab
