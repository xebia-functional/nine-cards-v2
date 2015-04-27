package com.fortysevendeg.ninecardslauncher.di

import android.app.Application
import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistentServices
import com.fortysevendeg.ninecardslauncher.modules.persistent.impl.PersistentServicesImpl
import macroid.AppContext

trait DependencyInjector {

  self : Application =>

  val appContext: AppContext = AppContext(this)

  lazy val persistentServices: PersistentServices = new PersistentServicesImpl()(appContext)

}
