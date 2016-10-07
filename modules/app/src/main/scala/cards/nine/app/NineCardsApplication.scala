package cards.nine.app

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.fortysevendeg.ninecardslauncher.R
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric

class NineCardsApplication
  extends Application {
  self =>

  override def onCreate() {

    def readFlag(key: Int): Boolean = getString(key).equalsIgnoreCase("true")

    def initCrashlytics(): Unit =
      try {
        if (readFlag(R.string.crashlytics_enabled) && getString(R.string.crashlytics_api_key).nonEmpty) {
          Fabric.`with`(self, new Crashlytics())
        }
      } catch {
        case e: Throwable => e.printStackTrace()
      }

    def initStrictMode(): Unit =
      try {
        if (readFlag(R.string.strict_mode_enabled)) {
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
        }
      } catch {
        case e: Throwable => e.printStackTrace()
      }

    def initStetho(): Unit =
      try {
        if (readFlag(R.string.stetho_enabled)) {
          Stetho.initialize(
            Stetho.newInitializerBuilder(this)
              .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
              .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
              .build())
        }
      } catch {
        case e: Throwable => e.printStackTrace()
      }

    def initFirebase(): Unit =
      try {
        if (readFlag(R.string.firebase_enabled)) FirebaseAnalytics.getInstance(this)
      } catch {
        case e: Throwable => e.printStackTrace()
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