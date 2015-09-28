package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.services.api.RequestConfig
import com.fortysevendeg.ninecardslauncher.services.api.models._
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.contacts.models._
import com.fortysevendeg.ninecardslauncher.services.image.{AppPackagePath, AppWebsitePath}
import com.fortysevendeg.ninecardslauncher.services.persistence.AddAppRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{AppData, App, CacheCategory}
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.Shortcut

trait DeviceProcessData {

  val statusCodeOk = 200

  val name1 = "Scala Android"
  val packageName1 = "com.fortysevendeg.scala.android"
  val className1 = "ScalaAndroidActivity"
  val path1 = "/example/path1"
  val category1 = "category1"
  val imagePath1 = "imagePath1"
  val resourceIcon1 = 1
  val colorPrimary1 = "colorPrimary"
  val dateInstalled1 = 1d
  val dateUpdate1 = 1d
  val version1 = "22"
  val installedFromGooglePlay1 = true

  val name2 = "Example"
  val packageName2 = "com.fortysevendeg.example"
  val className2 = "ExampleActivity"
  val path2 = "/example/path2"
  val category2 = "category1"
  val imagePath2 = "imagePath1"
  val resourceIcon2 = 2
  val colorPrimary2 = "colorPrimary"
  val dateInstalled2 = 1d
  val dateUpdate2 = 1d
  val version2 = "22"
  val installedFromGooglePlay2 = true

  val name3 = "Scala Api"
  val packageName3 = "com.fortysevendeg.scala.api"
  val className3 = "ScalaApiActivity"
  val path3 = "/example/path3"
  val category3 = "category1"
  val imagePath3 = "imagePath1"
  val resourceIcon3 = 3
  val colorPrimary3 = "colorPrimary"
  val dateInstalled3 = 1d
  val dateUpdate3 = 1d
  val version3 = "22"
  val installedFromGooglePlay3 = true

  val name4 = "Last App"
  val packageName4 = "com.fortysevendeg.scala.last"
  val className4 = "LastAppActivity"
  val path4 = "/example/path4"
  val category4 = "category1"
  val imagePath4 = "imagePath1"
  val resourceIcon4 = 4
  val colorPrimary4 = "colorPrimary"
  val dateInstalled4 = 1d
  val dateUpdate4 = 1d
  val version4 = "22"
  val installedFromGooglePlay4 = true

  val applicationNoCached = Application(
    name = name4,
    packageName = packageName4,
    className = className4,
    resourceIcon = resourceIcon4,
    colorPrimary = colorPrimary4,
    dateInstalled = dateInstalled4,
    dateUpdate = dateUpdate4,
    version = version4,
    installedFromGooglePlay = installedFromGooglePlay4)

  val applications: Seq[Application] = Seq(
    Application(
      name = name1,
      packageName = packageName1,
      className = className1,
      resourceIcon = resourceIcon1,
      colorPrimary = colorPrimary1,
      dateInstalled = dateInstalled1,
      dateUpdate = dateUpdate1,
      version = version1,
      installedFromGooglePlay = installedFromGooglePlay1),
    Application(
      name = name2,
      packageName = packageName2,
      className = className2,
      resourceIcon = resourceIcon2,
      colorPrimary = colorPrimary2,
      dateInstalled = dateInstalled2,
      dateUpdate = dateUpdate2,
      version = version2,
      installedFromGooglePlay = installedFromGooglePlay2),
    Application(
      name = name3,
      packageName = packageName3,
      className = className3,
      resourceIcon = resourceIcon3,
      colorPrimary = colorPrimary3,
      dateInstalled = dateInstalled3,
      dateUpdate = dateUpdate3,
      version = version3,
      installedFromGooglePlay = installedFromGooglePlay3)
  )

  val apps: Seq[App] = Seq(
    App(
      id = 1,
      name = name1,
      packageName = packageName1,
      className = className1,
      resourceIcon = resourceIcon1,
      colorPrimary = colorPrimary1,
      dateInstalled = dateInstalled1,
      dateUpdate = dateUpdate1,
      version = version1,
      installedFromGooglePlay = installedFromGooglePlay1),
    App(
      id = 2,
      name = name2,
      packageName = packageName2,
      className = className2,
      resourceIcon = resourceIcon2,
      colorPrimary = colorPrimary2,
      dateInstalled = dateInstalled2,
      dateUpdate = dateUpdate2,
      version = version2,
      installedFromGooglePlay = installedFromGooglePlay2),
    App(
      id = 3,
      name = name3,
      packageName = packageName3,
      className = className3,
      resourceIcon = resourceIcon3,
      colorPrimary = colorPrimary3,
      dateInstalled = dateInstalled3,
      dateUpdate = dateUpdate3,
      version = version3,
      installedFromGooglePlay = installedFromGooglePlay3)
  )

  val appDataSeq: Seq[AppData] = Seq(
    AppData(
      name = name1,
      packageName = packageName1,
      className = className1,
      resourceIcon = resourceIcon1,
      colorPrimary = colorPrimary1,
      dateInstalled = dateInstalled1,
      dateUpdate = dateUpdate1,
      version = version1,
      installedFromGooglePlay = installedFromGooglePlay1),
    AppData(
      name = name2,
      packageName = packageName2,
      className = className2,
      resourceIcon = resourceIcon2,
      colorPrimary = colorPrimary2,
      dateInstalled = dateInstalled2,
      dateUpdate = dateUpdate2,
      version = version2,
      installedFromGooglePlay = installedFromGooglePlay2),
    AppData(
      name = name3,
      packageName = packageName3,
      className = className3,
      resourceIcon = resourceIcon3,
      colorPrimary = colorPrimary3,
      dateInstalled = dateInstalled3,
      dateUpdate = dateUpdate3,
      version = version3,
      installedFromGooglePlay = installedFromGooglePlay3)
  )

  val newCacheCategory = CacheCategory(
    id = 4,
    packageName = packageName4,
    category = "SOCIAL",
    starRating = 4.5,
    numDownloads = "+100.000",
    ratingsCount = 5000,
    commentCount = 40000)

  val cacheCategories: Seq[CacheCategory] = Seq(
    CacheCategory(
      id = 1,
      packageName = packageName1,
      category = "SOCIAL",
      starRating = 3.5,
      numDownloads = "+500.000",
      ratingsCount = 78000,
      commentCount = 5000),
    CacheCategory(
      id = 2,
      packageName = packageName2,
      category = "COMMUNICATION",
      starRating = 4.5,
      numDownloads = "+100.000",
      ratingsCount = 5000,
      commentCount = 40000),
    CacheCategory(
      id = 3,
      packageName = packageName3,
      category = "TOOLS",
      starRating = 4,
      numDownloads = "+10.000.000",
      ratingsCount = 2300,
      commentCount = 34000))

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
      path = path3))

  val appCategorizedNoCached = AppCategorized(
    name = name4,
    packageName = packageName4,
    className = className4,
    imagePath = Option(path4),
    category = Option("SOCIAL"),
    starRating = Option(4.5),
    numDownloads = Option("+100.000"),
    ratingsCount = Option(5000),
    commentCount = Option(40000))

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
      commentCount = Option(5000)),
    AppCategorized(
      name = name2,
      packageName = packageName2,
      className = className2,
      imagePath = Option(path2),
      category = Option("COMMUNICATION"),
      starRating = Option(4.5),
      numDownloads = Option("+100.000"),
      ratingsCount = Option(5000),
      commentCount = Option(40000)),
    AppCategorized(
      name = name3,
      packageName = packageName3,
      className = className3,
      imagePath = Option(path3),
      category = Option("TOOLS"),
      starRating = Option(4),
      numDownloads = Option("+10.000.000"),
      ratingsCount = Option(2300),
      commentCount = Option(34000)))

  val googlePlaySimplePackageNoCached = GooglePlaySimplePackage(
    packageName = packageName4,
    appType = "APP",
    appCategory = "SOCIAL",
    starRating = 4.5,
    numDownloads = "+100.000",
    ratingCount = 5000,
    commentCount = 40000)

  val requestConfig = RequestConfig("fake-device-id", "fake-token")

  val packageNameForCreateImage = "com.example"

  val pathForCreateImage = "/example/for/create/image"

  val urlForCreateImage = "http://www.w.com/image.jpg"

  val appWebsitePath = AppWebsitePath(
    packageName = packageNameForCreateImage,
    url = urlForCreateImage,
    path = pathForCreateImage)

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
      details = GooglePlayDetails(GooglePlayAppDetails(Seq("SOCIAL"), "", None, None, None, Option(1), Option("1"), None, Seq.empty)),
      offer = Seq.empty,
      aggregateRating = GooglePlayAggregateRating(0, None, 0, 0, 0, 0, 0, 0)))

  val shortcuts: Seq[Shortcut] = Seq(
    Shortcut(
      title = "Shortcut 1",
      icon = 0,
      name = "Shortcut 1",
      packageName = "com.example.shortcut1"),
    Shortcut(
      title = "Shortcut 2",
      icon = 0,
      name = "Shortcut 2",
      packageName = "com.example.shortcut2"),
    Shortcut(
      title = "Shortcut 3",
      icon = 0,
      name = "Shortcut 3",
      packageName = "com.example.shortcut3"))

  val contacts: Seq[Contact] = Seq(
    Contact(
      name = "Contact 1",
      lookupKey = "lookupKey 1",
      photoUri = "photoUri 2",
      hasPhone = false,
      favorite = false,
      info = None),
    Contact(
      name = "Contact 2",
      lookupKey = "lookupKey 2",
      photoUri = "photoUri 2",
      hasPhone = false,
      favorite = false,
      info = None),
    Contact(
      name = "Contact 3",
      lookupKey = "lookupKey 3",
      photoUri = "photoUri 3",
      hasPhone = false,
      favorite = false,
      info = None))

  val pathShortcut = "/example/shortcut"

  val nameShortcut = "aeiou-12345"

  val fileNameShortcut = s"$pathShortcut/$nameShortcut"

  val lookupKey = "lookupKey 1"

  val contact = Contact(
    name = "Simple Contact",
    lookupKey = lookupKey,
    photoUri = "photoUri 1",
    hasPhone = true,
    favorite = false,
    info = Some(
      ContactInfo(
        emails = Seq(
          ContactEmail(
            address = "sample1@47deg.com",
            category = EmailHome
          )
        ),
        phones = Seq(
          ContactPhone(
            number = "+00 111 222 333",
            category = PhoneHome
          ),
          ContactPhone(
            number = "+00 444 555 666",
            category = PhoneMobile
          )
        )
      )
    ))

}
