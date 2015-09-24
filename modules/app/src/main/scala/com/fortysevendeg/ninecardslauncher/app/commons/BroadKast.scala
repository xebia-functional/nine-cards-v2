package com.fortysevendeg.ninecardslauncher.app.commons

import android.content._

trait Broadkast {

  kast: ContextWrapper =>

  val actionsFilters: Seq[String]

  def actionSubscribed(action: String, data: Option[String])

  def returnActionSubscribed(action: String): Option[BroadAction]

  lazy val broadkast = new BroadcastReceiver {
    override def onReceive(context: Context, intent: Intent): Unit = Option(intent) map { i =>
      (Option(i.getAction), Option(i.getStringExtra(keyType)), Option(i.getStringExtra(keyData)))
    } match {
      case Some((Some(action: String), Some(key: String), data)) if key == commandType =>
        actionSubscribed(action, data)
      case Some((Some(action: String), Some(key: String), _)) if key == questionType =>
        returnActionSubscribed(action) foreach (ba => kast ! ba)
    }
  }

  def registerKast = {
    val intentFilter = new IntentFilter()
    actionsFilters foreach intentFilter.addAction
    registerReceiver(broadkast, intentFilter)
  }

  def unregisterKast = unregisterReceiver(broadkast)

  def !(broadAction: BroadAction) = {
    val intent = new Intent(broadAction.action)
    intent.putExtra(keyType, commandType)
    broadAction.data foreach (d => intent.putExtra(keyData, d))
    sendBroadcast(intent)
  }

  def ?(broadAction: BroadAction) = {
    val intent = new Intent(broadAction.action)
    intent.putExtra(keyType, questionType)
    broadAction.data foreach (d => intent.putExtra(keyData, d))
    sendBroadcast(intent)
  }

  case class BroadAction(action: String, data: Option[String] = None)

  private[this] val keyType = "broadKast-key-type"

  private[this] val questionType = "broadKast-question"

  private[this] val commandType = "broadKast-command"

  private[this] val keyData = "broadKast-key-data"

}
