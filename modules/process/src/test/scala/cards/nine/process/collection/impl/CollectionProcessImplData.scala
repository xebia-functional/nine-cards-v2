package cards.nine.process.collection.impl

import cards.nine.commons.test.data.ApplicationTestData
import cards.nine.models._
import cards.nine.services.api.{CategorizedDetailPackage, RankAppsResponse, RankAppsResponseList}

trait CollectionProcessImplData extends ApplicationTestData {

  val latitude: Double = 47
  val longitude: Double = 36
  val statusCodeOk: Int = 200

  val countryCode: String = "countryCode"
  val countryName: String = "countryName"
  val street: String = "street"
  val city: String = "city"
  val postalCode: String = "postalCode"
  val icon: String = "icon"
  val free: Boolean = true
  val downloads: String = "100"
  val stars: Double = 4.5

  val awarenessLocation =
    Location(
      latitude = latitude,
      longitude = longitude,
      countryCode = Some(countryCode),
      countryName = Some(countryName),
      addressLines = Seq(street, city, postalCode))

  val categorizedDetailPackages = seqApplication map { app =>
    CategorizedDetailPackage(
      packageName = app.packageName,
      title = app.name,
      category = Option(app.category.name),
      icon = icon,
      free = free,
      downloads = downloads,
      stars = stars)
  }

  val seqCategoryAndPackages =
    (seqApplication map (app => (app.category, app.packageName))).groupBy(_._1).mapValues(_.map(_._2)).toSeq

  def generateRankAppsResponse() = seqCategoryAndPackages map { item =>
    RankAppsResponse(
      category = item._1.name,
      packages = item._2)
  }

  val rankAppsResponseList = RankAppsResponseList(
    statusCode = statusCodeOk,
    items = generateRankAppsResponse())

  val packagesByCategory =
    seqCategoryAndPackages map { item =>
      PackagesByCategory(
        category = item._1,
        packages = item._2)
    }

}
