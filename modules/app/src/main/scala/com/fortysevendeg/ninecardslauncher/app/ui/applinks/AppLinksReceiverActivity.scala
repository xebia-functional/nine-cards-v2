package com.fortysevendeg.ninecardslauncher.app.ui.applinks

import android.app.Activity
import android.os.Bundle

class AppLinksReceiverActivity
  extends Activity {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    val intent = getIntent
    val data = intent.getData
    val path = data.getPath

    println(s"-----> $data")
    println(s"-----> $path")
    finish()
  }
}
