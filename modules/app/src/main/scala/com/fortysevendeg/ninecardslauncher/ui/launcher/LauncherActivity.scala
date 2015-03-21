package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.app.Activity
import android.os.Bundle
import macroid.Contexts

class LauncherActivity
  extends Activity
  with Contexts[Activity]
  with Layout {

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(content)
  }

}
