package com.fortysevendeg.ninecardslauncher.process.recognition.impl

import com.fortysevendeg.ninecardslauncher.process.recognition.{Weather, Location, Headphones, ProbablyActivity}
import com.fortysevendeg.ninecardslauncher.services.awareness._

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
  
  val kindActivitySeq = Seq(
    InVehicleActivity,
    OnBicycleActivity,
    OnFootActivity,
    RunningActivity,
    StillActivity,
    TiltingActivity,
    WalkingActivity,
    UnknownActivity)

  val kindActivity = kindActivitySeq(Random.nextInt(kindActivitySeq.size))

  val typeActivity = TypeActivity(
    activityType = kindActivity)

  val probablyActivity = ProbablyActivity(
    activity = kindActivity)

  val connected = Random.nextBoolean()

  val headphonesState = HeadphonesState(connected = connected)

  val headphones = Headphones(connected = connected)

  val awarenessLocation = AwarenessLocation(
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
  
  val conditionWeatherSeq = Seq(
    ClearCondition,
    CloudyCondition,
    FoggyCondition,
    HazyCondition,
    IcyCondition,
    RainyCondition,
    SnowyCondition,
    StormyCondition,
    WindyCondition,
    UnknownCondition)

  def generateConditionWeather(index: Int) = conditionWeatherSeq(index)
  
  val conditions = 0 to 2 map (i => generateConditionWeather(Random.nextInt(conditionWeatherSeq.size)))

  val weatherState = WeatherState(
    conditions = conditions,
    humidity = humidity,
    dewPointCelsius = dewPointCelsius,
    dewPointFahrenheit = dewPointFahrenheit,
    temperatureCelsius = temperatureCelsius,
    temperatureFahrenheit = temperatureFahrenheit)

  val weather = Weather(
    conditions = conditions,
    humidity = humidity,
    dewPointCelsius = dewPointCelsius,
    dewPointFahrenheit = dewPointFahrenheit,
    temperatureCelsius = temperatureCelsius,
    temperatureFahrenheit = temperatureFahrenheit)

}
