package com.fortysevendeg.ninecardslauncher.process.accounts

sealed trait AppPermission {
  val value: String
}

case object GetAccounts extends AppPermission {
  override val value: String = android.Manifest.permission.GET_ACCOUNTS
}

case object ReadContacts extends AppPermission {
  override val value: String = android.Manifest.permission.READ_CONTACTS
}

case object ReadCallLog extends AppPermission {
  override val value: String = android.Manifest.permission.READ_CALL_LOG
}

case object CallPhone extends AppPermission {
  override val value: String = android.Manifest.permission.CALL_PHONE
}

object AppPermission {

  def values = Seq(GetAccounts, ReadContacts, ReadCallLog, CallPhone)

}

case class PermissionResult(permission: AppPermission, result: Boolean) {
  def hasPermission(p: AppPermission): Boolean = permission == p && result
}