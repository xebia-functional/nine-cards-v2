package com.fortysevendeg.ninecardslauncher.app

import android.app.Application
import android.content.Context
import android.os.{Handler, StrictMode}
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.fortysevendeg.ninecardslauncher2.BuildConfig
import io.fabric.sdk.android.Fabric

class NineCardsApplication
  extends Application {
  self =>

  override def onCreate() {
    super.onCreate()
    try {
      // Create variables for enabling Stetho and StrictMode in issue #212
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
      Fabric.`with`(self, new Crashlytics())
    } catch {
      case _: Throwable =>
    }
  }

  override def attachBaseContext(base: Context): Unit = {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

}