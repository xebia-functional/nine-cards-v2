package com.fortysevendeg.ninecardslauncher.app.ui.commons.header

import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.HeaderUtils
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.{AppHeadered, ContactHeadered}
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, Contact}
import com.fortysevendeg.ninecardslauncher.process.device.{GetAppOrder, GetByCategory, GetByName, GetByInstallDate}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

import scala.annotation.tailrec

trait HeaderGenerator extends HeaderUtils {

  val headerUpdated1 = R.string.apps_header_updated_1

  val headerUpdated2 = R.string.apps_header_updated_2

  val headerUpdated3 = R.string.apps_header_updated_3

  val headerUpdated4 = R.string.apps_header_updated_4

  val headerUpdated4Alt = R.string.apps_date

  val dayDiffMillis = 1000 * 60 * 60 * 24

  val weekDiffMillis = dayDiffMillis * 7

  val monthDiffMillis = dayDiffMillis * 30

  def generateHeaderList(apps: Seq[App], getAppOrder: GetAppOrder)(implicit contextWrapper: ContextWrapper): Seq[AppHeadered] =
    generateHeaderList(apps = apps, getAppOrder = getAppOrder, actualSeq = Seq.empty, previousHeaders = Seq.empty)

  private[this] def generateHeaderList(
      apps: Seq[App],
      getAppOrder: GetAppOrder,
      actualSeq: Seq[AppHeadered],
      previousHeaders: Seq[String])(implicit contextWrapper: ContextWrapper): Seq[AppHeadered] = apps match {
    case Nil => actualSeq
    case Seq(h, t@_ *) =>
      val header = generateAppHeader(h, getAppOrder, previousHeaders)
      if (previousHeaders.contains(header)) {
        generateHeaderList(
          apps = t,
          getAppOrder = getAppOrder,
          actualSeq = actualSeq :+ AppHeadered(item = Option(h)),
          previousHeaders = previousHeaders)
      } else {
        generateHeaderList(
          apps = t,
          getAppOrder = getAppOrder,
          actualSeq = actualSeq :+ AppHeadered(header = Some(header)) :+ AppHeadered(item = Option(h)),
          previousHeaders = previousHeaders :+ header)
      }

  }

  private[this] def generateAppHeader(
      app: App,
      getAppOrder: GetAppOrder,
      previousHeaders: Seq[String])(implicit cw: ContextWrapper): String =
    getAppOrder match {
      case GetByName(_) => getCurrentChar(app.name)
      case GetByCategory(_) =>
        val appCategory = if (gamesCategories.contains(app.category)) game else app.category
        resGetString(appCategory.toLowerCase) getOrElse appCategory.toLowerCase
      case GetByInstallDate(_) =>
        val diff = System.currentTimeMillis() - app.dateInstalled
        resGetString(findStringForDiff(diff, previousHeaders.isEmpty))
    }

  private[this] def findStringForDiff(diff: Long, firstOne: Boolean): Int = diff match {
    case d if d <= dayDiffMillis => headerUpdated1
    case d if d <= weekDiffMillis => headerUpdated2
    case d if d <= monthDiffMillis => headerUpdated3
    case _ => if (firstOne) headerUpdated4Alt else headerUpdated4
  }

  def generateContactsForList(contacts: Seq[Contact]): Seq[ContactHeadered] =
    generateContactsForList(contacts = contacts, actualSeq = Seq.empty, lastHeader = None)

  @tailrec
  private[this] def generateContactsForList(
      contacts: Seq[Contact],
      actualSeq: Seq[ContactHeadered],
      lastHeader: Option[String]): Seq[ContactHeadered] = contacts match {
    case Nil => actualSeq
    case Seq(h, t @ _ *) =>
      val header: String = getCurrentChar(h.name)
      if (lastHeader.contains(header)) {
        generateContactsForList(
          contacts = t,
          actualSeq = actualSeq :+ ContactHeadered(item = Option(h)),
          lastHeader = lastHeader)
      } else {
        generateContactsForList(
          contacts = t,
          actualSeq = actualSeq :+ ContactHeadered(header = Option(header)) :+ ContactHeadered(item = Option(h)),
          lastHeader = Some(header))
      }
  }


}
