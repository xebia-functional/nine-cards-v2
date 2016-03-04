package com.fortysevendeg.ninecardslauncher.process.commons.types

import com.fortysevendeg.ninecardslauncher.services.commons.{
  PhoneCategory => ServicesPhoneCategory,
  PhoneHome => ServicesPhoneHome,
  PhoneWork => ServicesPhoneWork,
  PhoneMobile=> ServicesPhoneMobile,
  PhoneMain => ServicesPhoneMain,
  PhoneFaxWork => ServicesPhoneFaxWork,
  PhoneFaxHome=> ServicesPhoneFaxHome,
  PhonePager => ServicesPhonePager,
  PhoneOther => ServicesPhoneOther}

sealed trait PhoneCategory

case object PhoneHome extends PhoneCategory

case object PhoneWork extends PhoneCategory

case object PhoneMobile extends PhoneCategory

case object PhoneMain extends PhoneCategory

case object PhoneFaxWork extends PhoneCategory

case object PhoneFaxHome extends PhoneCategory

case object PhonePager extends PhoneCategory

case object PhoneOther extends PhoneCategory

object PhoneCategory {

  def apply(servicesPhoneCategory: ServicesPhoneCategory): PhoneCategory = servicesPhoneCategory match {
    case ServicesPhoneHome => PhoneHome
    case ServicesPhoneWork => PhoneWork
    case ServicesPhoneMobile => PhoneMobile
    case ServicesPhoneMain => PhoneMain
    case ServicesPhoneFaxWork => PhoneFaxWork
    case ServicesPhoneFaxHome => PhoneFaxHome
    case ServicesPhonePager => PhonePager
    case ServicesPhoneOther => PhoneOther
  }
}