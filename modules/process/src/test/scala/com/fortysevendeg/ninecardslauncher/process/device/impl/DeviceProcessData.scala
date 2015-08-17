package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.services.api.RequestConfig
import com.fortysevendeg.ninecardslauncher.services.api.models._
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.image.{AppPackagePath, AppWebsitePath}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.ShortCut

trait DeviceProcessData {

  val statusCodeOk = 200

  val name1 = "Scala Android"
  val packageName1 = "com.fortysevendeg.scala.android"
  val className1 = "ScalaAndroidActivity"
  val path1 = "/example/path1"

  val name2 = "Example"
  val packageName2 = "com.fortysevendeg.example"
  val className2 = "ExampleActivity"
  val path2 = "/example/path2"

  val name3 = "Scala Api"
  val packageName3 = "com.fortysevendeg.scala.api"
  val className3 = "ScalaApiActivity"
  val path3 = "/example/path3"

  val name4 = "Last App"
  val packageName4 = "com.fortysevendeg.scala.last"
  val className4 = "LastAppActivity"
  val path4 = "/example/path4"

  val applicationNoCached = Application(
    name = name4,
    packageName = packageName4,
    className = className4,
    icon = 4
  )

  val applications: Seq[Application] = Seq(
    Application(
      name = name1,
      packageName = packageName1,
      className = className1,
      icon = 2
    ),
    Application(
      name = name2,
      packageName = packageName2,
      className = className2,
      icon = 8
    ),
    Application(
      name = name3,
      packageName = packageName3,
      className = className3,
      icon = 4
    )
  )

  val newCacheCategory = CacheCategory(
    id = 4,
    packageName = packageName4,
    category = "SOCIAL",
    starRating = 4.5,
    numDownloads = "+100.000",
    ratingsCount = 5000,
    commentCount = 40000
  )

  val cacheCategories: Seq[CacheCategory] = Seq(
    CacheCategory(
      id = 1,
      packageName = packageName1,
      category = "SOCIAL",
      starRating = 3.5,
      numDownloads = "+500.000",
      ratingsCount = 78000,
      commentCount = 5000
    ),
    CacheCategory(
      id = 2,
      packageName = packageName2,
      category = "COMMUNICATION",
      starRating = 4.5,
      numDownloads = "+100.000",
      ratingsCount = 5000,
      commentCount = 40000
    ),
    CacheCategory(
      id = 3,
      packageName = packageName3,
      category = "TOOLS",
      starRating = 4,
      numDownloads = "+10.000.000",
      ratingsCount = 2300,
      commentCount = 34000
    )
  )

  val appPackagePathNoCached = AppPackagePath(
    packageName = packageName4,
    className = className4,
    path = path4)

  val appPathResponses: Seq[AppPackagePath] = Seq(
    AppPackagePath(
      packageName = packageName1,
      className = className1,
      path = path1),
    AppPackagePath(
      packageName = packageName2,
      className = className2,
      path = path2),
    AppPackagePath(
      packageName = packageName3,
      className = className3,
      path = path3)
  )

  val appCategorizedNoCached = AppCategorized(
    name = name4,
    packageName = packageName4,
    className = className4,
    imagePath = Option(path4),
    category = Option("SOCIAL"),
    starRating = Option(4.5),
    numDownloads = Option("+100.000"),
    ratingsCount = Option(5000),
    commentCount = Option(40000)
  )

  val appsCategorized: Seq[AppCategorized] = Seq(
    AppCategorized(
      name = name1,
      packageName = packageName1,
      className = className1,
      imagePath = Option(path1),
      category = Option("SOCIAL"),
      starRating = Option(3.5),
      numDownloads = Option("+500.000"),
      ratingsCount = Option(78000),
      commentCount = Option(5000)
    ),
    AppCategorized(
      name = name2,
      packageName = packageName2,
      className = className2,
      imagePath = Option(path2),
      category = Option("COMMUNICATION"),
      starRating = Option(4.5),
      numDownloads = Option("+100.000"),
      ratingsCount = Option(5000),
      commentCount = Option(40000)
    ),
    AppCategorized(
      name = name3,
      packageName = packageName3,
      className = className3,
      imagePath = Option(path3),
      category = Option("TOOLS"),
      starRating = Option(4),
      numDownloads = Option("+10.000.000"),
      ratingsCount = Option(2300),
      commentCount = Option(34000)
    )
  )

  val googlePlaySimplePackageNoCached = GooglePlaySimplePackage(
    packageName = packageName4,
    appType = "APP",
    appCategory = "SOCIAL",
    starRating = 4.5,
    numDownloads = "+100.000",
    ratingCount = 5000,
    commentCount = 40000
  )

  val requestConfig = RequestConfig("fake-device-id", "fake-token")

  val packageNameForCreateImage = "com.example"

  val pathForCreateImage =  "/example/for/create/image"

  val urlForCreateImage = "http://www.w.com/image.jpg"

  val appWebsitePath = AppWebsitePath(
    packageName = packageNameForCreateImage,
    url = urlForCreateImage,
    path = pathForCreateImage
  )

  val googlePlayPackage = GooglePlayPackage(
    GooglePlayApp(
      docid = packageNameForCreateImage,
      title = "",
      creator = "",
      descriptionHtml = None,
      icon = Some(urlForCreateImage),
      background = None,
      screenshots = Seq.empty,
      video = None,
      details = GooglePlayDetails(GooglePlayAppDetails(Seq("SOCIAL"), "", None, None, None, 1, Option("1"), None, Seq.empty)),
      offer = Seq.empty,
      aggregateRating = GooglePlayAggregateRating(0, None, 0, 0, 0, 0, 0, 0)
    )
  )

  val shortCuts: Seq[ShortCut] = Seq(
    ShortCut(
      title = "ShortCut 1",
      icon = 0,
      name = "ShortCut 1",
      packageName = "com.example.shortcut1",
      className = "ClassName1"
    ),
    ShortCut(
      title = "ShortCut 2",
      icon = 0,
      name = "ShortCut 2",
      packageName = "com.example.shortcut2",
      className = "ClassName2"
    ),
    ShortCut(
      title = "ShortCut 3",
      icon = 0,
      name = "ShortCut 3",
      packageName = "com.example.shortcut3",
      className = "ClassName3"
    )
  )

}
