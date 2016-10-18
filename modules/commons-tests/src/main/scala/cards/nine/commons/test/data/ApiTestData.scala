package cards.nine.commons.test.data

import cards.nine.commons.test.data.ApiValues._
import cards.nine.models._
import cards.nine.models.types.NineCardsCategory

trait ApiTestData extends ApplicationTestData {

  val awarenessLocation = Location(
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
      icon = apiIcon,
      free = free,
      downloads = downloads,
      stars = stars)
  }

  val seqCategoryAndPackages: Seq[(NineCardsCategory, Seq[String])] =
    (seqApplication map (app => (app.category, app.packageName))).groupBy(_._1).mapValues(_.map(_._2)).toSeq

  val rankApps: Seq[RankApps] = seqCategoryAndPackages map { item =>
    RankApps(
      category = item._1.name,
      packages = item._2)
  }

  val packagesByCategory =
    seqCategoryAndPackages map { item =>
      PackagesByCategory(
        category = item._1,
        packages = item._2)
    }


}
