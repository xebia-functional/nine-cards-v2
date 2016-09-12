package com.fortysevendeg.ninecardslauncher.services.awareness.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.awareness._
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.snapshot.{DetectedActivityResult, HeadphoneStateResult, LocationResult, WeatherResult}
import com.google.android.gms.awareness.state.{HeadphoneState, Weather}
import com.google.android.gms.common.api.{GoogleApiClient, ResultCallback}

import scalaz.\/
import scalaz.concurrent.Task

class AwarenessServicesImpl(client: GoogleApiClient)
  extends AwarenessServices {

  override def getTypeActivity =
    TaskService {
      Task.async[AwarenessException Xor TypeActivity] { handler =>

        Awareness.SnapshotApi.getDetectedActivity(client)
          .setResultCallback(new ResultCallback[DetectedActivityResult]() {
            override def onResult(detectedActivityResult: DetectedActivityResult): Unit = {
              Option(detectedActivityResult) match {
                case Some(result) if result.getStatus.isSuccess =>
                  Option(result.getActivityRecognitionResult) match {
                    case Some(recognition) if Option(recognition.getMostProbableActivity).isDefined =>
                      handler(\/.right(Xor.right(TypeActivity(recognition.getMostProbableActivity.getType))))
                    case _ => handler(\/.right(Xor.left(AwarenessException("Most probable activity not found"))))
                  }
                case _ =>
                  handler(\/.right(Xor.left(AwarenessException("Detected activity not found"))))
              }
            }
          })
      }
    }

  override def getHeadphonesState =
    TaskService {
      Task.async[AwarenessException Xor HeadphonesState] { handler =>

        Awareness.SnapshotApi.getHeadphoneState(client)
          .setResultCallback(new ResultCallback[HeadphoneStateResult]() {
            override def onResult(headphoneStateResult: HeadphoneStateResult): Unit = {
              Option(headphoneStateResult) match {
                case Some(result) if result.getStatus.isSuccess =>
                  Option(result.getHeadphoneState) match {
                    case Some(headphoneState) =>
                      handler(\/.right(Xor.right(HeadphonesState(headphoneState.getState == HeadphoneState.PLUGGED_IN))))
                    case _ =>
                      handler(\/.right(Xor.left(AwarenessException("Headphone state not found"))))
                  }
                case _ =>
                  handler(\/.right(Xor.left(AwarenessException("Headphone result not found"))))
              }
            }
          })

      }
    }

  override def getLocation =
    TaskService {
      Task.async[AwarenessException Xor LocationState] { handler =>

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
                      handler(\/.right(Xor.right(locationState)))
                    case _ =>
                      handler(\/.right(Xor.left(AwarenessException("Location not found"))))
                  }
                case _ =>
                  handler(\/.right(Xor.left(AwarenessException("Location result not found"))))
              }
            }

          })

      }
    }

  override def getWeather =
    TaskService {
      Task.async[AwarenessException Xor WeatherState] { handler =>

        Awareness.SnapshotApi.getWeather(client)
          .setResultCallback(new ResultCallback[WeatherResult]() {
            override def onResult(weatherResult: WeatherResult): Unit = {
              Option(weatherResult) match {
                case Some(result) if result.getStatus.isSuccess =>
                  Option(weatherResult.getWeather) match {
                    case Some(weather) =>
                      val weatherState = WeatherState(
                        conditions = weather.getConditions,
                        humidity = weather.getHumidity,
                        dewPointCelsius = weather.getDewPoint(Weather.CELSIUS),
                        dewPointFahrenheit = weather.getDewPoint(Weather.FAHRENHEIT),
                        temperatureCelsius = weather.getTemperature(Weather.CELSIUS),
                        temperatureFahrenheit = weather.getTemperature(Weather.FAHRENHEIT)
                      )
                      handler(\/.right(Xor.right(weatherState)))
                    case _ =>
                      handler(\/.right(Xor.left(AwarenessException("Weather not found"))))
                  }
                case _ =>
                  handler(\/.right(Xor.left(AwarenessException("Weather result not found"))))
              }
            }

          })

      }
    }

}
