package com.fortysevendeg.ninecardslauncher

import android.content.Context
import android.support.multidex.MultiDex
import android.app.Application

class NineCardsApplication extends Application {

  override def attachBaseContext(base: Context): Unit = {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

}