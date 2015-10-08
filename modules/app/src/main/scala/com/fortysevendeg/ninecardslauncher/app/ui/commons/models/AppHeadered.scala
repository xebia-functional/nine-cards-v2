package com.fortysevendeg.ninecardslauncher.app.ui.commons.models

import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.ItemHeadered
import com.fortysevendeg.ninecardslauncher.app.ui.commons.HeaderUtils
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized

import scala.annotation.tailrec

import scala.math.Ordering.Implicits._

case class AppHeadered(item: Option[AppCategorized] = None, header: Option[String] = None)
  extends ItemHeadered[AppCategorized]

object AppHeadered
  extends HeaderUtils {

  def generateAppHeaderedListByCategory(category: String, apps: Seq[AppCategorized]) =
    AppHeadered(header = Option(category)) +: (apps sortBy sortByName map (app => AppHeadered(item = Option(app))))

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
        app <- appsHeadered.item
        appName <- Option(Option(app.name) getOrElse charUnnamed)
        c <- Option(generateChar(appName.substring(0, 1)))
      } yield c
      val skipChar = lastChar exists (_ equals currentChar)
      if (skipChar) {
        generateAppsForList(t, acc :+ AppHeadered(item = Option(h)))
      } else {
        generateAppsForList(t, acc ++ Seq(AppHeadered(header = Option(currentChar)), AppHeadered(item = Option(h))))
      }
  }

}