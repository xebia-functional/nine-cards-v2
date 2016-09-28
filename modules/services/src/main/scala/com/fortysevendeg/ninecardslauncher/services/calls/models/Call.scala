package cards.nine.services.calls.models

import cards.nine.services.commons.PhoneCategory

case class Call(
  number: String,
  name: Option[String] = None,
  numberType: PhoneCategory,
  date: Long,
  callType: Int)
