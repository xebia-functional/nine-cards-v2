package com.fortysevendeg.ninecardslauncher.di

import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistenceServices
import com.fortysevendeg.ninecardslauncher.modules.persistent.impl.PersistenceServicesImpl

trait Module {

  lazy val persistentServices: PersistenceServices = new PersistenceServicesImpl

}
