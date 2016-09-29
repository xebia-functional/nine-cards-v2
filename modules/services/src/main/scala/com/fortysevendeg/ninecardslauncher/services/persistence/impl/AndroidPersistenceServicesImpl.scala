package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import android.database.Cursor
import android.net.Uri
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.javaNull
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.services.persistence.{AndroidIdNotFoundException, PersistenceServices}
import monix.eval.Task

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
          case Some(r) => Right(r)
          case _ => Left(AndroidIdNotFoundException("Android Id not found"))
        }
      }
    }
}
