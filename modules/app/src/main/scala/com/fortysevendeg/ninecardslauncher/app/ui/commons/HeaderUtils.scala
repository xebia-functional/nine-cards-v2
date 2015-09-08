package com.fortysevendeg.ninecardslauncher.app.ui.commons

trait HeaderUtils {

  val charUnnamed = "?"

  val charMisc = "#"

  val abc = "abcdefghijklmnñopqrstuvwxyz"

  val number = "0123456789"

  def generateChar(char: String) = char match {
    case c if abc.contains(c.toLowerCase) => c.toUpperCase
    case _ => charMisc
  }
}
