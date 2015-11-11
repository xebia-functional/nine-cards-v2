package com.fortysevendeg.ninecardslauncher.services.calls.models

import com.fortysevendeg.ninecardslauncher.services.commons.PhoneCategory

case class Call (
  number: String,
  name: Option[String] = None,
  numberType: PhoneCategory,
  date: String,
  callType: CallType)

sealed trait CallType

case object IncomingType extends CallType

case object OutgoingType extends CallType

case object MissedType extends CallType

case object OtherType extends CallType
