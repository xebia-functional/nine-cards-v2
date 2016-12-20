package cards.nine.services.shortcuts.impl

import android.content.Intent
import cards.nine.models.Shortcut

trait ShortcutsServicesImplData {

  val name        = "Name"
  val packageName = "com.fortysevendeg.ninecardslauncher.test.sampleapp"

  val intent = new Intent(Intent.ACTION_CREATE_SHORTCUT)

  val sampleShortcut1 = Shortcut(title = "B - Sample Name 1", icon = None, intent = intent)

  val sampleShortcut2 = Shortcut(title = "A - Sample Name 2", icon = None, intent = intent)

  val shotcutsList = Seq(sampleShortcut1, sampleShortcut2)

}
