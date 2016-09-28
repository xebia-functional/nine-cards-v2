package cards.nine.process.commons.types

import cards.nine.services.commons.{
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