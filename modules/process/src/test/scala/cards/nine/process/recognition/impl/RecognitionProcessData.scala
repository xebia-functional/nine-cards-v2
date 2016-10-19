package cards.nine.process.recognition.impl

import cards.nine.models._
import cards.nine.models.types.{ClearCondition, CloudyCondition, FoggyCondition, InVehicleActivity}

import scala.util.Random

trait RecognitionProcessData {

  val latitude = Random.nextDouble()
  val longitude = Random.nextDouble()
  val countryCode = Some("ES")
  val countryName = Some("Spain")
  val addressLines = Seq("street", "city", "postal code")
  
  val humidity = Random.nextInt(100)
  val dewPointCelsius = Random.nextFloat()
  val dewPointFahrenheit = Random.nextFloat()
  val temperatureCelsius = Random.nextFloat()
  val temperatureFahrenheit = Random.nextFloat()

  val kindActivityService = InVehicleActivity

  val kindActivityProcess = InVehicleActivity

  val typeActivity = ProbablyActivity(
    activityType = kindActivityService)

  val probablyActivity = ProbablyActivity(
    activityType = kindActivityProcess)

  val connected = Random.nextBoolean()

  val headphonesState = Headphones(connected = connected)

  val headphones = Headphones(connected = connected)

  val awarenessLocation = Location(
    latitude = latitude,
    longitude = longitude,
    countryCode = countryCode,
    countryName = countryName,
    addressLines = addressLines)

  val location = Location(
    latitude = latitude,
    longitude = longitude,
    countryCode = countryCode,
    countryName = countryName,
    addressLines = addressLines)

  val conditionsServices = Seq(
    ClearCondition,
    CloudyCondition,
    FoggyCondition)

  val conditionsProcess = Seq(
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

  val weather = WeatherState(
    conditions = conditionsProcess,
    humidity = humidity,
    dewPointCelsius = dewPointCelsius,
    dewPointFahrenheit = dewPointFahrenheit,
    temperatureCelsius = temperatureCelsius,
    temperatureFahrenheit = temperatureFahrenheit)

}
