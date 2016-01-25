package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.Contexts
import macroid.FullDsl._

import scalaz.concurrent.Task

class ProfileActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with ProfileComposer {

  implicit lazy val di = new Injector

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    getUserInfo()
    setContentView(R.layout.profile_activity)
    runUi(initUi)
  }

  private[this] def getUserInfo() = Task.fork(di.userConfigProcess.getUserInfo.run).resolveAsyncUi(
    onResult = (userInfo) => userProfile(userInfo.email, userInfo.imageUrl)
  )

}
