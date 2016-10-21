package cards.nine.app.receivers.moments

import android.content.{BroadcastReceiver, Context, Intent}
import android.net.ConnectivityManager
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.models.types.{HeadphonesFence, InVehicleFence, OnBicycleFence, RunningFence}
import com.google.android.gms.awareness.fence.FenceState
import macroid.ContextWrapper
import monix.execution.cancelables.SerialCancelable

import scala.concurrent.duration._
import scala.util.Try

class MomentBroadcastReceiver
  extends BroadcastReceiver {

  import MomentBroadcastReceiver._

  override def onReceive(context: Context, intent: Intent): Unit = {

    implicit val contextWrapper = ContextWrapper(context)

    val connectionStatusChangedJobs = new ConnectionStatusChangedJobs

    def verifyConnectionStatus(maybeNetworkInfo: Option[android.net.NetworkInfo]): Unit =
      maybeNetworkInfo foreach { networkInfo =>
        if (networkInfo.getType == ConnectivityManager.TYPE_WIFI) {
          connectionStatusTaskRef := connectionStatusChangedJobs.connectionStatusChanged().resolveAsyncDelayed(10.seconds)
        }
      }

    def verifyFenceStatus(maybeState: Option[FenceState]): Unit = {
      import FenceState._
      val maybeService = maybeState flatMap { state =>
        Option(state.getFenceKey) match {
          case Some(HeadphonesFence.keyIn) if state.getCurrentState == TRUE =>
            Some(connectionStatusChangedJobs.headphoneStatusChanged(HeadphonesFence.keyIn))
          case Some(HeadphonesFence.keyOut) if state.getCurrentState == TRUE =>
            Some(connectionStatusChangedJobs.headphoneStatusChanged(HeadphonesFence.keyOut))
          case Some(RunningFence.key) if state.getCurrentState == TRUE || state.getPreviousState == TRUE =>
            Some(connectionStatusChangedJobs.runningStatusChanged())
          case Some(InVehicleFence.key) if state.getCurrentState == TRUE || state.getPreviousState == TRUE =>
            Some(connectionStatusChangedJobs.inVehicleStatusChanged())
          case Some(OnBicycleFence.key) if state.getCurrentState == TRUE || state.getPreviousState == TRUE =>
            Some(connectionStatusChangedJobs.onBicycleStatusChanged())
          case _ => None
        }
      }

      maybeService foreach { service =>
        fenceStatusRef := service.resolveAsyncDelayed(1.seconds)
      }
    }

    Option(intent) foreach { i =>
      Option(i.getAction) match {
        case Some(ConnectionStatusChangedJobs.action) =>
          verifyConnectionStatus(Option(i.getParcelableExtra[android.net.NetworkInfo]("networkInfo")))
        case Some(`momentFenceAction`) =>
          verifyFenceStatus(Try(FenceState.extract(i)).toOption)
        case _ =>
      }
    }

  }

}

object MomentBroadcastReceiver {

  val connectionStatusTaskRef = SerialCancelable()

  val fenceStatusRef = SerialCancelable()

  val momentFenceAction = "MOMENT_FENCE_ACTION"

}