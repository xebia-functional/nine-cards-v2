package cards.nine.process.device.impl

import cards.nine.commons._
import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.models.Application
import cards.nine.models.types._
import cards.nine.process.commons.NineCardIntentConversions
import cards.nine.process.commons.models.NineCardIntent
import cards.nine.process.commons.models.NineCardIntentImplicits._
import cards.nine.process.device.SaveDockAppRequest
import cards.nine.process.device.models.{App, CallData, LastCallsContact, Widget, _}
import cards.nine.process.device.types._
import cards.nine.repository.model.{App => RepositoryApp}
import cards.nine.services.api.{CategorizedPackage, RequestConfig}
import cards.nine.services.calls.models.{Call => ServicesCall}
import cards.nine.services.contacts.models.{Contact, ContactEmail, ContactInfo, ContactPhone, _}
import cards.nine.services.image.{AppPackagePath, AppWebsitePath}
import cards.nine.services.persistence.models.{App => ServicesApp, DataCounter => ServicesDataCounter, DockApp => ServicesDockApp, IterableApps => ServicesIterableApps}
import cards.nine.services.shortcuts.models.Shortcut
import cards.nine.services.widgets.models.{Widget => ServicesWidget}
import play.api.libs.json.Json

trait DeviceProcessData
  extends NineCardIntentConversions {

  val statusCodeOk = 200
  val items = 5

  val size = 4

  val name1 = "Scala Android"
  val packageName1 = "com.fortysevendeg.scala.android"
  val className1 = "ScalaAndroidActivity"
  val path1 = "/example/path1"
  val category1 = Game
  val imagePath1 = "imagePath1"
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
  val resizeMode1 = 1
  val updatePeriodMillis1 = 1
  val label1 = "label1"
  val preview1: Int = 1

  val userHashCodeOption1 = Option(userHashCode1)

  val userHashCode2 = 2
  val autoAdvanceViewId2 = 2
  val initialLayout2 = 2
  val minHeight2 = 110
  val minResizeHeight2 = 110
  val minResizeWidth2 = 110
  val minWidth2 = 110
  val resizeMode2 = 2
  val updatePeriodMillis2 = 2
  val label2 = "label2"
  val preview2: Int = 2

  val userHashCodeOption2 = Option(userHashCode2)

  val userHashCode3 = 3
  val autoAdvanceViewId3 = 3
  val initialLayout3 = 3
  val minHeight3 = 180
  val minResizeHeight3 = 180
  val minResizeWidth3 = 180
  val minWidth3 = 180
  val resizeMode3 = 3
  val updatePeriodMillis3 = 3
  val label3 = "label3"
  val preview3: Int = 3

  val userHashCodeOption3 = Option(userHashCode3)

  val phoneNumber1 = "+00 111 222 333"
  val contactName1 = "Contact 1"
  val numberType1 = PhoneHome
  val date1 = 3L
  val callType1 = 1
  val lookupKey1 = "lookupKey 1"
  val photoUri1 = "photoUri 1"

  val phoneNumber2 = "+00 444 555 666"
  val contactName2 = "Contact 2"
  val numberType2 = PhoneWork
  val date2 = 2L
  val callType2 = 2
  val lookupKey2 = "lookupKey 2"
  val photoUri2 = "photoUri 2"

  val phoneNumber3 = "+00 777 888 999"
  val contactName3 = "Contact 3"
  val numberType3 = PhoneOther
  val date3 = 1L
  val callType3 = 3
  val lookupKey3 = "lookupKey 3"
  val photoUri3 = "photoUri 3"

  val dockAppsRemoved = 4

  val category = "GAME"

  val dockType = AppDockType
  val dockTypeName = AppDockType.name

  val applicationNoCached = Application(
    name = name4,
    packageName = packageName4,
    className = className4,
    dateInstalled = dateInstalled4,
    dateUpdate = dateUpdate4,
    version = version4,
    installedFromGooglePlay = installedFromGooglePlay4)

  val applications: Seq[Application] = Seq(
    Application(
      name = name1,
      packageName = packageName1,
      className = className1,
      dateInstalled = dateInstalled1,
      dateUpdate = dateUpdate1,
      version = version1,
      installedFromGooglePlay = installedFromGooglePlay1),
    Application(
      name = name2,
      packageName = packageName2,
      className = className2,
      dateInstalled = dateInstalled2,
      dateUpdate = dateUpdate2,
      version = version2,
      installedFromGooglePlay = installedFromGooglePlay2),
    Application(
      name = name3,
      packageName = packageName3,
      className = className3,
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
      dateInstalled = dateInstalled1,
      dateUpdate = dateUpdate1,
      version = version1,
      installedFromGooglePlay = installedFromGooglePlay1),
    App(
      name = name2,
      packageName = packageName2,
      className = className2,
      category = category2,
      dateInstalled = dateInstalled2,
      dateUpdate = dateUpdate2,
      version = version2,
      installedFromGooglePlay = installedFromGooglePlay2),
    App(
      name = name3,
      packageName = packageName3,
      className = className3,
      category = category3,
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

  val requestConfig = RequestConfig("fake-api-key", "fake-session-token", "fake-android-id", Some("fake-android-token"))

  val packageNameForCreateImage = "com.example"

  val pathForCreateImage = "/example/for/create/image"

  val urlForCreateImage = "http://www.w.com/image.jpg"

  val appWebsitePath = AppWebsitePath(
    packageName = packageNameForCreateImage,
    url = urlForCreateImage,
    path = pathForCreateImage)

  val categorizedPackage = CategorizedPackage(
    packageName = packageNameForCreateImage,
    category = Some("SOCIAL"))

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
            category = EmailHome
          )
        ),
        phones = Seq(
          ContactPhone(
            number = phoneNumber1,
            category = PhoneHome
          ),
          ContactPhone(
            number = phoneNumber2,
            category = PhoneMobile
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
      preview = preview1),
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
      preview = preview2),
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
      preview = preview3)
  )

  val widgets: Seq[Widget] = Seq(
    Widget(
      userHashCode = userHashCodeOption1,
      autoAdvanceViewId = autoAdvanceViewId1,
      initialLayout = initialLayout1,
      minWidth = minWidth1,
      minHeight = minWidth1,
      minResizeWidth = minWidth1,
      minResizeHeight = minWidth1,
      className = className1,
      packageName = packageName1,
      resizeMode = WidgetResizeMode(resizeMode1),
      updatePeriodMillis = updatePeriodMillis1,
      label = label1,
      preview = preview1),
    Widget(
      userHashCode = userHashCodeOption2,
      autoAdvanceViewId = autoAdvanceViewId2,
      initialLayout = initialLayout2,
      minWidth = minWidth2,
      minHeight = minWidth2,
      minResizeWidth = minWidth2,
      minResizeHeight = minWidth2,
      className = className2,
      packageName = packageName2,
      resizeMode = WidgetResizeMode(resizeMode2),
      updatePeriodMillis = updatePeriodMillis2,
      label = label2,
      preview = preview2),
    Widget(
      userHashCode = userHashCodeOption3,
      autoAdvanceViewId = autoAdvanceViewId3,
      initialLayout = initialLayout3,
      minWidth = minWidth3,
      minHeight = minWidth3,
      minResizeWidth = minWidth3,
      minResizeHeight = minWidth3,
      className = className3,
      packageName = packageName3,
      resizeMode = WidgetResizeMode(resizeMode3),
      updatePeriodMillis = updatePeriodMillis3,
      label = label3,
      preview = preview3)
  )

  val appWithWidgets = widgets map { widget =>
    AppsWithWidgets(
      name = widget.packageName match {
        case `packageName1` => name1
        case `packageName2` => name2
        case `packageName3` => name3
        case _ => ""
      },
      packageName = widget.packageName,
      widgets = Seq(widget))
  }

  val callsServices: Seq[ServicesCall] = Seq(
    ServicesCall(
      number = phoneNumber1,
      name = Some(contactName1),
      numberType = PhoneMobile,
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

  def createDockAppServiceSeq(
    num: Int = 4,
    id: Int = 0,
    name: String = name1,
    dockType: String = dockTypeName,
    intent: String = intentStr,
    imagePath: String = imagePath1,
    position: Int = 0) =
    (0 until num) map (item =>
      ServicesDockApp(
        id = id + num,
        name = name,
        dockType = dockType,
        intent = intent,
        imagePath = imagePath,
        position = position + num))

  def createDockAppProcessSeq(
    num: Int = 4,
    name: String = name1,
    dockType: String = dockTypeName,
    intent: String = intentStr,
    imagePath: String = imagePath1,
    position: Int = 0) =
    (0 until num) map (item =>
      DockApp(
        name = name,
        dockType = DockType(dockType),
        intent = jsonToNineCardIntent(intent),
        imagePath = imagePath,
        position = position + num))

  def createSaveDockAppRequestSeq(
     num: Int = 4,
     name: String = name1,
     dockType: DockType = dockType,
     intent: String = intentStr,
     imagePath: String = imagePath1,
     position: Int = 0) =
    (0 until num) map (item =>
      SaveDockAppRequest(
        name = name,
        dockType = dockType,
        intent = intent,
        imagePath = imagePath,
        position = position + num))

  val dockAppSeq = createDockAppServiceSeq()
  val dockApp1 = dockAppSeq(0)
  val dockAppProcessSeq = createDockAppProcessSeq()
  val dockAppProcess1 = dockAppProcessSeq(0)
  val saveDockAppRequestSeq = createSaveDockAppRequestSeq()

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

  val networks = 0 to 10 map (c => s"Networks $c")

}
