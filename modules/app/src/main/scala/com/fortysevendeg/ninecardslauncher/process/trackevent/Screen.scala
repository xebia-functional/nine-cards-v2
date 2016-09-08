package com.fortysevendeg.ninecardslauncher.process.trackevent

import com.fortysevendeg.ninecardslauncher.process.trackevent.Screen._

sealed trait Screen {
  def name: String
}

case object WizardScreen extends Screen {
  override def name: String = wizardName
}

case object LauncherScreen extends Screen {
  override def name: String = launcherName
}

case object CollectionDetailScreen extends Screen {
  override def name: String = collectionDetailName
}

object Screen {
  val wizardName = "Wizard"
  val launcherName = "Launcher"
  val collectionDetailName = "CollectionDetail"
}