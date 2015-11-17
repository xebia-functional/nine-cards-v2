package com.fortysevendeg.ninecardslauncher.process.device.types

import android.provider.CallLog

sealed trait CallType

case object IncomingType extends CallType

case object OutgoingType extends CallType

case object MissedType extends CallType

case object OtherType extends CallType

object CallType {

  def apply(mode: Int): CallType = mode match {
    case CallLog.Calls.INCOMING_TYPE => IncomingType
    case CallLog.Calls.OUTGOING_TYPE => OutgoingType
    case CallLog.Calls.MISSED_TYPE => MissedType
    case _ => OtherType
  }

}
