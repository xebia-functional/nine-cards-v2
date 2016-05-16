package com.fortysevendeg.ninecardslauncher.app.ui.commons.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.fortysevendeg.ninecardslauncher.process.device.models.App

class AppIconLoader
  extends ModelLoader[App, App] {

  override def getResourceFetcher(model: App, width: Int, height: Int): DataFetcher[App] =
    new DataFetcher[App]() {
      override def cleanup(): Unit = {}

      override def loadData(priority: Priority): App = model

      override def cancel(): Unit = {}

      override def getId: String = model.packageName
    }

}
