package cards.nine.app.ui.launcher.jobs

import android.graphics.Color
import cards.nine.app.ui.components.models.{LauncherData, LauncherMoment, MomentWorkSpace}
import cards.nine.commons.test.data.{ApplicationTestData, DeviceTestData}
import cards.nine.models._
import cards.nine.models.types.theme._
import cards.nine.models.types.{ClearCondition, CloudyCondition, FoggyCondition, NineCardsMoment}
import cards.nine.services.persistence.conversions.AppConversions

import scala.util.Random

trait LauncherTestData
  extends DeviceTestData
  with ApplicationTestData
  with AppConversions {

  val idWidget = 1
  val appWidgetId = 1

  val launcherMoment = LauncherMoment(momentType = Option(NineCardsMoment.defaultMoment), collection = None)

  def launcherData(num: Int = 0) =
    LauncherData(
      workSpaceType = MomentWorkSpace,
      moment = Option(launcherMoment),
      collections = Seq.empty,
      positionByType = 0 + num)

  val launcherData: LauncherData = launcherData(0)
  val seqLauncherData: Seq[LauncherData] = Seq(launcherData(0), launcherData(1), launcherData(2))

  val numberPhone = "123456789"
  val packageName = "packageName"
  val errorMenu = 0

  val keyword: String = "keyword"
  val querry: String = "querry"

  val position: Int = 1
  val positionFrom: Int = 1
  val positionFromNoExist: Int = 50
  val positionTo: Int = 2

  val theme = NineCardsTheme(
    name = "light",
    parent = ThemeLight,
    styles = Seq.empty,
    themeColors = ThemeColors(Color.parseColor("#FF9800"), Seq.empty))

  val humidity = Random.nextInt(100)
  val dewPointCelsius = Random.nextFloat()
  val dewPointFahrenheit = Random.nextFloat()
  val temperatureCelsius = Random.nextFloat()
  val temperatureFahrenheit = Random.nextFloat()
  val conditionsServices = Seq(
    ClearCondition,
    CloudyCondition,
    FoggyCondition)

  val weatherState = WeatherState(
    conditions = conditionsServices,
    humidity = humidity,
    dewPointCelsius = dewPointCelsius,
    dewPointFahrenheit = dewPointFahrenheit,
    temperatureCelsius = temperatureCelsius,
    temperatureFahrenheit = temperatureFahrenheit)

  val lastPhone = "lastPhone"


}
