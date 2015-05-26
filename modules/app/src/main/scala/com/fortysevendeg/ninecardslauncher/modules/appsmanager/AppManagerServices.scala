package com.fortysevendeg.ninecardslauncher.modules.appsmanager

import com.fortysevendeg.ninecardslauncher.models.{AppItem, NineCardIntent}

import scala.concurrent.{Future, ExecutionContext}

trait AppManagerServices {

  def getApps()(implicit executionContext: ExecutionContext): Future[Seq[AppItem]]

  def createBitmapsForNoPackagesInstalled(intents: Seq[NineCardIntent])(implicit executionContext: ExecutionContext): Future[Seq[String]]

  def getCategorizedApps()(implicit executionContext: ExecutionContext): Future[Seq[AppItem]]

  def getAppsByCategory(category: String)(implicit executionContext: ExecutionContext): Future[Seq[AppItem]]

  def categorizeApps()(implicit executionContext: ExecutionContext): Future[Unit]
}
