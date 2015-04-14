package com.fortysevendeg.ninecardslauncher.commons

import android.content.ContentResolver

trait ContentResolverProvider {

  implicit val contentResolver : ContentResolver

}
