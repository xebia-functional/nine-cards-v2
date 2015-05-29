package com.fortysevendeg.ninecardslauncher.modules.persistent.impl

import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistenceServices
import com.fortysevendeg.ninecardslauncher.modules.theme.{Theme, ThemeLight}
import macroid.ContextWrapper

class PersistenceServicesImpl extends PersistenceServices {

  def theme(implicit context: ContextWrapper): Theme = ThemeLight

}