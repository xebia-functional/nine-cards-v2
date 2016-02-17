package com.fortysevendeg.ninecardslauncher.app.ui.profile.models

import java.util.Date

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher2.R
import org.ocpsoft.prettytime.PrettyTime
import scalaz.Scalaz._

sealed trait AccountSyncType

case object Header extends AccountSyncType

case object Device extends AccountSyncType

case object SyncDevice extends AccountSyncType

case class AccountSync(
  title: String,
  accountSyncType: AccountSyncType,
  subtitle: Option[String] = None)

object AccountSync {

  def header(title: String) =
    AccountSync(
      title = title,
      accountSyncType = Header)

  def device(title: String, current: Boolean)(implicit context: ContextSupport) =
    AccountSync(
      title = title,
      accountSyncType = Device,
      subtitle = current.option(context.getResources.getString(R.string.sync_current)))

  def syncDevice(title: String, syncDate: Date)(implicit context: ContextSupport) = {
    val time = new PrettyTime().format(syncDate)
    AccountSync(
      title = title,
      accountSyncType = SyncDevice,
      subtitle = Option(context.getResources.getString(R.string.sync_last_synced, time)))
  }

}