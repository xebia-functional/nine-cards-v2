package com.fortysevendeg.ninecardslauncher.modules.persistent

import com.fortysevendeg.ninecardslauncher.modules.theme.Theme
import macroid.ContextWrapper

trait PersistenceServices {

  def theme(implicit context: ContextWrapper): Theme

}
