package com.fortysevendeg.ninecardslauncher.modules.persistent.impl

import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistenceServices
import com.fortysevendeg.ninecardslauncher.modules.theme.{Theme, ThemeLight}
import macroid.AppContext

class PersistenceServicesImpl extends PersistenceServices {

  def theme(implicit appContext: AppContext): Theme = ThemeLight

}