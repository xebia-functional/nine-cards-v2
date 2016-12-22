package cards.nine.process.thirdparty

import android.os.StrictMode
import cards.nine.app.ui.commons.AppLog
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.preferences.commons.IsStethoActive
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import com.apptentive.android.sdk.Apptentive
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.facebook.stetho.Stetho
import com.fortysevendeg.ninecardslauncher.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import io.fabric.sdk.android.Fabric
import io.flowup.FlowUp
import macroid.Ui

class ExternalServicesProcess
    extends ImplicitsExternalServicesProcessException
    with ImplicitsTokenFirebaseException {

  def initializeCrashlytics(implicit contextSupport: ContextSupport): TaskService[Unit] =
    TaskService {
      CatchAll[ExternalServicesProcessException] {
        if (readFlag(R.string.crashlytics_enabled) && getString(R.string.crashlytics_api_key).nonEmpty) {
          AppLog.info("Initializing Crashlytics")
          Fabric.`with`(
            contextSupport.context,
            new Crashlytics.Builder().core(new CrashlyticsCore.Builder().build()).build())
        }
      }
    }

  def initializeStrictMode(implicit contextSupport: ContextSupport): TaskService[Unit] =
    TaskService {
      CatchAll[ExternalServicesProcessException] {
        if (readFlag(R.string.strict_mode_enabled)) {
          AppLog.info("Initializing Strict Mode")
          StrictMode.setThreadPolicy(
            new StrictMode.ThreadPolicy.Builder()
              .detectDiskReads()
              .detectDiskWrites()
              .detectAll()
              .penaltyLog()
              .build())
          StrictMode.setVmPolicy(
            new StrictMode.VmPolicy.Builder()
              .detectLeakedSqlLiteObjects()
              .detectLeakedClosableObjects()
              .detectAll()
              .penaltyLog()
              .build())
        }
      }
    }

  def initializeStetho(implicit contextSupport: ContextSupport): TaskService[Unit] =
    TaskService {
      CatchAll[ExternalServicesProcessException] {
        if (IsStethoActive.readValueWith(contextSupport.context)) {
          AppLog.info("Initializing Stetho")
          Stetho.initialize(
            Stetho
              .newInitializerBuilder(contextSupport.context)
              .enableDumpapp(Stetho.defaultDumperPluginsProvider(contextSupport.context))
              .enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(contextSupport.context))
              .build())
        }
      }
    }

  def initializeFirebase(implicit contextSupport: ContextSupport): TaskService[Unit] =
    TaskService {
      CatchAll[ExternalServicesProcessException] {
        if (readFlag(R.string.firebase_enabled)) {
          AppLog.info("Initializing Firebase")
          FirebaseAnalytics.getInstance(contextSupport.context)
        }
      }
    }

  def initializeFlowUp(implicit contextSupport: ContextSupport): TaskService[Unit] =
    Ui {
      if (readFlag(R.string.flowup_enabled)) {
        AppLog.info("Initializing FlowUp")
        FlowUp.Builder
          .`with`(contextSupport.application)
          .apiKey(getString(R.string.flowup_apikey))
          .start()
      }
    }.toService()

  def initializeApptentive(implicit contextSupport: ContextSupport): TaskService[Unit] =
    Ui {
      if (readFlag(R.string.apptentive_enabled)) {
        AppLog.info("Initializing Apptentive")
        Apptentive.register(contextSupport.application, getString(R.string.apptentive_apikey))
      }
    }.toService()

  def readFirebaseToken: TaskService[String] = TaskService {
    CatchAll[TokenFirebaseException] {
      FirebaseInstanceId.getInstance().getToken
    }
  }

  private[this] def getString(key: Int)(implicit contextSupport: ContextSupport): String =
    contextSupport.getResources.getString(key)

  private[this] def readFlag(key: Int)(implicit contextSupport: ContextSupport): Boolean =
    getString(key).equalsIgnoreCase("true")

}
