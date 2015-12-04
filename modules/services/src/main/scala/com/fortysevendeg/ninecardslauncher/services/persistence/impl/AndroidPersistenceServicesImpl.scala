package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.persistence.AndroidIdNotFoundException
import rapture.core.Result

import scalaz.concurrent.Task

trait AndroidPersistenceServicesImpl {

  val androidId = "android_id"

  val contentGServices = "content://com.google.android.gsf.gservices"

  def getAndroidId(implicit context: ContextSupport) =
    Service {
      Task {
        val cursor = Option(context.getContentResolver.query(Uri.parse(contentGServices), null, null, Array(androidId), null))
        val result = cursor filter (c => c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(1).toHexString.toUpperCase)
        cursor foreach (_.close())
        result map {
          Result.answer[String, AndroidIdNotFoundException]
        } getOrElse Result.errata[String, AndroidIdNotFoundException](AndroidIdNotFoundException(message = "Android Id not found"))
      }
    }
}
