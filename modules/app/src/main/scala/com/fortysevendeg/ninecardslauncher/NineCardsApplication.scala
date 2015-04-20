package com.fortysevendeg.ninecardslauncher

import android.content.Context
import android.app.Application
import com.fortysevendeg.ninecardslauncher.di.DependencyInjector

class NineCardsApplication
    extends Application
    with DependencyInjector {

  override def attachBaseContext(base: Context): Unit = {
    super.attachBaseContext(base)
  }

}