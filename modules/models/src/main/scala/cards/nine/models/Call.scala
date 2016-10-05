package cards.nine.models

import cards.nine.models.types.PhoneCategory

case class Call(
  number: String,
  name: Option[String] = None,
  numberType: PhoneCategory,
  date: Long,
  callType: Int)
