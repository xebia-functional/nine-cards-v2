package com.fortysevendeg.ninecardslauncher.app

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.fortysevendeg.ninecardslauncher2.BuildConfig
import io.fabric.sdk.android.Fabric

class NineCardsApplication
  extends Application {

  override def onCreate() {
    super.onCreate()
    // In old version BuildConfig returns a NoClassDefFoundError
    // Fix this problem in ticket 9C-325
    try {
      if (BuildConfig.DEBUG) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
          .detectDiskReads()
          .detectDiskWrites()
          .detectAll()
          .penaltyLog()
          .build())
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
          .detectLeakedSqlLiteObjects()
          .detectLeakedClosableObjects()
          .detectAll()
          .penaltyLog()
          .build())
        Stetho.initialize(
          Stetho.newInitializerBuilder(this)
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
            .build())
      }
    } catch {
      case _: Throwable =>
    }
    Fabric.`with`(this, new Crashlytics())
  }

  override def attachBaseContext(base: Context): Unit = {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

}