package com.fortysevendeg.ninecardslauncher.services.awareness.impl

import android.location.{Address, Geocoder}
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.services.awareness._
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.snapshot.{DetectedActivityResult, HeadphoneStateResult, LocationResult, WeatherResult}
import com.google.android.gms.awareness.state.{HeadphoneState, Weather}
import com.google.android.gms.common.api.{GoogleApiClient, ResultCallback}
import monix.eval.Task
import monix.execution.Cancelable

import scala.util.Success

class GoogleAwarenessServicesImpl(client: GoogleApiClient)
  extends AwarenessServices {

  override def getTypeActivity =
    TaskService {
      Task.async[AwarenessException Either TypeActivity] { (scheduler, callback) =>
        Awareness.SnapshotApi.getDetectedActivity(client)
          .setResultCallback(new ResultCallback[DetectedActivityResult]() {
            override def onResult(detectedActivityResult: DetectedActivityResult): Unit = {
              Option(detectedActivityResult) match {
                case Some(result) if result.getStatus.isSuccess =>
                  Option(result.getActivityRecognitionResult) match {
                    case Some(recognition) if Option(recognition.getMostProbableActivity).isDefined =>
                      callback(Success(Either.right(TypeActivity(KindActivity(recognition.getMostProbableActivity.getType)))))
                    case _ => callback(Success(Either.left(AwarenessException("Most probable activity not found"))))
                  }
                case _ =>
                  callback(Success(Either.left(AwarenessException("Detected activity not found"))))
              }
            }
          })

        Cancelable.empty
      }
    }

  override def getHeadphonesState =
    TaskService {
      Task.async[AwarenessException Either HeadphonesState] { (scheduler, callback) =>
        Awareness.SnapshotApi.getHeadphoneState(client)
          .setResultCallback(new ResultCallback[HeadphoneStateResult]() {
            override def onResult(headphoneStateResult: HeadphoneStateResult): Unit = {
              Option(headphoneStateResult) match {
                case Some(result) if result.getStatus.isSuccess =>
                  Option(result.getHeadphoneState) match {
                    case Some(headphoneState) =>
                      callback(Success(Either.right(HeadphonesState(headphoneState.getState == HeadphoneState.PLUGGED_IN))))
                    case _ =>
                      callback(Success(Either.left(AwarenessException("Headphone state not found"))))
                  }
                case _ =>
                  callback(Success(Either.left(AwarenessException("Headphone result not found"))))
              }
            }
          })
        Cancelable.empty
      }
    }

  override def getLocation(implicit contextSupport: ContextSupport): TaskService[AwarenessLocation] = {

    def loadAddress(locationState: LocationState) =
      TaskService {
        Task {
          Either.catchNonFatal {
            val address = new Geocoder(contextSupport.context)
              .getFromLocation(locationState.latitude, locationState.longitude, 1)
            Option(address) match {
              case Some(list) if list.size() > 0 => toAwarenessLocation(list.get(0))
              case None => throw new IllegalStateException("Geocoder doesn't return a valid address")
            }
          }.leftMap {
            case e => AwarenessException(e.getMessage, Some(e))
          }
        }
      }

    for {
      locationState <- getCurrentLocation
      location <- loadAddress(locationState)
    } yield location
  }

  override def getCountryLocation(implicit contextSupport: ContextSupport): TaskService[AwarenessLocation] = {

    def loadCountryLocation(locationState: LocationState) =
      TaskService {
        Task {
          Either.catchNonFatal {
            val addressList = new Geocoder(contextSupport.context)
              .getFromLocation(locationState.latitude, locationState.longitude, 1)
            Option(addressList) match {
              case Some(list) if list.size() > 0 => toAwarenessLocation(list.get(0))
              case None => throw new IllegalStateException("Geocoder doesn't return a valid address")
            }
          }.leftMap {
            case e => AwarenessException(e.getMessage, Some(e))
          }
        }
      }

    for {
      locationState <- getCurrentLocation
      location <- loadCountryLocation(locationState)
    } yield location
  }

  private[this] def toAwarenessLocation(address: Address) =
    AwarenessLocation(
      latitude = address.getLatitude,
      longitude = address.getLongitude,
      countryCode = Option(address.getCountryCode),
      countryName = Option(address.getCountryName),
      addressLines = toAddressLines(address))

  private[this] def toAddressLines(address: Address) = 0 to address.getMaxAddressLineIndex flatMap { index =>
    Option(address.getAddressLine(index))
  }

  private[this] def getCurrentLocation =
    TaskService {
      Task.async[AwarenessException Either LocationState] {(scheduler, callback)  =>
        Awareness.SnapshotApi.getLocation(client)
          .setResultCallback(new ResultCallback[LocationResult]() {
            override def onResult(locationResult: LocationResult): Unit = {
              Option(locationResult) match {
                case Some(result) if result.getStatus.isSuccess =>
                  Option(locationResult.getLocation) match {
                    case Some(location) =>
                      val locationState = LocationState(
                        accuracy = location.getAccuracy,
                        altitude = location.getAltitude,
                        bearing = location.getBearing,
                        latitude = location.getLatitude,
                        longitude = location.getLongitude,
                        speed = location.getSpeed,
                        elapsedTime = location.getElapsedRealtimeNanos,
                        time = location.getTime)
                      callback(Success(Either.right(locationState)))
                    case _ =>
                      callback(Success(Either.left(AwarenessException("Location not found"))))
                  }
                case _ =>
                  callback(Success(Either.left(AwarenessException("Location result not found"))))
              }
            }

          })
        Cancelable.empty
      }
    }

  override def getWeather =
    TaskService {
      Task.async[AwarenessException Either WeatherState] { (scheduler, callback) =>
        Awareness.SnapshotApi.getWeather(client)
          .setResultCallback(new ResultCallback[WeatherResult]() {
            override def onResult(weatherResult: WeatherResult): Unit = {
              Option(weatherResult) match {
                case Some(result) if result.getStatus.isSuccess =>
                  Option(weatherResult.getWeather) match {
                    case Some(weather) =>
                      val weatherState = WeatherState(
                        conditions = weather.getConditions map (ConditionWeather(_)),
                        humidity = weather.getHumidity,
                        dewPointCelsius = weather.getDewPoint(Weather.CELSIUS),
                        dewPointFahrenheit = weather.getDewPoint(Weather.FAHRENHEIT),
                        temperatureCelsius = weather.getTemperature(Weather.CELSIUS),
                        temperatureFahrenheit = weather.getTemperature(Weather.FAHRENHEIT)
                      )
                      callback(Success(Either.right(weatherState)))
                    case _ =>
                      callback(Success(Either.left(AwarenessException("Weather not found"))))
                  }
                case _ =>
                  callback(Success(Either.left(AwarenessException("Weather result not found"))))
              }
            }
          })
        Cancelable.empty

      }
    }

}
