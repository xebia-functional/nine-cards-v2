package cards.nine.services.awareness.impl

import android.app.PendingIntent
import android.content.{BroadcastReceiver, Intent, IntentFilter}
import android.location.{Address, Geocoder}
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models._
import cards.nine.models.types._
import cards.nine.services.awareness._
import cats.syntax.either._
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.{AwarenessFence, DetectedActivityFence, FenceUpdateRequest, HeadphoneFence}
import com.google.android.gms.awareness.snapshot.{DetectedActivityResult, HeadphoneStateResult, LocationResult, WeatherResult}
import com.google.android.gms.awareness.state.{HeadphoneState, Weather}
import com.google.android.gms.common.api.{GoogleApiClient, ResultCallback, Status}
import monix.eval.Task
import monix.execution.Cancelable

import scala.util.Success

class GoogleAwarenessServicesImpl(client: GoogleApiClient)
  extends AwarenessServices {

  val fenceReceiverAction = "FENCE_RECEIVER_ACTION"

  override def getTypeActivity =
    TaskService {
      Task.async[AwarenessException Either ProbablyActivity] { (scheduler, callback) =>
        Awareness.SnapshotApi.getDetectedActivity(client)
          .setResultCallback(new ResultCallback[DetectedActivityResult]() {
            override def onResult(detectedActivityResult: DetectedActivityResult): Unit = {
              Option(detectedActivityResult) match {
                case Some(result) if result.getStatus.isSuccess =>
                  Option(result.getActivityRecognitionResult) match {
                    case Some(recognition) if Option(recognition.getMostProbableActivity).isDefined =>
                      callback(Success(Either.right(ProbablyActivity(KindActivity(recognition.getMostProbableActivity.getType)))))
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

  override def registerFenceUpdates(fences: Seq[AwarenessFenceUpdate], receiver: BroadcastReceiver)(implicit contextSupport: ContextSupport) = {

    def registerReceiver: TaskService[Unit] = TaskService {
      Task {
        Either
          .catchNonFatal(contextSupport.context.registerReceiver(receiver, new IntentFilter(fenceReceiverAction)))
          .map(_ => (): Unit)
          .leftMap(e => AwarenessException(e.getMessage, Some(e)))
      }
    }

    def registerIntent: TaskService[Unit] = {

      val pendingIntent = PendingIntent.getBroadcast(contextSupport.context, 1001, new Intent(fenceReceiverAction), 0)
      val builder = new FenceUpdateRequest.Builder()
      fences flatMap toAPIFence foreach {
        case (apiFence, key) => builder.addFence(key, apiFence, pendingIntent)
      }
      val request = builder.build()

      TaskService {
        Task.async[AwarenessException Either Unit] { (scheduler, callback) =>
          Awareness.FenceApi.updateFences(client, request)
            .setResultCallback(new ResultCallback[Status] {
              override def onResult(r: Status): Unit = {
                Option(r) match {
                  case Some(result) if result.getStatus.isSuccess => callback(Success(Either.right((): Unit)))
                  case _ => callback(Success(Either.left(AwarenessException("Can't register the fence updates"))))
                }
              }
            })
          Cancelable.empty
        }
      }
    }

    for {
      _ <- registerReceiver
      _ <- registerIntent
    } yield ()
  }

  override def unregisterFenceUpdates(implicit contextSupport: ContextSupport) = {

    val request = new FenceUpdateRequest.Builder()
      .removeFence(PendingIntent.getBroadcast(contextSupport.context, 1001, new Intent(fenceReceiverAction), 0))
      .build()

    TaskService {
      Task.async[AwarenessException Either Unit] { (scheduler, callback) =>
        Awareness.FenceApi.updateFences(client, request)
          .setResultCallback(new ResultCallback[Status] {
            override def onResult(r: Status): Unit = {
              Option(r) match {
                case Some(result) if result.getStatus.isSuccess => callback(Success(Either.right((): Unit)))
                case _ => callback(Success(Either.left(AwarenessException("Can't register the fence updates"))))
              }
            }
          })
        Cancelable.empty
      }
    }
  }

  private[this] def toAPIFence(awarenessFence: AwarenessFenceUpdate): Seq[(AwarenessFence, String)] =
    awarenessFence match {
      case HeadphonesFence =>
        Seq(
          (HeadphoneFence.during(HeadphoneState.PLUGGED_IN), s"${HeadphonesFence.key}_IN"),
          (HeadphoneFence.during(HeadphoneState.UNPLUGGED), s"${HeadphonesFence.key}_IN"))
      case InVehicleFence =>
        Seq((DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE), InVehicleFence.key))
      case OnBicycleFence =>
        Seq((DetectedActivityFence.during(DetectedActivityFence.ON_BICYCLE), OnBicycleFence.key))
      case RunningFence =>
        Seq((DetectedActivityFence.during(DetectedActivityFence.RUNNING), RunningFence.key))
    }

  override def getHeadphonesState =
    TaskService {
      Task.async[AwarenessException Either Headphones] { (scheduler, callback) =>
        Awareness.SnapshotApi.getHeadphoneState(client)
          .setResultCallback(new ResultCallback[HeadphoneStateResult]() {
            override def onResult(headphoneStateResult: HeadphoneStateResult): Unit = {
              Option(headphoneStateResult) match {
                case Some(result) if result.getStatus.isSuccess =>
                  Option(result.getHeadphoneState) match {
                    case Some(headphoneState) =>
                      callback(Success(Either.right(Headphones(headphoneState.getState == HeadphoneState.PLUGGED_IN))))
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

  override def getLocation(implicit contextSupport: ContextSupport): TaskService[Location] = {

    def getCurrentLocation =
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

    def loadAddress(locationState: LocationState) =
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
      location <- loadAddress(locationState)
    } yield location
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
                        temperatureFahrenheit = weather.getTemperature(Weather.FAHRENHEIT))
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

  private[this] def toAwarenessLocation(address: Address) =
    Location(
      latitude = address.getLatitude,
      longitude = address.getLongitude,
      countryCode = Option(address.getCountryCode),
      countryName = Option(address.getCountryName),
      addressLines = toAddressLines(address))

  private[this] def toAddressLines(address: Address) = 0 to address.getMaxAddressLineIndex flatMap { index =>
    Option(address.getAddressLine(index))
  }

}
