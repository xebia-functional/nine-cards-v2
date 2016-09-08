package com.fortysevendeg.ninecardslauncher.process.trackevent.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.process.trackevent._
import com.fortysevendeg.ninecardslauncher.services.analytics.{AnalyticEvent, AnalyticsServices}

class TrackEventProcessImpl(analyticsServices: AnalyticsServices)
  extends TrackEventProcess
  with ImplicitsTrackEventException {

  override def openAppFromAppDrawer(packageName: String, category: Category) = {
    val event = AnalyticEvent(
      screen = LauncherScreen.name,
      category = category.name,
      action = OpenAction.name,
      label = Option(packageName),
      value = Option(OpenAppFromAppDrawerValue.value))
    analyticsServices.trackEvent(event).resolve[TrackEventException]
  }

  override def openAppFromCollection(packageName: String, category: Category) = {
    val event = AnalyticEvent(
      screen = CollectionDetailScreen.name,
      category = category.name,
      action = OpenCardAction.name,
      label = Option(packageName),
      value = Option(OpenAppFromCollectionValue.value))
    analyticsServices.trackEvent(event).resolve[TrackEventException]
  }

  def addToCollection(packageName: String, category: Category) = {
    val event = AnalyticEvent(
      screen = CollectionDetailScreen.name,
      category = category.name,
      action = AddedToCollectionAction.name,
      label = Option(packageName),
      value = Option(AddedToCollectionValue.value))
    analyticsServices.trackEvent(event).resolve[TrackEventException]
  }

  def removedInCollection(packageName: String, category: Category) = {
    val event = AnalyticEvent(
      screen = CollectionDetailScreen.name,
      category = category.name,
      action = RemovedInCollectionAction.name,
      label = Option(packageName),
      value = Option(RemovedInCollectionValue.value))
    analyticsServices.trackEvent(event).resolve[TrackEventException]
  }

}
