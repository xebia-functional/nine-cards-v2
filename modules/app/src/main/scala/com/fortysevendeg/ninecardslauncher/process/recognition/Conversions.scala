package com.fortysevendeg.ninecardslauncher.process.recognition

import android.location.Address
import com.fortysevendeg.ninecardslauncher.services.awareness.{HeadphonesState, TypeActivity, WeatherState}

trait Conversions {

  def toProbablyActivity(typeActivity: TypeActivity): ProbablyActivity =
    ProbablyActivity(KindActivity(typeActivity.activityType))

  def toHeadphones(state: HeadphonesState): Headphones =
    Headphones(state.connected)

  def toWeather(weather: WeatherState) =
    Weather(
      conditions = weather.conditions map (ConditionWeather(_)),
      humidity = weather.humidity,
      dewPointCelsius = weather.dewPointCelsius,
      dewPointFahrenheit = weather.dewPointFahrenheit,
      temperatureCelsius = weather.temperatureCelsius,
      temperatureFahrenheit = weather.temperatureFahrenheit)

  def toLocation(address: Address) =
    Location(
      latitude = address.getLatitude,
      longitude = address.getLongitude,
      countryCode = Option(address.getCountryCode),
      countryName = Option(address.getCountryName),
      locale = Option(address.getLocale),
      postalCode = Option(address.getPostalCode),
      locality = Option(address.getLocality),
      subLocality = Option(address.getSubLocality),
      adminArea = Option(address.getAdminArea),
      subAdminArea = Option(address.getSubAdminArea),
      thoroughfare = Option(address.getThoroughfare),
      subThoroughfare = Option(address.getSubThoroughfare),
      addressLines = toAddressLines(address),
      phone = Option(address.getPhone),
      premises = Option(address.getPremises),
      url = Option(address.getUrl))

  def toAddressLines(address: Address) = 0 to address.getMaxAddressLineIndex flatMap { index =>
    Option(address.getAddressLine(index))
  }

}
