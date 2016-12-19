package cards.nine.repository

import cards.nine.repository.model._
import cards.nine.repository.provider._

object Conversions {

  def toApp(app: AppEntity): App =
    App(
      id = app.id,
      data = AppData(
        name = app.data.name,
        packageName = app.data.packageName,
        className = app.data.className,
        category = app.data.category,
        dateInstalled = app.data.dateInstalled,
        dateUpdate = app.data.dateUpdate,
        version = app.data.version,
        installedFromGooglePlay = app.data.installedFromGooglePlay))

  def toCard(cardEntity: CardEntity): Card =
    Card(
      id = cardEntity.id,
      data = CardData(
        position = cardEntity.data.position,
        term = cardEntity.data.term,
        packageName = Option[String](cardEntity.data.packageName),
        cardType = cardEntity.data.`type`,
        intent = cardEntity.data.intent,
        imagePath = Option[String](cardEntity.data.imagePath),
        notification = Option[String](cardEntity.data.notification)))

  def toCollection(collectionEntity: CollectionEntity): Collection =
    Collection(
      id = collectionEntity.id,
      data = CollectionData(
        position = collectionEntity.data.position,
        name = collectionEntity.data.name,
        collectionType = collectionEntity.data.`type`,
        icon = collectionEntity.data.icon,
        themedColorIndex = collectionEntity.data.themedColorIndex,
        appsCategory = Option[String](collectionEntity.data.appsCategory),
        originalSharedCollectionId =
          Option[String](collectionEntity.data.originalSharedCollectionId),
        sharedCollectionId = Option[String](collectionEntity.data.sharedCollectionId),
        sharedCollectionSubscribed =
          Option[Boolean](collectionEntity.data.sharedCollectionSubscribed)))

  def toDockApp(dockAppEntity: DockAppEntity): DockApp =
    DockApp(
      id = dockAppEntity.id,
      data = DockAppData(
        name = dockAppEntity.data.name,
        dockType = dockAppEntity.data.dockType,
        intent = dockAppEntity.data.intent,
        imagePath = dockAppEntity.data.imagePath,
        position = dockAppEntity.data.position))

  def toMoment(momentEntity: MomentEntity): Moment =
    Moment(
      id = momentEntity.id,
      data = MomentData(
        collectionId = momentEntity.data.collectionId,
        timeslot = momentEntity.data.timeslot,
        wifi = momentEntity.data.wifi,
        headphone = momentEntity.data.headphone,
        momentType = Option[String](momentEntity.data.momentType)))

  def toUser(userEntity: UserEntity): User =
    User(
      id = userEntity.id,
      data = UserData(
        email = Option[String](userEntity.data.email),
        apiKey = Option[String](userEntity.data.apiKey),
        sessionToken = Option[String](userEntity.data.sessionToken),
        deviceToken = Option[String](userEntity.data.deviceToken),
        marketToken = Option[String](userEntity.data.marketToken),
        name = Option[String](userEntity.data.name),
        avatar = Option[String](userEntity.data.avatar),
        cover = Option[String](userEntity.data.cover),
        deviceName = Option[String](userEntity.data.deviceName),
        deviceCloudId = Option[String](userEntity.data.deviceCloudId)))

  def toWidget(widget: WidgetEntity): Widget =
    Widget(
      id = widget.id,
      data = WidgetData(
        momentId = widget.data.momentId,
        packageName = widget.data.packageName,
        className = widget.data.className,
        appWidgetId = widget.data.appWidgetId,
        startX = widget.data.startX,
        startY = widget.data.startY,
        spanX = widget.data.spanX,
        spanY = widget.data.spanY,
        widgetType = widget.data.widgetType,
        label = Option[String](widget.data.label),
        imagePath = Option[String](widget.data.imagePath),
        intent = Option[String](widget.data.intent)))

}
