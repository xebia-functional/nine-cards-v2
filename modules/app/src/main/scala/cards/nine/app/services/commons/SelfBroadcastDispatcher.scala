package cards.nine.app.services.commons

import android.content._
import cards.nine.app.commons.BroadcastDispatcher._

trait SelfBroadcastDispatcher {

  dispatcher: ContextWrapper =>

  val actionsFilters: Seq[String]

  def manageCommand(action: String, command: Option[String]): Unit = {}

  def manageQuestion(action: String): Unit = {}

  lazy val broadcast = new BroadcastReceiver {
    override def onReceive(context: Context, intent: Intent): Unit = Option(intent) map { i =>
      (Option(i.getAction), Option(i.getStringExtra(keyType)), Option(i.getStringExtra(keyCommand)))
    } match {
      case Some((Some(action: String), Some(key: String), data)) if key == commandType =>
        manageCommand(action, data)
      case Some((Some(action: String), Some(key: String), _)) if key == questionType =>
        manageQuestion(action)
      case _ =>
    }
  }

  def registerDispatchers() = {
    val intentFilter = new IntentFilter()
    actionsFilters foreach intentFilter.addAction
    registerReceiver(broadcast, intentFilter)
  }

  def unregisterDispatcher() = unregisterReceiver(broadcast)

}
