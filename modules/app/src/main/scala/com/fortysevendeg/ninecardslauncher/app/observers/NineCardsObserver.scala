package com.fortysevendeg.ninecardslauncher.app.observers

import android.database.ContentObserver
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons._

class NineCardsObserver extends ContentObserver(javaNull) {

  override def onChange(selfChange: Boolean, uri: Uri): Unit = {
    android.util.Log.d("9Cards", s"onChange ${Option(uri).map(_.toString).getOrElse("")}")
  }
}
