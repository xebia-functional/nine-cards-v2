package cards.nine.models

case class Collection(
  id: Int,
  position: Int,
  name: String,
  collectionType: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Boolean,
  cards: Seq[Card] = Seq.empty,
  moment: Option[Moment])

case class Card(
  id: Int,
  position: Int,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: Option[String],
  notification: Option[String] = None)

case class User(
  id: Int,
  email: Option[String],
  apiKey: Option[String],
  sessionToken: Option[String],
  deviceToken: Option[String],
  marketToken: Option[String],
  name: Option[String],
  avatar: Option[String],
  cover: Option[String],
  deviceName: Option[String],
  deviceCloudId: Option[String])

case class DockApp(
  id: Int,
  name: String,
  dockType: String,
  intent: String,
  imagePath: String,
  position: Int)

case class Moment(
  id: Int,
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[String])

case class MomentTimeSlot(
  from: String,
  to: String,
  days: Seq[Int])

case class DataCounter(term: String, count: Int)

case class PersistenceWidget(
  id: Int,
  momentId: Int,
  packageName: String,
  className: String,
  appWidgetId: Option[Int],
  startX: Int,
  startY: Int,
  spanX: Int,
  spanY: Int,
  widgetType: String,
  label: Option[String],
  imagePath: Option[String],
  intent: Option[String])