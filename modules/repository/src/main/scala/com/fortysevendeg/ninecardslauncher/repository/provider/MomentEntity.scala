package com.fortysevendeg.ninecardslauncher.repository.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.repository.model.Moment
import com.fortysevendeg.ninecardslauncher.repository.Conversions._

case class MomentEntity(id: Int, data: MomentEntityData)

case class MomentEntityData(
  collectionId: Int,
  timeslot: String,
  wifi: String,
  headphone: Boolean,
  momentType : String)

object MomentEntity {
  val table = "Moment"
  val collectionId = "collectionId"
  val timeslot = "timeslot"
  val wifi = "wifi"
  val headphone = "headphone"
  val momentType = "momentType"

  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    collectionId,
    timeslot,
    wifi,
    headphone)

  def momentEntityFromCursor(cursor: Cursor): MomentEntity =
    MomentEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = MomentEntityData(
        collectionId = cursor.getInt(cursor.getColumnIndex(collectionId)),
        timeslot = cursor.getString(cursor.getColumnIndex(timeslot)),
        wifi = cursor.getString(cursor.getColumnIndex(wifi)),
        headphone = cursor.getInt(cursor.getColumnIndex(headphone)) > 0,
        momentType = cursor.getString(cursor.getColumnIndex(momentType))))


  def momentFromCursor(cursor: Cursor): Moment = toMoment(momentEntityFromCursor(cursor))

  def createTableSQL: String =
    s"""CREATE TABLE ${MomentEntity.table}
        |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
        |${MomentEntity.collectionId} INTEGER,
        |${MomentEntity.timeslot} TEXT not null,
        |${MomentEntity.wifi} TEXT not null,
        |${MomentEntity.headphone} INTEGER not null),
        |${MomentEntity.momentType} TEXT""".stripMargin
}
