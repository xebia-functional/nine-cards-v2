package cards.nine.app.ui.share

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.ui.commons.{ActivityUiContext, UiContext}
import com.fortysevendeg.ninecardslauncher2.TypedFindView
import macroid.Contexts

class SharedContentActivity
  extends AppCompatActivity
    with Contexts[AppCompatActivity]
    with ContextSupportProvider
    with TypedFindView
    with SharedContentUiActionsImpl { self =>

  lazy val uiContext: UiContext[Activity] = ActivityUiContext(self)

  lazy val presenter: SharedContentPresenter = new SharedContentPresenter(self)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    presenter.receivedIntent(getIntent)
  }
}
