package com.fortysevendeg.ninecardslauncher.app.ui.applinks

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher2.R

class AppLinksReceiverActivity
  extends AppCompatActivity {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.app_link_dialog_activity)

    val intent = getIntent
    val data = intent.getData
    val path = data.getPath

    println(s"-----> $data")
    println(s"-----> $path")
//    finish()
  }
}
