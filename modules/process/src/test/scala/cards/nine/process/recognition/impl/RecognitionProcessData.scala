package cards.nine.process.recognition.impl

import cards.nine.process.recognition._
import cards.nine.services

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

  val kindActivityService = services.awareness.InVehicleActivity

  val kindActivityProcess = InVehicleActivity

  val typeActivity = services.awareness.TypeActivity(
    activityType = kindActivityService)

  val probablyActivity = ProbablyActivity(
    activity = kindActivityProcess)

  val connected = Random.nextBoolean()

  val headphonesState = services.awareness.HeadphonesState(connected = connected)

  val headphones = Headphones(connected = connected)

  val awarenessLocation = services.awareness.AwarenessLocation(
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
    services.awareness.ClearCondition,
    services.awareness.CloudyCondition,
    services.awareness.FoggyCondition)

  val conditionsProcess = Seq(
    ClearCondition,
    CloudyCondition,
    FoggyCondition)

  val weatherState = services.awareness.WeatherState(
    conditions = conditionsServices,
    humidity = humidity,
    dewPointCelsius = dewPointCelsius,
    dewPointFahrenheit = dewPointFahrenheit,
    temperatureCelsius = temperatureCelsius,
    temperatureFahrenheit = temperatureFahrenheit)

  val weather = Weather(
    conditions = conditionsProcess,
    humidity = humidity,
    dewPointCelsius = dewPointCelsius,
    dewPointFahrenheit = dewPointFahrenheit,
    temperatureCelsius = temperatureCelsius,
    temperatureFahrenheit = temperatureFahrenheit)

}
