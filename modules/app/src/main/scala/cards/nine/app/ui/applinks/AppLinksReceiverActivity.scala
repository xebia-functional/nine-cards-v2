package cards.nine.app.ui.applinks

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.ui.commons.{ActivityUiContext, AppLog, UiContext}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.process.sharedcollections.SharedCollectionsConfigurationException
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.Contexts

class AppLinksReceiverActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView {

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  lazy val actions = new AppLinksReceiverUiActions(AppLinksReceiverDOM(this))

  lazy val jobs = new AppLinksReceiverJobs(actions)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.app_link_dialog_activity)

    Option(getIntent) match {
      case Some(intent) => jobs.uriReceived(intent.getData).resolveAsync(onException = (e: Throwable) => e match {
        case e: SharedCollectionsConfigurationException =>
          AppLog.invalidConfigurationV2
          finish()
        case _ =>
          finish()
      })
      case None => finish()
    }
  }
}

case class AppLinksReceiverDOM(finder: TypedFindView) {

  lazy val rootView = finder.findView(TR.app_link_root)

  lazy val loadingView = finder.findView(TR.app_link_loading_layout)

  lazy val loadingText = finder.findView(TR.app_link_loading_text)

  lazy val collectionView = finder.findView(TR.app_link_collection)

}