package com.fortysevendeg.ninecardslauncher.app.ui.commons.models

import com.fortysevendeg.ninecardslauncher.app.ui.commons.HeaderUtils
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized

import scala.annotation.tailrec

import scala.math.Ordering.Implicits._

case class AppHeadered(app: Option[AppCategorized] = None, header: Option[String] = None)

object AppHeadered
  extends HeaderUtils {

  def generateAppHeaderedList(apps: Seq[AppCategorized]): Seq[AppHeadered] =
    generateAppsForList(apps sortBy sortByName, Seq.empty)

  private[this] def sortByName(app: AppCategorized) = app.name map (c => if (c.isUpper) 2 * c + 1 else 2 * (c - ('a' - 'A')))

  @tailrec
  private[this] def generateAppsForList(apps: Seq[AppCategorized], acc: Seq[AppHeadered]): Seq[AppHeadered] = apps match {
    case Nil => acc
    case Seq(h, t @ _ *) =>
      val currentChar: String = getCurrentChar(h.name)
      val lastChar: Option[String] = for {
        appsHeadered <- acc.lastOption
        app <- appsHeadered.app
        appName <- Option(Option(app.name) getOrElse charUnnamed)
        c <- Option(generateChar(appName.substring(0, 1)))
      } yield c
      val skipChar = lastChar exists (_ equals currentChar)
      if (skipChar) {
        generateAppsForList(t, acc :+ AppHeadered(app = Option(h)))
      } else {
        generateAppsForList(t, acc ++ Seq(AppHeadered(header = Option(currentChar)), AppHeadered(app = Option(h))))
      }
  }

}