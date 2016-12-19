package cards.nine.models

import cards.nine.models.types.{NineCardsCategory, NineCardsMoment}

case class CategorizedDetailPackage(
    packageName: String,
    title: String,
    category: Option[NineCardsCategory],
    icon: String,
    free: Boolean,
    downloads: String,
    stars: Double)

case class CategorizedPackage(packageName: String, category: Option[NineCardsCategory])

case class LoginResponse(apiKey: String, sessionToken: String)

case class RankApps(category: NineCardsCategory, packages: Seq[String])

case class RankAppsByMoment(moment: NineCardsMoment, packages: Seq[String])

case class RankWidget(packageName: String, className: String)

case class RankWidgetsByMoment(moment: NineCardsMoment, widgets: Seq[RankWidget])

case class NotCategorizedPackage(
    packageName: String,
    title: String,
    icon: Option[String],
    downloads: String,
    stars: Double,
    free: Boolean,
    screenshots: Seq[String])

case class RequestConfig(
    apiKey: String,
    sessionToken: String,
    androidId: String,
    marketToken: Option[String] = None)

case class RequestConfigV1(deviceId: String, token: String, marketToken: Option[String])
