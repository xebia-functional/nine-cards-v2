package com.fortysevendeg.ninecardslauncher.app.ui.share

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.URLUtil
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ActivityUiContext, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.share.models.{SharedContent, Web}
import com.fortysevendeg.ninecardslauncher2.TypedFindView
import macroid.Contexts

class SharedContentActivity
  extends AppCompatActivity
    with Contexts[AppCompatActivity]
    with ContextSupportProvider
    with TypedFindView
    with SharedContentUiActionsImpl { self =>

  lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  lazy val presenter: SharedContentPresenter = new SharedContentPresenter(self)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    presenter.receivedIntent(getIntent)
  }
}
