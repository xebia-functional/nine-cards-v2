package com.fortysevendeg.ninecardslauncher.app

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.fortysevendeg.ninecardslauncher2.R
import io.fabric.sdk.android.Fabric

class NineCardsApplication
  extends Application {
  self =>

  override def onCreate() {

    def readFlag(key: Int): Boolean = getString(key).equalsIgnoreCase("true")

    def initCrashlytics =
      if (readFlag(R.string.crashlytics_enabled) && getString(R.string.crashlytics_api_key).nonEmpty) {
        try { Fabric.`with`(self, new Crashlytics()) } catch { case _: Throwable => }
      }

    def initStrictMode =
      if (readFlag(R.string.strict_mode_enabled)) {
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
        } catch {
          case _: Throwable =>
        }
      }

    def initStetho =
      if (readFlag(R.string.stetho_enabled)) {
        Stetho.initialize(
          Stetho.newInitializerBuilder(this)
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
            .build())
      }

    super.onCreate()
    initStetho
    initStrictMode
    initCrashlytics
  }

  override def attachBaseContext(base: Context): Unit = {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

}