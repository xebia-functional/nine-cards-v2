package com.fortysevendeg.ninecardslauncher.services.commons

sealed trait PhoneCategory

case object PhoneHome extends PhoneCategory

case object PhoneWork extends PhoneCategory

case object PhoneMobile extends PhoneCategory

case object PhoneOther extends PhoneCategory

