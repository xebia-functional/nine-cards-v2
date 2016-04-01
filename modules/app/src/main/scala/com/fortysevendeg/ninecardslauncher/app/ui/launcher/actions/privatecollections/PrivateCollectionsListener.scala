package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import com.fortysevendeg.ninecardslauncher.process.commons.models.PrivateCollection

trait PrivateCollectionsListener {

  def saveCollection(privateCollection: PrivateCollection): Unit

}
