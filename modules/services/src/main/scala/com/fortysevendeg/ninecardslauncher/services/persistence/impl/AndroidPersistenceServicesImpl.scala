package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import android.database.Cursor
import android.net.Uri
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.services.persistence.{AndroidIdNotFoundException, PersistenceServices}

import scalaz.concurrent.Task

trait AndroidPersistenceServicesImpl extends PersistenceServices {

  val androidId = "android_id"

  val contentGServices = "content://com.google.android.gsf.gservices"

  def getAndroidId(implicit context: ContextSupport): TaskService[String] =
    TaskService {
      Task {
        val cursor: Option[Cursor] = Option(context.getContentResolver.query(Uri.parse(contentGServices), javaNull, javaNull, Array(androidId), javaNull))
        val result: Option[String] = cursor filter (c => c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(1).toHexString.toUpperCase)
        cursor foreach (_.close())
        result match {
          case Some(r) => Xor.Right(r)
          case _ => Xor.Left(AndroidIdNotFoundException("Android Id not found"))
        }
      }
    }
}
