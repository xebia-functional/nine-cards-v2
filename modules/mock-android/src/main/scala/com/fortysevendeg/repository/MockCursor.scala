package com.fortysevendeg.repository

import java.lang.Math._

import android.database.Cursor
import org.mockito.Mockito._
import org.specs2.mock.Mockito

sealed trait CursorDataType

case object ArrayByteDataType extends CursorDataType

case object DoubleDataType extends CursorDataType

case object FloatDataType extends CursorDataType

case object IntDataType extends CursorDataType

case object LongDataType extends CursorDataType

case object ShortDataType extends CursorDataType

case object StringDataType extends CursorDataType

trait MockCursor extends Mockito {

  val mockCursor = mock[Cursor]

  def mockIsAfterLast(size: Int) = {
    val isAfterLastData = Seq.fill(size)(false) :+ true

    when(mockCursor.isAfterLast).thenReturn(isAfterLastData.head, isAfterLastData.tail.toArray: _*)
  }

  def mockMoveToFirst(nonEmpty: Boolean) = when(mockCursor.moveToFirst).thenReturn(nonEmpty)

  def mockMoveToNext(size: Int) = {
    val sizeNext = max(0, size - 1)
    val isMoveToNextData = Seq.fill(sizeNext)(true) :+ false

    when(mockCursor.moveToNext).thenReturn(isMoveToNextData.head, isMoveToNextData.tail.toArray: _*)
  }

  def mockGetColumnIndex(columnName: String, index: Int) = when(mockCursor.getColumnIndex(columnName)).thenReturn(index)

  def mockGetData(index: Int, valueSeq: Seq[Any], cursorDataType: CursorDataType) =
    cursorDataType match {
      case ArrayByteDataType =>
        val arrayByteList = valueSeq.collect { case item: Array[Byte] => item }
        when(mockCursor.getBlob(index)).thenReturn(arrayByteList.head, arrayByteList.tail.toArray: _*)
      case DoubleDataType =>
        val doubleList = valueSeq.collect { case item: Double => item }
        when(mockCursor.getDouble(index)).thenReturn(doubleList.head, doubleList.tail.toArray: _*)
      case FloatDataType if valueSeq.nonEmpty =>
        val floatList = valueSeq.collect { case item: Float => item }
        when(mockCursor.getFloat(index)).thenReturn(floatList.head, floatList.tail.toArray: _*)
      case IntDataType if valueSeq.nonEmpty =>
        val intList = valueSeq.collect { case item: Int => item }
        when(mockCursor.getInt(index)).thenReturn(intList.head, intList.tail.toArray: _*)
      case LongDataType if valueSeq.nonEmpty =>
        val longList = valueSeq.collect { case item: Long => item }
        when(mockCursor.getLong(index)).thenReturn(longList.head, longList.tail.toArray: _*)
      case ShortDataType if valueSeq.nonEmpty =>
        val shortList = valueSeq.collect { case item: Short => item }
        when(mockCursor.getShort(index)).thenReturn(shortList.head, shortList.tail.toArray: _*)
      case StringDataType if valueSeq.nonEmpty =>
        val stringList = valueSeq.collect { case item: String => item }
        when(mockCursor.getString(index)).thenReturn(stringList.head, stringList.tail.toArray: _*)
    }

  def prepareCursor[T](size: Int, data: Seq[(String, Int, Seq[Any], CursorDataType)]) = {

    mockIsAfterLast(size = size)
    mockMoveToFirst(nonEmpty = size > 0)
    mockMoveToNext(size = size)

    data foreach {
      case (column, index, Nil, cursorDataType) =>
        mockGetColumnIndex(column, index)
      case (column, index, valueSeq, cursorDataType) =>
        mockGetColumnIndex(column, index)
        mockGetData(index, valueSeq, cursorDataType)
    }
  }
}
