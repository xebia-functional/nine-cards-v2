package com.fortysevendeg.ninecardslauncher.app.ui.profile.models

import java.util.Date

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher2.R
import org.ocpsoft.prettytime.PrettyTime
import scalaz.Scalaz._

sealed trait AccountSyncType

case object Header extends AccountSyncType

case class Device(current: Boolean) extends AccountSyncType

case class AccountSync(
  title: String,
  accountSyncType: AccountSyncType,
  resourceId: Option[String] = None,
  subtitle: Option[String] = None)

object AccountSync {

  def header(title: String) =
    AccountSync(
      title = title,
      accountSyncType = Header)

  def syncDevice(title: String, syncDate: Date, current: Boolean = false, resourceId: String)(implicit context: ContextSupport) = {
    val time = new PrettyTime().format(syncDate)
    AccountSync(
      title = title,
      accountSyncType = Device(current),
      resourceId = resourceId.some,
      subtitle = Option(context.getResources.getString(R.string.syncLastSynced, time)))
  }

}