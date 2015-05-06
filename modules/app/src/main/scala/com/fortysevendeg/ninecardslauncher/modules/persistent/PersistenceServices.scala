package com.fortysevendeg.ninecardslauncher.modules.persistent

import com.fortysevendeg.ninecardslauncher.modules.theme.Theme
import macroid.AppContext

trait PersistenceServices {

  def theme(implicit appContext: AppContext): Theme

}
