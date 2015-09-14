package com.fortysevendeg.ninecardslauncher.app.ui.commons.models

import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized

import scala.annotation.tailrec

import scala.math.Ordering.Implicits._

case class AppHeadered(app: Option[AppCategorized] = None, header: Option[String] = None)

object AppHeadered {

  def generateAppHeaderedList(apps: Seq[AppCategorized]): Seq[AppHeadered] =
    generateAppsForList(apps sortBy sortByName, Seq.empty)

  private[this] def sortByName(app: AppCategorized) = app.name map (c => if (c.isUpper) 2 * c + 1 else 2 * (c - ('a' - 'A')))

  @tailrec
  private[this] def generateAppsForList(apps: Seq[AppCategorized], acc: Seq[AppHeadered]): Seq[AppHeadered] = apps match {
    case Nil => acc
    case h :: t =>
      val currentChar = h.name.substring(0, 1).toUpperCase
      val lastChar = acc.lastOption flatMap (_.app map (_.name.substring(0, 1).toUpperCase))
      val skipChar = lastChar exists (_ equals currentChar)
      val seqHeadered = if (skipChar) {
        acc :+ AppHeadered(app = Option(h))
      } else {
        acc ++ Seq(AppHeadered(header = Option(currentChar)), AppHeadered(app = Option(h)))
      }
      generateAppsForList(t, seqHeadered)
  }

}