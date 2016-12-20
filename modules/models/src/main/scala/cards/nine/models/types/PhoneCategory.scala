package cards.nine.models.types

sealed trait PhoneCategory

case object PhoneHome extends PhoneCategory

case object PhoneWork extends PhoneCategory

case object PhoneMobile extends PhoneCategory

case object PhoneMain extends PhoneCategory

case object PhoneFaxWork extends PhoneCategory

case object PhoneFaxHome extends PhoneCategory

case object PhonePager extends PhoneCategory

case object PhoneOther extends PhoneCategory
