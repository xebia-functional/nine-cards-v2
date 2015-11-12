package com.fortysevendeg.ninecardslauncher.services.calls.models

import com.fortysevendeg.ninecardslauncher.services.commons.PhoneCategory

case class Call(
  number: String,
  name: Option[String] = None,
  numberType: PhoneCategory,
  date: Long,
  callType: Int)
