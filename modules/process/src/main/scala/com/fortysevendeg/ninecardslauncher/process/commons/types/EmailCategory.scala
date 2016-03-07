package com.fortysevendeg.ninecardslauncher.process.commons.types

import com.fortysevendeg.ninecardslauncher.services.commons.{
  EmailCategory => ServicesEmailCategory,
  EmailHome => ServicesEmailHome,
  EmailWork => ServicesEmailWork,
  EmailOther => ServicesEmailOther}

sealed trait EmailCategory

case object EmailHome extends EmailCategory

case object EmailWork extends EmailCategory

case object EmailOther extends EmailCategory

object EmailCategory {

  def apply(servicesEmailCategory: ServicesEmailCategory): EmailCategory = servicesEmailCategory match {
    case ServicesEmailHome => EmailHome
    case ServicesEmailWork => EmailWork
    case ServicesEmailOther => EmailOther
  }
}