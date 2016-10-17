package cards.nine.process.thirdparty

import android.os.StrictMode
import cards.nine.app.ui.preferences.commons.{IsStethoActive, NineCardsPreferencesValue}
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.facebook.stetho.Stetho
import com.fortysevendeg.ninecardslauncher.R
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric

class ExternalServicesProcess
  extends ImplicitsExternalServicesProcessException {

  def initializeCrashlytics(implicit contextSupport: ContextSupport): TaskService[Unit] = TaskService {
    CatchAll[ExternalServicesProcessException] {
      if (readFlag(R.string.crashlytics_enabled) && getString(R.string.crashlytics_api_key).nonEmpty) {
        Fabric.`with`(contextSupport.context, new Crashlytics.Builder()
          .core(new CrashlyticsCore.Builder().build())
          .build())
      }
    }
  }

  def initializeStrictMode(implicit contextSupport: ContextSupport): TaskService[Unit] = TaskService {
    CatchAll[ExternalServicesProcessException] {
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
    }
  }

  def initializeStetho(implicit contextSupport: ContextSupport): TaskService[Unit] = TaskService {
    CatchAll[ExternalServicesProcessException] {
      if (IsStethoActive.readValue(NineCardsPreferencesValue(contextSupport))) {
        Stetho.initialize(
          Stetho.newInitializerBuilder(contextSupport.context)
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(contextSupport.context))
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(contextSupport.context))
            .build())
      }
    }
  }

  def initializeFirebase(implicit contextSupport: ContextSupport): TaskService[Unit] = TaskService {
    CatchAll[ExternalServicesProcessException] {
      if (readFlag(R.string.firebase_enabled)) FirebaseAnalytics.getInstance(contextSupport.context)
    }
  }

  private[this] def getString(key: Int)(implicit contextSupport: ContextSupport): String =
    contextSupport.getResources.getString(key)

  private[this] def readFlag(key: Int)(implicit contextSupport: ContextSupport): Boolean =
    getString(key).equalsIgnoreCase("true")

}
