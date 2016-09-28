package cards.nine.app

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import cards.nine.app.ui.commons.AppLog
import com.fortysevendeg.ninecardslauncher2.R
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric

class NineCardsApplication
  extends Application {
  self =>

  override def onCreate() {

    def readFlag(key: Int): Boolean = getString(key).equalsIgnoreCase("true")

    def initCrashlytics(): Unit =
      if (readFlag(R.string.crashlytics_enabled) && getString(R.string.crashlytics_api_key).nonEmpty) {
        try {
          Fabric.`with`(self, new Crashlytics())
        } catch {
          case e: Throwable => AppLog.printErrorMessage(e, Some("Error initializing Crashlytics"))
        }
      }

    def initStrictMode(): Unit =
      if (readFlag(R.string.strict_mode_enabled)) {
        try {
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
          case e: Throwable => AppLog.printErrorMessage(e, Some("Error initializing strict mode"))
        }
      }

    def initStetho(): Unit =
      if (readFlag(R.string.stetho_enabled)) {
        try {
          Stetho.initialize(
            Stetho.newInitializerBuilder(this)
              .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
              .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
              .build())
        } catch {
          case e: Throwable => AppLog.printErrorMessage(e, Some("Error initializing Stetho"))
        }
      }

    def initFirebase(): Unit =
      if (readFlag(R.string.firebase_enabled)) {
        try {
          FirebaseAnalytics.getInstance(this)
        } catch {
          case e: Throwable => AppLog.printErrorMessage(e, Some("Error initializing Firebase"))
        }
      }

    super.onCreate()
    initFirebase()
    initCrashlytics()
    initStetho()
    initStrictMode()
  }

  override def attachBaseContext(base: Context): Unit = {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

}