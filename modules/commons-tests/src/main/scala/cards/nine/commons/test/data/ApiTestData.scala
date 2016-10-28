package cards.nine.commons.test.data

import cards.nine.commons.test.data.ApiValues._
import cards.nine.commons.test.data.AppWidgetValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.MomentValues._
import cards.nine.commons.test.data.UserValues._
import cards.nine.models._
import cards.nine.models.types.{NineCardsMoment, NineCardsCategory}

trait ApiTestData
  extends ApplicationTestData
  with AppWidgetTestData {

  implicit val requestConfig = RequestConfig(
    apiKey = apiKey,
    sessionToken = sessionToken,
    androidId = androidId,
    marketToken = Option(marketToken))

  val loginResponse = LoginResponse(
    apiKey = apiKey,
    sessionToken = sessionToken)

  val awarenessLocation = Location(
    latitude = latitude,
    longitude = longitude,
    countryCode = Option(countryCode),
    countryName = Option(countryName),
    addressLines = Seq(street, city, postalCode))

  def categorizedPackage(num: Int = 0) = CategorizedPackage(
    packageName = apiPackageName + num,
    category = Option(category))

  val categorizedPackage: CategorizedPackage = categorizedPackage(0)
  val seqCategorizedPackage: Seq[CategorizedPackage]  = Seq(categorizedPackage(0), categorizedPackage(1), categorizedPackage(2))

  val categorizedDetailPackages = seqApplication map { app =>
    CategorizedDetailPackage(
      packageName = app.packageName,
      title = app.name,
      category = Option(app.category),
      icon = apiIcon,
      free = free,
      downloads = downloads,
      stars = stars)
  }

  val seqCategoryAndPackages: Seq[(NineCardsCategory, Seq[String])] =
    (seqApplication map (app => (app.category, app.packageName))).groupBy(_._1).mapValues(_.map(_._2)).toSeq

  val seqRankApps: Seq[RankApps] = seqCategoryAndPackages map { item =>
    RankApps(
      category = item._1,
      packages = item._2)
  }

  val seqPackagesByCategory =
    seqCategoryAndPackages map { item =>
      PackagesByCategory(
        category = item._1,
        packages = item._2)
    }

  def notCategorizedPackage(num: Int = 0) = NotCategorizedPackage(
    packageName = apiPackageName + num,
    title = apiTitle + num,
    downloads = downloads,
    icon = Option(apiIcon),
    stars = stars,
    free = free,
    screenshots = screenshots)

  val notCategorizedPackage: NotCategorizedPackage = notCategorizedPackage(0)
  val seqNotCategorizedPackage: Seq[NotCategorizedPackage] =
    Seq(notCategorizedPackage(0), notCategorizedPackage(1), notCategorizedPackage(2))

  def rankAppsByMoment(num: Int = 0) = RankAppsByMoment(
    moment = NineCardsMoment(momentTypeSeq(num)),
    packages = Seq(apiPackages(num)))

  val seqRankAppsByMoment: Seq[RankAppsByMoment] =
    Seq(rankAppsByMoment(0), rankAppsByMoment(1), rankAppsByMoment(2))

  def packagesByMoment(num: Int = 0) = PackagesByMoment(
    moment = NineCardsMoment(momentTypeSeq(num)),
    packages = Seq(apiPackages(num)))

  val seqPackagesByMoment: Seq[PackagesByMoment] =
    Seq(packagesByMoment(0), packagesByMoment(1), packagesByMoment(2))

  def rankWidget(num: Int = 0) = RankWidget(
    packageName = appWidgetPackageName+ num,
    className = appWidgetClassName + num)

  val seqRankWidget: Seq[RankWidget] =
    Seq(rankWidget(0), rankWidget(1), rankWidget(2))

  def rankWidgetsByMoment(num: Int = 0) = RankWidgetsByMoment(
    moment = NineCardsMoment(momentTypeSeq(num)),
    widgets = Seq(seqRankWidget(num)))

  val seqRankWidgetsByMoment: Seq[RankWidgetsByMoment] =
    Seq(rankWidgetsByMoment(0), rankWidgetsByMoment(1), rankWidgetsByMoment(2))

  def widgetsByMoment(num: Int = 0) = WidgetsByMoment(
    moment = NineCardsMoment(momentTypeSeq(num)),
    widgets = Seq(seqAppWidget(num)))

  val seqWidgetsByMoment: Seq[WidgetsByMoment] =
    Seq(widgetsByMoment(0), widgetsByMoment(1), widgetsByMoment(2))

}
