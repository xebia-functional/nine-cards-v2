package cards.nine.models

import cards.nine.models.types.{CallType, PhoneCategory}

case class Call(
    number: String,
    name: Option[String] = None,
    numberType: PhoneCategory,
    date: Long,
    callType: CallType)
