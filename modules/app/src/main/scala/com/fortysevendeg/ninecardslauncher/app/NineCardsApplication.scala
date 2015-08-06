package com.fortysevendeg.ninecardslauncher.app

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex

class NineCardsApplication
  extends Application {

  override def attachBaseContext(base: Context): Unit = {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

}