package com.fortysevendeg.ninecardslauncher.app.ui.commons.models

import com.fortysevendeg.ninecardslauncher.app.ui.commons.HeaderUtils
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact

import scala.annotation.tailrec

case class ContactHeadered(contact: Option[Contact] = None, header: Option[String] = None)

object ContactHeadered
  extends HeaderUtils {

  def generateContactsForList(contacts: Seq[Contact]): Seq[ContactHeadered] =
    generateContactsForList(contacts, Seq.empty)

  @tailrec
  private[this] def generateContactsForList(contacts: Seq[Contact], acc: Seq[ContactHeadered]): Seq[ContactHeadered] = contacts match {
    case Nil => acc
    case Seq(h, t@_ *) =>
      val currentChar: String = getCurrentChar(h.name)
      val lastChar: Option[String] = for {
        contactHeadered <- acc.lastOption
        contact <- contactHeadered.contact
        contactName <- Option(Option(contact.name) getOrElse charUnnamed)
        c <- Option(generateChar(contactName.substring(0, 1)))
      } yield c
      val skipChar = lastChar exists (_ equals currentChar)
      if (skipChar) {
        generateContactsForList(t, acc :+ ContactHeadered(contact = Option(h)))
      } else {
        generateContactsForList(t, acc ++ Seq(ContactHeadered(header = Option(currentChar)), ContactHeadered(contact = Option(h))))
      }
  }

}