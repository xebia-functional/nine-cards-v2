package com.fortysevendeg.ninecardslauncher.app.commons

import android.content._
import BroadcastDispatcher._

trait BroadcastDispatcher {

  dispatcher: ContextWrapper =>

  val actionsFilters: Seq[String]

  def manageCommand(action: String, command: Option[String]): Unit = {}

  def manageQuestion(action: String): Option[BroadAction] = None

  lazy val broadcast = new BroadcastReceiver {
    override def onReceive(context: Context, intent: Intent): Unit = Option(intent) map { i =>
      (Option(i.getAction), Option(i.getStringExtra(keyType)), Option(i.getStringExtra(keyCommand)))
    } match {
      case Some((Some(action: String), Some(key: String), data)) if key == commandType =>
        manageCommand(action, data)
      case Some((Some(action: String), Some(key: String), _)) if key == questionType =>
        manageQuestion(action) foreach (ba => dispatcher ! ba)
      case _ =>
    }
  }

  def registerDispatchers = {
    val intentFilter = new IntentFilter()
    actionsFilters foreach intentFilter.addAction
    registerReceiver(broadcast, intentFilter)
  }

  def unregisterDispatcher = unregisterReceiver(broadcast)

  def !(broadAction: BroadAction) = {
    val intent = new Intent(broadAction.action)
    intent.putExtra(keyType, commandType)
    broadAction.command foreach (d => intent.putExtra(keyCommand, d))
    sendBroadcast(intent)
  }

  def ?(action: String) = {
    val intent = new Intent(action)
    intent.putExtra(keyType, questionType)
    sendBroadcast(intent)
  }

}

case class BroadAction(action: String, command: Option[String] = None)

object BroadcastDispatcher {
  val keyType = "broadcast-key-type"
  val questionType = "broadcast-question"
  val commandType = "broadcast-command"
  val keyCommand = "broadcast-key-command"
}
