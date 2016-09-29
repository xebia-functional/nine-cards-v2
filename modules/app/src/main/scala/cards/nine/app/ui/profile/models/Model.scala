package cards.nine.app.ui.profile.models

import java.util.Date

import cards.nine.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.R
import org.ocpsoft.prettytime.PrettyTime


sealed trait AccountSyncType

case object Header extends AccountSyncType

case class Device(current: Boolean) extends AccountSyncType

case class AccountSync(
  title: String,
  accountSyncType: AccountSyncType,
  cloudId: Option[String] = None,
  subtitle: Option[String] = None)

object AccountSync {

  def header(title: String): AccountSync =
    AccountSync(
      title = title,
      accountSyncType = Header)

  def syncDevice(
    title: String,
    syncDate: Date,
    current: Boolean = false,
    cloudId: String)(implicit context: ContextSupport): AccountSync = {
    val time = new PrettyTime().format(syncDate)
    AccountSync(
      title = title,
      accountSyncType = Device(current),
      cloudId = Option(cloudId),
      subtitle = Option(context.getResources.getString(R.string.syncLastSynced, time)))
  }

}