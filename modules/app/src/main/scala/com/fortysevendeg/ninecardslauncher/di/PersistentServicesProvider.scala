package com.fortysevendeg.ninecardslauncher.di

import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistentServices

trait PersistentServicesProvider {

  val getPersistentServices: Option[PersistentServices]

}
