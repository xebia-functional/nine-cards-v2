package com.fortysevendeg.ninecardslauncher.process.moment.models

case class Moment(
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean)

case class MomentTimeSlot(
  from: String,
  to: String,
  days: Seq[Int])

case class App(
  name: String,
  packageName: String,
  className: String,
  imagePath: String)
