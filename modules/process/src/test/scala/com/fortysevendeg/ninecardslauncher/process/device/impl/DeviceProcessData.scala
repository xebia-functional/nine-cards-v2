package com.fortysevendeg.ninecardslauncher.process.device.impl

import android.graphics.drawable.Drawable
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, CallData, LastCallsContact, Widget, WidgetDimensions, _}
import com.fortysevendeg.ninecardslauncher.process.device.types._
import com.fortysevendeg.ninecardslauncher.repository.model.{App => RepositoryApp}
import com.fortysevendeg.ninecardslauncher.services.api.RequestConfig
import com.fortysevendeg.ninecardslauncher.services.api.models._
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.calls.models.{Call => ServicesCall}
import com.fortysevendeg.ninecardslauncher.services.commons._
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact, ContactEmail, ContactInfo, ContactPhone, _}
import com.fortysevendeg.ninecardslauncher.services.image.{AppPackagePath, AppWebsitePath}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{
  App => ServicesApp,
  DockApp=> ServicesDockApp,
  DataCounter=> ServicesDataCounter,
  IterableApps => ServicesIterableApps}
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.Shortcut
import com.fortysevendeg.ninecardslauncher.services.widgets.models.{Widget => ServicesWidget}
import play.api.libs.json.Json
import com.fortysevendeg.ninecardslauncher.services.commons.{
  EmailCategory => ServicesEmailCategory,
  EmailHome => ServicesEmailHome,
  EmailWork => ServicesEmailWork,
  EmailOther => ServicesEmailOther}
import com.fortysevendeg.ninecardslauncher.services.commons.{
  PhoneCategory => ServicesPhoneCategory,
  PhoneHome => ServicesPhoneHome,
  PhoneWork => ServicesPhoneWork,
  PhoneMobile=> ServicesPhoneMobile,
  PhoneMain => ServicesPhoneMain,
  PhoneFaxWork => ServicesPhoneFaxWork,
  PhoneFaxHome=> ServicesPhoneFaxHome,
  PhonePager => ServicesPhonePager,
  PhoneOther => ServicesPhoneOther}

trait DeviceProcessData
  extends NineCardIntentConversions {

  val statusCodeOk = 200
  val items = 5

  val name1 = "Scala Android"
  val packageName1 = "com.fortysevendeg.scala.android"
  val className1 = "ScalaAndroidActivity"
  val path1 = "/example/path1"
  val category1 = Game
  val imagePath1 = "imagePath1"
  val resourceIcon1 = 1
  val colorPrimary1 = "colorPrimary"
  val dateInstalled1 = 1L
  val dateUpdate1 = 1L
  val version1 = "22"
  val installedFromGooglePlay1 = true

  val name2 = "Example"
  val packageName2 = "com.fortysevendeg.example"
  val className2 = "ExampleActivity"
  val path2 = "/example/path2"
  val category2 = BooksAndReference
  val imagePath2 = "imagePath1"
  val resourceIcon2 = 2
  val colorPrimary2 = "colorPrimary"
  val dateInstalled2 = 1L
  val dateUpdate2 = 1L
  val version2 = "22"
  val installedFromGooglePlay2 = true

  val name3 = "Scala Api"
  val packageName3 = "com.fortysevendeg.scala.api"
  val className3 = "ScalaApiActivity"
  val path3 = "/example/path3"
  val category3 = Business
  val imagePath3 = "imagePath1"
  val resourceIcon3 = 3
  val colorPrimary3 = "colorPrimary"
  val dateInstalled3 = 1L
  val dateUpdate3 = 1L
  val version3 = "22"
  val installedFromGooglePlay3 = true

  val name4 = "Last App"
  val packageName4 = "com.fortysevendeg.scala.last"
  val className4 = "LastAppActivity"
  val path4 = "/example/path4"
  val category4 = Comics
  val imagePath4 = "imagePath1"
  val resourceIcon4 = 4
  val colorPrimary4 = "colorPrimary"
  val dateInstalled4 = 1L
  val dateUpdate4 = 1L
  val version4 = "22"
  val installedFromGooglePlay4 = true

  val userHashCode1 = 1
  val autoAdvanceViewId1 = 1
  val initialLayout1 = 1
  val minHeight1 = 40
  val minResizeHeight1 = 40
  val minResizeWidth1 = 40
  val minWidth1 = 40
  val numCells1 = 1
  val resizeMode1 = 1
  val updatePeriodMillis1 = 1
  val label1 = "label1"
  val icon1: Drawable = new TestDrawable
  val preview1: Drawable = new TestDrawable

  val userHashCodeOption1 = Option(userHashCode1)
  val previewOption1 = Option(preview1)

  val userHashCode2 = 2
  val autoAdvanceViewId2 = 2
  val initialLayout2 = 2
  val minHeight2 = 110
  val minResizeHeight2 = 110
  val minResizeWidth2 = 110
  val minWidth2 = 110
  val numCells2 = 2
  val resizeMode2 = 2
  val updatePeriodMillis2 = 2
  val label2 = "label2"
  val icon2: Drawable = new TestDrawable
  val preview2: Drawable = new TestDrawable

  val userHashCodeOption2 = Option(userHashCode2)
  val previewOption2 = Option(preview2)

  val userHashCode3 = 3
  val autoAdvanceViewId3 = 3
  val initialLayout3 = 3
  val minHeight3 = 180
  val minResizeHeight3 = 180
  val minResizeWidth3 = 180
  val minWidth3 = 180
  val numCells3 = 3
  val resizeMode3 = 3
  val updatePeriodMillis3 = 3
  val label3 = "label3"
  val icon3: Drawable = new TestDrawable
  val preview3: Drawable = new TestDrawable

  val userHashCodeOption3 = Option(userHashCode3)
  val previewOption3 = Option(preview3)

  val phoneNumber1 = "+00 111 222 333"
  val contactName1 = "Contact 1"
  val numberType1 = ServicesPhoneHome
  val date1 = 3L
  val callType1 = 1
  val lookupKey1 = "lookupKey 1"
  val photoUri1 = "photoUri 1"

  val phoneNumber2 = "+00 444 555 666"
  val contactName2 = "Contact 2"
  val numberType2 = ServicesPhoneWork
  val date2 = 2L
  val callType2 = 2
  val lookupKey2 = "lookupKey 2"
  val photoUri2 = "photoUri 2"

  val phoneNumber3 = "+00 777 888 999"
  val contactName3 = "Contact 3"
  val numberType3 = ServicesPhoneOther
  val date3 = 1L
  val callType3 = 3
  val lookupKey3 = "lookupKey 3"
  val photoUri3 = "photoUri 3"

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
      name = name1,
      packageName = packageName1,
      className = className1,
      category = category1,
      imagePath = imagePath1,
      colorPrimary = colorPrimary1,
      dateInstalled = dateInstalled1,
      dateUpdate = dateUpdate1,
      version = version1,
      installedFromGooglePlay = installedFromGooglePlay1),
    App(
      name = name2,
      packageName = packageName2,
      className = className2,
      category = category2,
      imagePath = imagePath2,
      colorPrimary = colorPrimary2,
      dateInstalled = dateInstalled2,
      dateUpdate = dateUpdate2,
      version = version2,
      installedFromGooglePlay = installedFromGooglePlay2),
    App(
      name = name3,
      packageName = packageName3,
      className = className3,
      category = category3,
      imagePath = imagePath3,
      colorPrimary = colorPrimary3,
      dateInstalled = dateInstalled3,
      dateUpdate = dateUpdate3,
      version = version3,
      installedFromGooglePlay = installedFromGooglePlay3)
  )

  val appsPersistence: Seq[ServicesApp] = Seq(
    ServicesApp(
      id = 1,
      name = name1,
      packageName = packageName1,
      className = className1,
      category = category1.name,
      imagePath = imagePath1,
      colorPrimary = colorPrimary1,
      dateInstalled = dateInstalled1,
      dateUpdate = dateUpdate1,
      version = version1,
      installedFromGooglePlay = installedFromGooglePlay1),
    ServicesApp(
      id = 2,
      name = name2,
      packageName = packageName2,
      className = className2,
      category = category2.name,
      imagePath = imagePath2,
      colorPrimary = colorPrimary2,
      dateInstalled = dateInstalled2,
      dateUpdate = dateUpdate2,
      version = version2,
      installedFromGooglePlay = installedFromGooglePlay2),
    ServicesApp(
      id = 3,
      name = name3,
      packageName = packageName3,
      className = className3,
      category = category3.name,
      imagePath = imagePath3,
      colorPrimary = colorPrimary3,
      dateInstalled = dateInstalled3,
      dateUpdate = dateUpdate3,
      version = version3,
      installedFromGooglePlay = installedFromGooglePlay3)
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
      path = path3))

  val googlePlaySimplePackageNoCached = GooglePlaySimplePackage(
    packageName = packageName4,
    appType = "APP",
    appCategory = "SOCIAL",
    starRating = 4.5,
    numDownloads = "+100.000",
    ratingCount = 5000,
    commentCount = 40000)

  val requestConfig = RequestConfig("fake-device-id", "fake-token", Some("fake-android-token"))

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
      name = contactName1,
      lookupKey = lookupKey1,
      photoUri = photoUri1,
      hasPhone = false,
      favorite = false,
      info = None),
    Contact(
      name = contactName2,
      lookupKey = lookupKey2,
      photoUri = photoUri2,
      hasPhone = false,
      favorite = false,
      info = None),
    Contact(
      name = contactName3,
      lookupKey = lookupKey3,
      photoUri = photoUri3,
      hasPhone = false,
      favorite = false,
      info = None))

  val pathShortcut = "/example/shortcut"

  val nameShortcut = "aeiou-12345"

  val fileNameShortcut = s"$pathShortcut/$nameShortcut"

  val lookupKey = "lookupKey 1"

  val keyword = "keyword"

  val contact = Contact(
    name = "Simple Contact",
    lookupKey = lookupKey,
    photoUri = photoUri1,
    hasPhone = true,
    favorite = false,
    info = Some(
      ContactInfo(
        emails = Seq(
          ContactEmail(
            address = "sample1@47deg.com",
            category = ServicesEmailHome
          )
        ),
        phones = Seq(
          ContactPhone(
            number = phoneNumber1,
            category = ServicesPhoneHome
          ),
          ContactPhone(
            number = phoneNumber2,
            category = ServicesPhoneMobile
          )
        )
      )
    ))

  val widgetsServices: Seq[ServicesWidget] = Seq(
    ServicesWidget(
      userHashCode = userHashCodeOption1,
      autoAdvanceViewId = autoAdvanceViewId1,
      initialLayout = initialLayout1,
      minHeight = minHeight1,
      minResizeHeight = minResizeHeight1,
      minResizeWidth = minResizeWidth1,
      minWidth = minWidth1,
      className = className1,
      packageName = packageName1,
      resizeMode = resizeMode1,
      updatePeriodMillis = updatePeriodMillis1,
      label = label1,
      icon = icon1,
      preview = previewOption1),
    ServicesWidget(
      userHashCode = userHashCodeOption2,
      autoAdvanceViewId = autoAdvanceViewId2,
      initialLayout = initialLayout2,
      minHeight = minHeight2,
      minResizeHeight = minResizeHeight2,
      minResizeWidth = minResizeWidth2,
      minWidth = minWidth2,
      className = className2,
      packageName = packageName2,
      resizeMode = resizeMode2,
      updatePeriodMillis = updatePeriodMillis2,
      label = label2,
      icon = icon2,
      preview = previewOption2),
    ServicesWidget(
      userHashCode = userHashCodeOption3,
      autoAdvanceViewId = autoAdvanceViewId3,
      initialLayout = initialLayout3,
      minHeight = minHeight3,
      minResizeHeight = minResizeHeight3,
      minResizeWidth = minResizeWidth3,
      minWidth = minWidth3,
      className = className3,
      packageName = packageName3,
      resizeMode = resizeMode3,
      updatePeriodMillis = updatePeriodMillis3,
      label = label3,
      icon = icon3,
      preview = previewOption3)
  )

  val widgets: Seq[Widget] = Seq(
    Widget(
      userHashCode = userHashCodeOption1,
      autoAdvanceViewId = autoAdvanceViewId1,
      initialLayout = initialLayout1,
      dimensions = WidgetDimensions(
        minCellHeight = numCells1,
        minResizeCellHeight = numCells1,
        minResizeCellWidth = numCells1,
        minCellWidth = numCells1
      ),
      className = className1,
      packageName = packageName1,
      resizeMode = WidgetResizeMode(resizeMode1),
      updatePeriodMillis = updatePeriodMillis1,
      label = label1,
      icon = icon1,
      preview = previewOption1),
    Widget(
      userHashCode = userHashCodeOption2,
      autoAdvanceViewId = autoAdvanceViewId2,
      initialLayout = initialLayout2,
      dimensions = WidgetDimensions(
        minCellHeight = numCells2,
        minResizeCellHeight = numCells2,
        minResizeCellWidth = numCells2,
        minCellWidth = numCells2
      ),
      className = className2,
      packageName = packageName2,
      resizeMode = WidgetResizeMode(resizeMode2),
      updatePeriodMillis = updatePeriodMillis2,
      label = label2,
      icon = icon2,
      preview = previewOption2),
    Widget(
      userHashCode = userHashCodeOption3,
      autoAdvanceViewId = autoAdvanceViewId3,
      initialLayout = initialLayout3,
      dimensions = WidgetDimensions(
        minCellHeight = numCells3,
        minResizeCellHeight = numCells3,
        minResizeCellWidth = numCells3,
        minCellWidth = numCells3
      ),
      className = className3,
      packageName = packageName3,
      resizeMode = WidgetResizeMode(resizeMode3),
      updatePeriodMillis = updatePeriodMillis3,
      label = label3,
      icon = icon3,
      preview = previewOption3)
  )

  val callsServices: Seq[ServicesCall] = Seq(
    ServicesCall(
      number = phoneNumber1,
      name = Some(contactName1),
      numberType = ServicesPhoneMobile,
      date = date1,
      callType = callType1),
    ServicesCall(
      number = phoneNumber2,
      name = Some(contactName2),
      numberType = numberType2,
      date = date2,
      callType = callType2),
    ServicesCall(
      number = phoneNumber3,
      name = Some(contactName3),
      numberType = numberType3,
      date = date3,
      callType = callType3))

  val callsContacts: Seq[Contact] = Seq(
    Contact(
      name = contactName1,
      lookupKey = lookupKey1,
      photoUri = photoUri1,
      hasPhone = false,
      favorite = false,
      info = None),
    Contact(
      name = contactName2,
      lookupKey = lookupKey2,
      photoUri = photoUri2,
      hasPhone = false,
      favorite = false,
      info = None),
    Contact(
      name = contactName3,
      lookupKey = lookupKey3,
      photoUri = photoUri3,
      hasPhone = false,
      favorite = false,
      info = None))

  val callsData1: Seq[CallData] = Seq(
    CallData(
      date = date1,
      callType = IncomingType))

  val callsData2: Seq[CallData] = Seq(
    CallData(
      date = date2,
      callType = OutgoingType))

  val callsData3: Seq[CallData] = Seq(
    CallData(
      date = date3,
      callType = MissedType))

  val lastCallsContacts: Seq[LastCallsContact] = Seq(
    LastCallsContact(
      hasContact = true,
      number = phoneNumber1,
      title = contactName1,
      photoUri = Some(photoUri1),
      lookupKey = Some(lookupKey1),
      lastCallDate = date1,
      calls = callsData1),
    LastCallsContact(
      hasContact = true,
      number = phoneNumber2,
      title = contactName2,
      photoUri = Some(photoUri2),
      lookupKey = Some(lookupKey2),
      lastCallDate = date2,
      calls = callsData2),
    LastCallsContact(
      hasContact = true,
      number = phoneNumber3,
      title = contactName3,
      photoUri = Some(photoUri3),
      lookupKey = Some(lookupKey3),
      lastCallDate = date3,
      calls = callsData3))

  val intentStr = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val intent = Json.parse(intentStr).as[NineCardIntent]

  val dockApp1 = ServicesDockApp(
    id = 1,
    name = packageName1,
    dockType = AppDockType.name,
    intent = intentStr,
    imagePath = imagePath1,
    position = 0)

  val dockAppSeq = Seq(dockApp1)

  val dockAppProcess1 = DockApp(
    name = dockApp1.name,
    dockType = DockType(dockApp1.dockType),
    intent = jsonToNineCardIntent(dockApp1.intent),
    imagePath = dockApp1.imagePath,
    position = dockApp1.position)

  val dockAppProcessSeq = Seq(dockAppProcess1)

  val iterableCursorContact = new IterableCursor[Contact] {
    override def count(): Int = contacts.length

    override def moveToPosition(pos: Int): Contact = contacts(pos)

    override def close(): Unit = ()
  }

  val iterableContact = new IterableContacts(iterableCursorContact)

  val mockIterableCursor = new IterableCursor[RepositoryApp] {
    override def count(): Int = 0

    override def moveToPosition(pos: Int): RepositoryApp = javaNull

    override def close(): Unit = ()
  }

  val iterableCursorApps = new ServicesIterableApps(mockIterableCursor) {
    override def count(): Int = appsPersistence.length

    override def moveToPosition(pos: Int): ServicesApp = appsPersistence(pos)

    override def close(): Unit = ()
  }

  val iterableApps = new IterableApps(iterableCursorApps)

  val appsCounters = Seq(
    ServicesDataCounter("#", 4),
    ServicesDataCounter("B", 1),
    ServicesDataCounter("E", 6),
    ServicesDataCounter("F", 5),
    ServicesDataCounter("Z", 3))

  val categoryCounters = Seq(
    ServicesDataCounter("COMMUNICATION", 4),
    ServicesDataCounter("GAMES", 1),
    ServicesDataCounter("SOCIAL", 6),
    ServicesDataCounter("TOOLS", 5),
    ServicesDataCounter("WEATHER", 3))

  val contactsCounters = Seq(
    ContactCounter("#", 4),
    ContactCounter("B", 1),
    ContactCounter("E", 6),
    ContactCounter("F", 5),
    ContactCounter("Z", 3))

  val installationAppsCounters = Seq(
    ServicesDataCounter("oneWeek", 4),
    ServicesDataCounter("twoWeeks", 1),
    ServicesDataCounter("oneMonth", 6),
    ServicesDataCounter("twoMonths", 5))

}
